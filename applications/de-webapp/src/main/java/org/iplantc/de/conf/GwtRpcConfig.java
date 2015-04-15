package org.iplantc.de.conf;

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
    public SimpleUrlHandlerMapping rpcUrlHandlerMapping() {
        // Set up simple url handler mappings here
        Properties urlProperties = new Properties();
//        urlProperties.put("/**/todo.rpc", todoRpcService());

        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setMappings(urlProperties);
        // Need to set order so these URLs are mapped/handled before any default handlers.
        simpleUrlHandlerMapping.setOrder(1);

        return simpleUrlHandlerMapping;
    }
}
