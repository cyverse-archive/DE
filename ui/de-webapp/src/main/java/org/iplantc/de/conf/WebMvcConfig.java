package org.iplantc.de.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

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
    public void addViewControllers(ViewControllerRegistry registry) {
        // Add redirection to prevent authentication issues when accessing base app urls not ending
        // in a "/"
        registry.addRedirectViewController("/de", "/de/")
                .setKeepQueryParams(true)
                .setStatusCode(HttpStatus.PERMANENT_REDIRECT);
        registry.addRedirectViewController("/belphegor", "/belphegor/")
                .setKeepQueryParams(true)
                .setStatusCode(HttpStatus.PERMANENT_REDIRECT);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/*.css").addResourceLocations("/");

        registry.addResourceHandler("/de/**").addResourceLocations("/");
        registry.addResourceHandler("/belphegor/**").addResourceLocations("/");
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
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
