package io.codekvast.javaagent.config;

import io.codekvast.javaagent.util.Constants;
import lombok.SneakyThrows;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class AgentConfigTest {

    private File file = classpathResourceAsFile("/codekvast1.conf");
    private AgentConfig config = AgentConfigFactory.parseAgentConfig(file, null);

    @After
    public void afterTest() {
        System.clearProperty(AgentConfigFactory.SYSPROP_OPTS);
    }

    @Test
    public void testParseConfigFileWithOverride() {
        AgentConfig config2 = AgentConfigFactory.parseAgentConfig(file, "appName=appName2;enabled=false");
        assertThat(config, not(is(config2)));
        assertThat(config.getAppName(), is("appName1"));
        assertThat(config.isEnabled(), is(true));
        assertThat(config.getHostname(), is("some-hostname"));
        assertThat(config2.getAppName(), is("appName2"));
        assertThat(config2.isEnabled(), is(false));
    }

    @Test
    public void testParseConfigFilePathWithSyspropAndCmdLineOverride() {
        System.setProperty(AgentConfigFactory.SYSPROP_OPTS, "codeBase=/path/to/$appName");
        AgentConfig config = AgentConfigFactory.parseAgentConfig(
            classpathResourceAsFile("/incomplete-agent-config.conf"),
            "appName=some-app-name;appVersion=version;");
        assertThat(config.getAppName(), is("some-app-name"));
        assertThat(config.getAppVersion(), is("version"));
        assertThat(config.getCodeBase(), is("/path/to/some-app-name"));
        assertThat(config.getHostname(), is(Constants.HOST_NAME));
    }

    @Test
    public void testGetFilenamePrefix() {
        AgentConfig config = AgentConfigFactory
            .createTemplateConfig()
            .toBuilder()
            .appName("   Some funky App name#'**  ")
            .appVersion("literal =)(%1.2./()¤%&3-Beta4+.RELEASE")
            .build();
        assertThat(config.getFilenamePrefix("prefix---"), is("prefix-somefunkyappname-1.2.3-beta4+.release-"));
    }

    @Test
    public void should_create_http_client_without_httpProxy() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig().toBuilder().httpProxyUsername("proxyUsername").build();
        OkHttpClient httpClient = config.getHttpClient();
        assertThat(httpClient, not(nullValue()));
        assertThat(httpClient.proxy(), nullValue());

        Authenticator authenticator = httpClient.proxyAuthenticator();
        assertThat(authenticator, is(Authenticator.NONE));
    }

    @Test
    public void should_accept_httpProxy_with_default_port() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig().toBuilder()
                                               .httpProxyHost("foo").build();
        OkHttpClient httpClient = config.getHttpClient();
        assertThat(httpClient, not(nullValue()));

        Proxy proxy = httpClient.proxy();
        assertThat(proxy, not(nullValue()));
        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        assertThat(proxy.address(), CoreMatchers.<SocketAddress>is(new InetSocketAddress("foo", 3128)));

        Authenticator authenticator = httpClient.proxyAuthenticator();
        assertThat(authenticator, is(Authenticator.NONE));
    }

    @Test
    public void should_accept_httpProxy_with_explicit_port() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig().toBuilder()
                                               .httpProxyHost("foo")
                                               .httpProxyPort(4711).build();
        OkHttpClient httpClient = config.getHttpClient();
        assertThat(httpClient, not(nullValue()));

        Proxy proxy = httpClient.proxy();
        assertThat(proxy, not(nullValue()));
        assertThat(proxy.type(), is(Proxy.Type.HTTP));
        assertThat(proxy.address(), CoreMatchers.<SocketAddress>is(new InetSocketAddress("foo", 4711)));

    }

    @Test
    public void should_accept_httpProxyHost_proxyUsername_and_proxyPassword() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig().toBuilder()
                                               .httpProxyHost("foo")
                                               .httpProxyUsername("username")
                                               .httpProxyPassword("password")
                                               .build();
        OkHttpClient httpClient = config.getHttpClient();
        assertThat(httpClient, not(nullValue()));

        Authenticator authenticator = httpClient.proxyAuthenticator();
        assertThat(authenticator, not(is(Authenticator.NONE)));
    }

    @Test
    public void should_accept_httpProxyHost_proxyUsername_but_no_proxyPassword() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig().toBuilder()
                                               .httpProxyHost("foo")
                                               .httpProxyUsername("username")
                                               .httpProxyPassword(null)
                                               .build();
        OkHttpClient httpClient = config.getHttpClient();
        assertThat(httpClient, not(nullValue()));

        Authenticator authenticator = httpClient.proxyAuthenticator();
        assertThat(authenticator, not(is(Authenticator.NONE)));
    }

    @Test
    public void should_cache_http_client() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig();
        OkHttpClient httpClient1 = config.getHttpClient();
        OkHttpClient httpClient2 = config.getHttpClient();

        assertThat(httpClient1, sameInstance(httpClient2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_reject_httpProxy_with_missing_port() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig().toBuilder()
                                               .httpProxyHost("foo")
                                               .httpProxyPort(0)
                                               .build();
        config.getHttpClient();
    }

    @Test
    public void should_have_scheduler_intervals_in_sample_config() {
        AgentConfig config = AgentConfigFactory.createSampleAgentConfig();
        assertThat(config.getSchedulerInitialDelayMillis(), not(is(0)));
        assertThat(config.getSchedulerIntervalMillis(), not(is(0)));
    }

    @Test
    public void should_have_scheduler_intervals_in_template_config() {
        AgentConfig config = AgentConfigFactory.createTemplateConfig();
        assertThat(config.getSchedulerInitialDelayMillis(), not(is(0)));
        assertThat(config.getSchedulerIntervalMillis(), not(is(0)));
    }

    @Test
    public void should_return_commonPublicationData() {
        AgentConfig config = AgentConfigFactory.createTemplateConfig();
        assertNotNull(config.commonPublicationData());
    }

    @SneakyThrows(URISyntaxException.class)
    private File classpathResourceAsFile(String resourceName) {
        return new File(getClass().getResource(resourceName).toURI());
    }
}
