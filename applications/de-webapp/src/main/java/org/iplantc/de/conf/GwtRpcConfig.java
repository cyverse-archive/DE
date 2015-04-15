package org.iplantc.de.conf;

import org.iplantc.de.server.rpc.GwtRpcController;
import org.iplantc.de.server.services.AboutApplicationServiceImpl;
import org.iplantc.de.server.services.DEServiceImpl;
import org.iplantc.de.server.services.EmailServiceImpl;
import org.iplantc.de.server.services.PropertyServiceImpl;
import org.iplantc.de.server.services.UUIDServiceImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * Configuration and Spring MVC integration for GWT RPC classes.
 *
 * @author jstroot
 */
@Configuration
public class GwtRpcConfig {

    @Bean
    public GwtRpcController aboutRpcService(){
        return new GwtRpcController(new AboutApplicationServiceImpl());
    }

    @Bean
    public GwtRpcController uuidRpcService(){
        return new GwtRpcController(new UUIDServiceImpl());
    }

    @Bean
    public GwtRpcController emailRpcService(){
        return new GwtRpcController(new EmailServiceImpl());
    }

    @Bean
    public GwtRpcController propertiesRpcService(){
        return new GwtRpcController(new PropertyServiceImpl());
    }

    @Bean
    public GwtRpcController apiRpcService(){
        return new GwtRpcController(new DEServiceImpl());
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

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setMappings(urlProperties);
        // Need to set order so these URLs are mapped/handled before any default handlers.
        simpleUrlHandlerMapping.setOrder(1);

        return simpleUrlHandlerMapping;
    }
}
