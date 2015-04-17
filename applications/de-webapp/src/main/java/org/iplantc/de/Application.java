package org.iplantc.de;

import org.iplantc.de.conf.WebMvcConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * The entry point for the DE webapp.
 *
 * @author jstroot
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends AbstractAnnotationConfigDispatcherServletInitializer
                         implements WebApplicationInitializer {

    private Environment environment;
    final static Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected String[] getServletMappings() {
        String[] servletMappings = {"/", "*.rpc"};
        return servletMappings;
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[0];
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        Class<?>[] servletConfigClasses = {WebMvcConfig.class};
        return servletConfigClasses;
    }
}
