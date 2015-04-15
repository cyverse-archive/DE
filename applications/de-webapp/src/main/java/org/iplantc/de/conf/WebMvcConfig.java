package org.iplantc.de.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import java.util.Properties;

/**
 * @author jstroot
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Autowired
    public WebMvcConfig() {
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/GwtWebApp.html").addResourceLocations("/");
        registry.addResourceHandler("/*.css").addResourceLocations("/");
        registry.addResourceHandler("/gwtwebapp/**").addResourceLocations("/gwtwebapp/");
        registry.addResourceHandler("/darkgwtwebapp/**").addResourceLocations("/darkgwtwebapp/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

//    @Bean
//    public GwtRpcController todoRpcService(){
        // Wire up rpc classes.
//        GwtRpcController todoRpcService = new GwtRpcController();
//        todoRpcService.setRemoteService(new TodoRpcServiceImpl());
//        return todoRpcService;
//    }

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

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setViewClass(JstlView.class);
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

}
