package se.crisp.codekvast.agent.main;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import se.crisp.codekvast.server.agent_api.model.v1.Constraints;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.File;
import java.net.URI;

/**
 * Encapsulates the configuration of the codekvast-agent.
 *
 * @author olle.hallin@crisp.se
 */
@Component
@ConfigurationProperties(prefix = "codekvast")
@Data
@Builder
@AllArgsConstructor
public class AgentConfig {
    @NotNull
    private File dataPath;

    @NotNull
    private String apiAccessID;

    @NotNull
    private String apiAccessSecret;

    @NotNull
    private URI serverUri;

    @Min(1)
    private int serverUploadIntervalSeconds;

    @NotNull
    @Size(max = Constraints.MAX_CODEKVAST_VERSION_LENGTH)
    String codekvastVersion;

    @NotNull
    @Size(max = Constraints.MAX_CODEKVAST_VCS_ID_LENGTH)
    String codekvastVcsId;

    public String getDisplayVersion() {
        return codekvastVersion + "-" + codekvastVcsId;
    }
}