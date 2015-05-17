package se.crisp.codekvast.server.codekvast_server.controller;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.Validator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import se.crisp.codekvast.server.codekvast_server.exception.CodekvastException;
import se.crisp.codekvast.server.codekvast_server.messaging.AbstractEventBusSubscriber;
import se.crisp.codekvast.server.codekvast_server.messaging.WebSocketUserPresenceHandler;
import se.crisp.codekvast.server.codekvast_server.model.event.display.WebSocketMessage;
import se.crisp.codekvast.server.codekvast_server.model.event.rest.GetMethodUsageRequest;
import se.crisp.codekvast.server.codekvast_server.model.event.rest.MethodUsageReport;
import se.crisp.codekvast.server.codekvast_server.model.event.rest.MethodUsageReport.MethodUsageReportFormatEnumConverter;
import se.crisp.codekvast.server.codekvast_server.model.event.rest.OrganisationSettings;
import se.crisp.codekvast.server.codekvast_server.service.ReportService;
import se.crisp.codekvast.server.codekvast_server.service.UserService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.Principal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Responsible for serving data to/from the Web UI.
 *
 * @author olle.hallin@crisp.se
 */
@RestController
@Slf4j
public class WebUIController extends AbstractEventBusSubscriber {
    @NonNull
    private final UserService userService;

    @NonNull
    private final ReportService reportService;

    @NonNull
    private final WebSocketUserPresenceHandler webSocketUserPresenceHandler;

    @NonNull
    private final Validator validator;

    @NonNull
    private final MethodUsageReportFormatEnumConverter methodUsageReportFormatEnumConverter;

    private final Map<MethodUsageReport.Format, ContentFormatter> formatters = new HashMap<>();

    {
        formatters.put(MethodUsageReport.Format.CSV, new CsvContentFormatter());
        formatters.put(MethodUsageReport.Format.JSON, new JsonContentFormatter());
    }

    @Inject
    public WebUIController(EventBus eventBus, SimpMessagingTemplate messagingTemplate, UserService userService,
                           ReportService reportService, WebSocketUserPresenceHandler webSocketUserPresenceHandler,
                           Validator validator, MethodUsageReportFormatEnumConverter methodUsageReportFormatEnumConverter) {
        super(eventBus, messagingTemplate);
        this.userService = userService;
        this.reportService = reportService;
        this.webSocketUserPresenceHandler = webSocketUserPresenceHandler;
        this.validator = validator;
        this.methodUsageReportFormatEnumConverter = methodUsageReportFormatEnumConverter;

        checkFormatters();
    }

    private void checkFormatters() {
        for (MethodUsageReport.Format format : MethodUsageReport.Format.values()) {
            if (!formatters.containsKey(format)) {
                throw new IllegalArgumentException("Missing content formatter for " + format);
            }
        }
    }

    @InitBinder
    private void initBinder(WebDataBinder binder) {
        binder.setValidator(checkNotNull(validator, "validator is null"));
        binder.registerCustomEditor(MethodUsageReport.Format.class, methodUsageReportFormatEnumConverter);
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
    private void onMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failure: " + e);
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    private void onApplicationException(CodekvastException e) {
        log.error("Application exception: " + e);
    }

    /**
     * Send the message to the currently logged in users that are affected by the message.
     */
    @Subscribe
    public void onWebSocketMessage(WebSocketMessage message) {
        message.getUsernames().stream().filter(username -> webSocketUserPresenceHandler.isPresent(username)).forEach(username -> {
            log.debug("Sending {} to '{}'", message, username);
            messagingTemplate.convertAndSendToUser(username, "/queue/data", message);
        });
    }

    /**
     * A REST endpoint for getting initial data for the web interface..
     *
     * @param principal The identity of the authenticated user.
     * @return An WebSocketMessage containing all data needed to inflate the web interface.
     */
    @RequestMapping("/api/web/data")
    public WebSocketMessage getInitialData(Principal principal) throws CodekvastException {
        String username = principal.getName();
        log.debug("'{}' requests initial data", username);

        return userService.getWebSocketMessage(username);
    }

    @RequestMapping(value = "/api/web/settings", method = RequestMethod.POST)
    public void saveSettings(@Valid @RequestBody OrganisationSettings settings, Principal principal)
            throws CodekvastException {

        String username = principal.getName();
        log.debug("'{}' persists settings {}", username, settings);

        userService.saveOrganisationSettings(username, settings);

        log.info("'{}' saved settings {}", username, settings);
    }

    @RequestMapping(value = "/api/web/methodUsagePreview", method = RequestMethod.POST, produces = "application/json")
    public MethodUsageReport getMethodUsagePreview(@Valid @RequestBody GetMethodUsageRequest request, Principal principal)
            throws CodekvastException {

        long startedAtMillis = System.currentTimeMillis();

        String username = principal.getName();
        log.debug("'{}' requests {}", username, request);

        MethodUsageReport report = reportService.getMethodUsagePreview(username, request);

        log.debug("Method usage report created in {} ms", System.currentTimeMillis() - startedAtMillis);
        return report;
    }

    @RequestMapping(value = "/api/web/methodUsage/{reportId}/{format}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getMethodUsageReport(Principal principal,
                                @PathVariable(value = "reportId") int reportId,
                                @PathVariable(value = "format") MethodUsageReport.Format format,
                                HttpServletResponse response)
            throws CodekvastException {

        log.debug("{} fetches report {} in {} format", principal.getName(), reportId, format);

        MethodUsageReport report = reportService.getMethodUsageReport(principal.getName(), reportId);

        setContentType(response, format, reportId);

        return formatters.get(format).format(report);
    }

    private void setContentType(HttpServletResponse response, MethodUsageReport.Format format, int reportId) {
        response.setContentType(format.getContentType() + "; charset=UTF-8");
        response.setHeader("Content-Disposition",
                           String.format("attachment; filename=codekvast-method-usage-report-%d.%s",
                                         reportId, format.getFilenameExtension()));
    }

    private static abstract class ContentFormatter {

        abstract String format(MethodUsageReport report);

        String formatInvokedAt(long invokedAtMillis) {
            return invokedAtMillis <= 0L ? "" : Instant.ofEpochMilli(invokedAtMillis).toString();
        }
    }


    private static class CsvContentFormatter extends ContentFormatter {
        @Override
        public String format(MethodUsageReport report) {
            List<String> lines = new ArrayList<>();
            lines.add("# method, scope, invokedAt");

            report.getMethods().forEach(entry -> {
                lines.add(
                        String.format("\"%s\",\"%s\",\"%s\"",
                                      entry.getName(),
                                      entry.getScope().toDisplayString(),
                                      formatInvokedAt(entry.getInvokedAtMillis())));
            });

            return lines.stream().collect(Collectors.joining("\n"));
        }
    }

    private static class JsonContentFormatter extends ContentFormatter {
        @Override
        public String format(MethodUsageReport report) {
            // TODO: implement JsonContentFormatter
            return "";
        }
    }


}
