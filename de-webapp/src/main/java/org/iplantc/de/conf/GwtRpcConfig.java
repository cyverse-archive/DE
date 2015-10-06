package org.iplantc.de.conf;

import org.iplantc.de.server.ServiceCallResolver;
import org.iplantc.de.server.auth.JwtUrlConnector;
import org.iplantc.de.server.auth.UrlConnector;
import org.iplantc.de.server.rpc.DeGwtRemoteLoggingServiceImpl;
import org.iplantc.de.server.rpc.GwtRpcController;
import org.iplantc.de.server.services.AboutApplicationServiceImpl;
import org.iplantc.de.server.services.DEServiceImpl;
import org.iplantc.de.server.services.EmailServiceImpl;
import org.iplantc.de.server.services.IplantEmailClient;
import org.iplantc.de.server.services.PropertyServiceImpl;
import org.iplantc.de.server.services.UUIDServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.ServletConfig;

/**
 * Configuration and Spring MVC integration for GWT RPC classes.
 *
 * @author jstroot
 */
@Configuration
public class GwtRpcConfig {

    @Autowired private Environment environment;
    @Autowired private ServiceCallResolver serviceCallResolver;
    @Autowired private IplantEmailClient emailClient;
    @Autowired private UrlConnector urlConnector;
    @Autowired private ServletConfig servletConfig;

    @Value("${org.iplantc.discoveryenvironment.about.defaultBuildNumber}") private String defaultBuildNumber;
    @Value("${org.iplantc.discoveryenvironment.about.releaseVersion}") private String releaseVersion;

    @Bean
    public GwtRpcController aboutRpcService(){
        return new GwtRpcController(new AboutApplicationServiceImpl(defaultBuildNumber,
                                                                    releaseVersion,
                                                                    servletConfig));
    }

    @Bean
    public GwtRpcController uuidRpcService(){
        return new GwtRpcController(new UUIDServiceImpl());
    }

    @Bean
    public GwtRpcController emailRpcService(){
        return new GwtRpcController(new EmailServiceImpl(emailClient));
    }

    @Bean
    public GwtRpcController propertiesRpcService(){
        return new GwtRpcController(new PropertyServiceImpl(environment));
    }

    @Bean
    public GwtRpcController apiRpcService(){
        return new GwtRpcController(new DEServiceImpl(serviceCallResolver,
                                                      urlConnector));
    }

    @Bean
    public GwtRpcController remoteLogging() {
        return new GwtRpcController(new DeGwtRemoteLoggingServiceImpl());
    }

    @Bean
    public SimpleUrlHandlerMapping rpcUrlHandlerMapping() {
        // Set up simple url handler mappings here
        Properties urlProperties = new Properties();
        urlProperties.put("**/about.rpc", aboutRpcService());
        urlProperties.put("**/uuid.rpc", uuidRpcService());
        urlProperties.put("**/email.rpc", emailRpcService());
        urlProperties.put("**/properties.rpc", propertiesRpcService());
        urlProperties.put("**/api.rpc", apiRpcService());
        urlProperties.put("**/remote_logging", remoteLogging());

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setMappings(urlProperties);
        // Need to set order so these URLs are mapped/handled before any default handlers.
        simpleUrlHandlerMapping.setOrder(1);

        return simpleUrlHandlerMapping;
    }
}
