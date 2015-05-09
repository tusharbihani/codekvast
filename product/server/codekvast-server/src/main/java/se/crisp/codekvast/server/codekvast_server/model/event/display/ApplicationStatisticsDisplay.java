package se.crisp.codekvast.server.codekvast_server.model.event.display;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * A display object for one application.
 *
 * @author olle.hallin@crisp.se
 */
@Value
@Builder
public class ApplicationStatisticsDisplay {
    @NonNull
    private String name;
    @NonNull
    private String version;
    int numHostNames;
    int numSignatures;
    int numNeverInvokedSignatures;
    int numInvokedSignatures;
    int numBootstrapSignatures;
    int usageCycleSeconds;
    long upTimeSeconds;
    long firstDataReceivedAtMillis;
    long lastDataReceivedAtMillis;
    boolean fullUsageCycleCompleted;
    int numWorkingCollectors;
    int numCollectors;
    int numPossiblyDeadSignatures;
    Integer percentPossiblyDeadSignatures;
    Integer percentNeverInvokedSignatures;
    Integer percentInvokedSignatures;
}
