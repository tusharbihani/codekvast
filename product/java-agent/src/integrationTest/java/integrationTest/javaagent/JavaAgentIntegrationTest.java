package integrationTest.javaagent;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import io.codekvast.javaagent.AspectjMessageHandler;
import io.codekvast.javaagent.config.AgentConfig;
import io.codekvast.javaagent.config.AgentConfigFactory;
import io.codekvast.javaagent.model.v1.rest.GetConfigResponse1;
import io.codekvast.javaagent.util.FileUtils;
import io.codekvast.testsupport.ProcessUtils;
import lombok.RequiredArgsConstructor;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static integrationTest.javaagent.JavaAgentIntegrationTest.AgentState.*;
import static io.codekvast.javaagent.model.Endpoints.Agent.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeTrue;

@SuppressWarnings("UseOfSystemOutOrSystemErr")
@RunWith(Parameterized.class)
@RequiredArgsConstructor
public class JavaAgentIntegrationTest {

    @RequiredArgsConstructor
    enum AgentState {
        NO_CONFIG(false),
        NOT_FOUND_CONFIG(false),
        DISABLED_IN_CONFIG(false),
        ENABLED_IN_CONFIG_BUT_DISABLED_IN_SERVER(true),
        ENABLED_IN_CONFIG_AND_SERVER(true);

        final boolean shouldStart;
    }

    @RequiredArgsConstructor
    private static class TestConfig {
        private final String javaVersion;
        private final AgentState agentState;

        @Override
        public String toString() {
            return "JVM=" + javaVersion + ", agentState=" + agentState;
        }
    }

    // TODO: private final String jacocoAgent = System.getProperty("integrationTest.jacocoAgent");
    private static final String codekvastAgent = System.getProperty("integrationTest.codekvastAgent");
    private static final String classpath = System.getProperty("integrationTest.classpath");
    private static final String javaVersions = System.getProperty("integrationTest.javaVersions");

    @ClassRule
    public static WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort().dynamicHttpsPort());

    private static final Gson gson = new Gson();

    @Parameterized.Parameters(name = "{0}")
    public static List<TestConfig> testParameters() {
        // The streams API is not available in Java 7!
        List<TestConfig> result = new ArrayList<>();
        for (String version : javaVersions.split(",")) {
            String v = version.trim();
            if (v.startsWith("7")) {
                // Test missing config and disabled by config only once.
                // We only need to test this once, since CodekvastAgent.premain() will exit immediately before any Java version-specific
                // code is executed.
                result.add(new TestConfig(v, NO_CONFIG));
                result.add(new TestConfig(v, NOT_FOUND_CONFIG));
                result.add(new TestConfig(v, DISABLED_IN_CONFIG));
                result.add(new TestConfig(v, ENABLED_IN_CONFIG_BUT_DISABLED_IN_SERVER));
            }
            // Test weaving and uploading for all versions
            result.add(new TestConfig(v, ENABLED_IN_CONFIG_AND_SERVER));
        }
        return result;
    }

    // Is injected from testParameters()
    private final TestConfig testConfig;

    private File agentConfigFile;

    @Before
    public void beforeTest() throws Exception {
        // TODO: assertThat(jacocoAgent, notNullValue());
        assertThat(codekvastAgent, notNullValue());
        assertThat(classpath, notNullValue());
        assertThat(javaVersions, notNullValue());

        AgentConfig agentConfig = AgentConfigFactory.createTemplateConfig().toBuilder()
                                                    .serverUrl("http://localhost:" + wireMockRule.port())
                                                    .appName("SampleApp")
                                                    .appVersion("literal 1.0")
                                                    .aspectjOptions("-verbose -showWeaveInfo")
                                                    .enabled(testConfig.agentState.shouldStart)
                                                    .packages("sample")
                                                    .methodVisibility("protected")
                                                    .excludePackages("sample.app.excluded")
                                                    .codeBase("build/classes/java/integrationTest")
                                                    .bridgeAspectjMessagesToJUL(true)
                                                    .schedulerInitialDelayMillis(0)
                                                    .schedulerIntervalMillis(100)
                                                    .build();
        agentConfigFile = FileUtils.serializeToFile(agentConfig, "codekvast", ".conf.ser");
        agentConfigFile.deleteOnExit();
    }

    @Test
    public void should_not_start_when_no_config() throws Exception {
        assumeTrue(testConfig.agentState == NO_CONFIG);

        // given
        List<String> command = buildJavaCommand(null);

        // when
        String stdout = ProcessUtils.executeCommand(command);

        // then
        assertThat(stdout, containsString("No configuration file found, Codekvast will not start"));
        assertSampleAppOutput(stdout);
    }

    @Test
    public void should_not_start_when_not_found_config() throws Exception {
        assumeTrue(testConfig.agentState == NOT_FOUND_CONFIG);

        // given
        List<String> command = buildJavaCommand("foobar");

        // when
        String stdout = ProcessUtils.executeCommand(command);

        // then
        assertThat(stdout, containsString("Trying foobar"));
        assertThat(stdout, containsString("Invalid value of -Dcodekvast.configuration or CODEKVAST_CONFIG: foobar"));
        assertThat(stdout, containsString("Codekvast will not start"));
        assertSampleAppOutput(stdout);
    }

    @Test
    public void should_not_start_when_disabled_in_config() throws Exception {
        assumeTrue(testConfig.agentState == DISABLED_IN_CONFIG);

        // given
        List<String> command = buildJavaCommand(agentConfigFile.getAbsolutePath());

        // when
        String stdout = ProcessUtils.executeCommand(command);

        // then
        assertThat(stdout, containsString("Codekvast is disabled"));
        assertSampleAppOutput(stdout);
    }

    @Test
    public void should_weave_and_call_server() throws Exception {
        assumeTrue(testConfig.agentState.shouldStart);

        // given
        boolean enabledByServer = testConfig.agentState == ENABLED_IN_CONFIG_AND_SERVER;

        givenThat(post(V1_POLL_CONFIG)
                      .willReturn(okJson(gson.toJson(
                          GetConfigResponse1.builder()
                                            .codeBasePublisherName("http")
                                            .codeBasePublisherConfig("enabled=" + enabledByServer)
                                            .customerId(1L)
                                            .invocationDataPublisherName("http")
                                            .invocationDataPublisherConfig("enabled=" + enabledByServer)
                                            .configPollIntervalSeconds(1)
                                            .configPollRetryIntervalSeconds(1)
                                            .codeBasePublisherCheckIntervalSeconds(1)
                                            .codeBasePublisherRetryIntervalSeconds(1)
                                            .invocationDataPublisherIntervalSeconds(1)
                                            .invocationDataPublisherRetryIntervalSeconds(1)
                                            .build()))));

        givenThat(post(V2_UPLOAD_CODEBASE).willReturn(ok()));
        givenThat(post(V2_UPLOAD_INVOCATION_DATA).willReturn(ok()));

        List<String> command = buildJavaCommand(agentConfigFile.getAbsolutePath());

        // when
        String stdout = ProcessUtils.executeCommand(command);
        System.out.printf("stdout from the JVM is%n--------------------------------------------------%n%s%n--------------------------------------------------%n%n", stdout);

        // then
        assertThat(stdout, containsString("Found " + agentConfigFile.getAbsolutePath()));
        assertThat(stdout, containsString("[INFO] " + AspectjMessageHandler.LOGGER_NAME));
        assertThat(stdout, containsString("AspectJ Weaver Version "));
        assertThat(stdout, containsString("define aspect io.codekvast.javaagent.MethodExecutionAspect"));
        assertThat(stdout, containsString("Join point 'constructor-execution(void sample.app.SampleApp.<init>())'"));
        assertThat(stdout, containsString("Join point 'method-execution(int sample.app.SampleApp.add(int, int))'"));
        assertThat(stdout, containsString("Join point 'method-execution(void sample.app.SampleApp.main(java.lang.String[]))'"));
        assertThat(stdout, not(containsString("Join point 'method-execution(int sample.app.SampleApp.privateAdd(int, int))'")));
        assertThat(stdout, not(containsString("Join point 'method-execution(void sample.app.excluded.NotTrackedClass.doSomething())'")));
        assertThat(stdout, containsString("Codekvast shutdown completed in "));
        assertThat(stdout, not(containsString("error")));
        assertThat(stdout, not(containsString("[SEVERE]")));
        if (atLeastJava9()) {
            assertThat(stdout, containsString(
                "no longer creating weavers for these classloaders: [jdk.internal.loader.ClassLoaders$PlatformClassLoader]"));
        }
        assertSampleAppOutput(stdout);

        verify(postRequestedFor(urlEqualTo(V1_POLL_CONFIG)));

        if (enabledByServer) {
            verify(postRequestedFor(urlEqualTo(V2_UPLOAD_CODEBASE)));
            verify(postRequestedFor(urlEqualTo(V2_UPLOAD_INVOCATION_DATA)));
        }

    }

    private void assertSampleAppOutput(String stdout) {
        assertThat(stdout, containsString("[INFO] sample.app.SampleApp - SampleApp starts"));
        assertThat(stdout, containsString("[INFO] sample.app.SampleApp - 2+2=4"));
        assertThat(stdout, containsString("[INFO] sample.app.SampleApp - Exit"));
    }

    private boolean atLeastJava9() {
        String v = testConfig.javaVersion;
        return !v.startsWith("7") && !v.startsWith("8");
    }

    private List<String> buildJavaCommand(String configPath) {
        String cp = classpath.endsWith(":") ? classpath.substring(0, classpath.length()-2) : classpath;

        String java = String.format("%s/.sdkman/candidates/java/%s/bin/java", System.getenv("HOME"), testConfig.javaVersion);

        List<String> command = new ArrayList<>(
            Arrays.asList(java,
                          // TODO: "-javaagent:" + jacocoAgent,
                          "-javaagent:" + codekvastAgent,
                          "-cp", cp,
                          "-Djava.util.logging.config.file=src/integrationTest/resources/logging.properties",
                          "-Duser.language=en",
                          "-Duser.country=US"));
        if (configPath != null) {
            command.add("-Dcodekvast.configuration=" + configPath);
        }
        command.add("sample.app.SampleApp");
        System.out.printf("%nLaunching SampleApp with the command: %s%n%n", command);
        return command;
    }

}
