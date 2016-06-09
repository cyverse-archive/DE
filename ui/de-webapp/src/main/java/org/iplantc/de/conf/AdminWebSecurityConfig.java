package org.iplantc.de.conf;

import org.iplantc.de.server.CacheControlFilter;
import org.iplantc.de.server.DeCasAuthenticationEntryPoint;
import org.iplantc.de.server.DeLandingPage;
import org.iplantc.de.server.MDCFilter;
import org.iplantc.de.server.auth.CasGroupUserDetailsService;
import org.iplantc.de.server.auth.CasLogoutSuccessHandler;

import static org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for DE admin portal, 'Belphegor'.
 * @author jstroot
 */
@Configuration
@Order(1)
@EnableWebMvcSecurity
public class AdminWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${org.iplantc.admin.cas.authorized-groups}") private String authorizedGroups;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/login") private String casLoginUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/logout") private String casLogoutUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}") private String casServerUrlPrefix;
    @Value("${org.iplantc.discoveryenvironment.maintenance-file}") private String deMaintenanceFile;
    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}") private String logoutUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.server-name}/belphegor") private String serverName;
    @Value("${org.iplantc.discoveryenvironment.cas.validation}") private String validation;

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> adminAuthenticationUserDetailsService() {
        return new CasGroupUserDetailsService("entitlement");
    }

    @Bean
    public AuthenticationEntryPoint adminCasAuthenticationEntryPoint() {
        DeCasAuthenticationEntryPoint casAuthenticationEntryPoint = new DeCasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLandingPage(adminLandingPage());
        casAuthenticationEntryPoint.setRpcSuffix(".rpc");
        casAuthenticationEntryPoint.setLogoutSuccessHandler(adminLogoutSuccessHandler());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CasAuthenticationFilter adminCasAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(validation);
        casAuthenticationFilter.setSessionAuthenticationStrategy(adminSessionStrategy());
        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationProvider adminCasAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(adminAuthenticationUserDetailsService());
        casAuthenticationProvider.setServiceProperties(adminServiceProperties());
        casAuthenticationProvider.setTicketValidator(adminTicketValidator());
        casAuthenticationProvider.setKey("tool_administration_utility");
        return casAuthenticationProvider;
    }

    @Bean
    public DeLandingPage adminLandingPage() {
        DeLandingPage landingPage = new DeLandingPage();
        landingPage.setCasService(adminServiceProperties());
        landingPage.setDeMaintenanceFile(deMaintenanceFile);
        landingPage.setLoginUrl(casLoginUrl);
        return landingPage;
    }

    @Bean
    public CasLogoutSuccessHandler adminLogoutSuccessHandler() {
        CasLogoutSuccessHandler logoutSuccessHandler = new CasLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutUrl(casLogoutUrl);
        return logoutSuccessHandler;
    }

    @Bean
    public LogoutFilter adminRequestSingleLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(adminLogoutSuccessHandler(),
                                                     new SecurityContextLogoutHandler());
        logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/belphegor" + logoutUrl));
        return logoutFilter;
    }

    @Bean
    public ServiceProperties adminServiceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(serverName + validation);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public SessionAuthenticationStrategy adminSessionStrategy() {
        return new SessionFixationProtectionStrategy();
    }

    public SingleSignOutFilter adminSingleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrlPrefix);
        return singleSignOutFilter;
    }

    @Bean
    public Cas20ServiceTicketValidator adminTicketValidator() {
        return new Cas20ServiceTicketValidator(casServerUrlPrefix);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.antMatcher("/belphegor/**")
            .authorizeRequests()
            .antMatchers("/**/logout", "/**/logged-out", "/*.css", "/*.png").permitAll()
            .anyRequest().authenticated()
            .anyRequest().hasAnyAuthority(authorizedGroups.split("\\s*,\\s*"))
            .and().exceptionHandling().authenticationEntryPoint(adminCasAuthenticationEntryPoint()).and()
            .exceptionHandling().accessDeniedPage("/access-denied");
        /*
         * There are two logout filters.
         * The first takes the user to /belphegor/logout, and is configured first via the HttpSecurity object.
         * The second takes the user to /belphegor/logged-out.
         */
        http.logout().logoutSuccessUrl("/belphegor/logout")
            .logoutRequestMatcher(new AntPathRequestMatcher("/belphegor/j_spring_security_logout"))
            .permitAll()
            .configure(http);

        http.addFilter(adminCasAuthenticationFilter())
            .addFilterBefore(adminSingleSignOutFilter(), CasAuthenticationFilter.class)
            .addFilterBefore(adminRequestSingleLogoutFilter(), LogoutFilter.class)
            .addFilterAfter(new CacheControlFilter(), CasAuthenticationFilter.class)
            .addFilterAfter(new MDCFilter(), CasAuthenticationFilter.class);

        http.headers()
            .contentTypeOptions()
            .xssProtection()
            .httpStrictTransportSecurity()
            .addHeaderWriter(new XFrameOptionsHeaderWriter(SAMEORIGIN));

        http.csrf().disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(adminCasAuthenticationProvider());
    }

}
