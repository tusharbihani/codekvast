package integrationTest.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.codekvast.common.metrics.CommonMetricsService;
import io.codekvast.dashboard.CodekvastDashboardApplication;
import io.codekvast.dashboard.agent.AgentController;
import io.codekvast.dashboard.agent.AgentService;
import io.codekvast.javaagent.model.v1.rest.GetConfigRequest1;
import io.codekvast.javaagent.model.v1.rest.GetConfigResponse1;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static io.codekvast.javaagent.model.Endpoints.Agent.V1_POLL_CONFIG;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author olle.hallin@crisp.se
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AgentController.class)
@ContextConfiguration(classes = CodekvastDashboardApplication.class)
public class AgentControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AgentService agentService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @MockBean
    private MeterRegistry meterRegistry;

    @MockBean
    private CommonMetricsService commonMetricsService;

    @Test
    public void should_accept_post_to_agentPollConfig() throws Exception {
        GetConfigRequest1 request = GetConfigRequest1.sample();
        when(agentService.getConfig(request)).thenReturn(GetConfigResponse1.sample().toBuilder()
                                                                           .codeBasePublisherName("foobar")
                                                                           .build());

        mvc.perform(post(V1_POLL_CONFIG)
                        .accept(APPLICATION_JSON_UTF8)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(objectMapper.writeValueAsString(request)))
           .andExpect(status().isOk())
           .andExpect(content().contentType(APPLICATION_JSON_UTF8))
           .andExpect(jsonPath("$.codeBasePublisherName").value("foobar"));
    }
}
