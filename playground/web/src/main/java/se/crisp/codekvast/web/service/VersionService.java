package se.crisp.codekvast.web.service;

/**
 * Provides other parts of the application with build version information.
 *
 * @author olle.hallin@crisp.se
 */
public interface VersionService {
    /**
     * @return A string containing all relevant build information for presentation in e.g., the web tier.
     */
    String getFullBuildVersion();
}