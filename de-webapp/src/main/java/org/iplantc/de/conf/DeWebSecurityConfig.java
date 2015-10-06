package org.iplantc.de.conf;

import org.iplantc.de.server.CacheControlFilter;
import org.iplantc.de.server.DeCasAuthenticationEntryPoint;
import org.iplantc.de.server.DeLandingPage;
import org.iplantc.de.server.MDCFilter;
import org.iplantc.de.server.auth.CasLogoutSuccessHandler;

import static org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.userdetails.GrantedAuthorityFromAssertionAttributesUserDetailsService;
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
import org.springframework.web.context.request.RequestContextListener;

@Configuration
@EnableWebMvcSecurity
public class DeWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(DeWebSecurityConfig.class);
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/login") private String casLoginUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/logout") private String casLogoutUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}") private String casServerUrlPrefix;
    @Value("${org.iplantc.discoveryenvironment.maintenance-file}") private String deMaintenanceFile;
    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}") private String logoutUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.server-name}/de") private String serverName;
    @Value("${org.iplantc.discoveryenvironment.cas.validation}") private String validation;

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> deAuthenticationUserDetailsService() {
        String[] attributes = {"entitlement"};
        return new GrantedAuthorityFromAssertionAttributesUserDetailsService(attributes);
    }

    @Bean
    public AuthenticationEntryPoint deCasAuthenticationEntryPoint() {
        DeCasAuthenticationEntryPoint casAuthenticationEntryPoint = new DeCasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLandingPage(deLandingPage());
        casAuthenticationEntryPoint.setRpcSuffix(".rpc");
        casAuthenticationEntryPoint.setLogoutSuccessHandler(deLogoutSuccessHandler());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CasAuthenticationFilter deCasAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(validation);
        casAuthenticationFilter.setSessionAuthenticationStrategy(deSessionStrategy());
        return casAuthenticationFilter;
    }

    @Bean
    public CasAuthenticationProvider deCasAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(deAuthenticationUserDetailsService());
        casAuthenticationProvider.setServiceProperties(deServiceProperties());
        casAuthenticationProvider.setTicketValidator(deServiceTicketValidator());
        casAuthenticationProvider.setKey("discovery_environment_web_application");
        return casAuthenticationProvider;
    }

    @Bean
    public DeLandingPage deLandingPage() {
        DeLandingPage landingPage = new DeLandingPage();
        landingPage.setCasService(deServiceProperties());
        landingPage.setDeMaintenanceFile(deMaintenanceFile);
        landingPage.setLoginUrl(casLoginUrl);
        return landingPage;
    }

    @Bean
    public CasLogoutSuccessHandler deLogoutSuccessHandler() {
        CasLogoutSuccessHandler logoutSuccessHandler = new CasLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutUrl(casLogoutUrl);
        logoutSuccessHandler.setDefaultRedirectUrl(serverName + "/logged-out");
        return logoutSuccessHandler;
    }

    @Bean
    public LogoutFilter deRequestSingleLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(deLogoutSuccessHandler(),
                                                     new SecurityContextLogoutHandler());
        logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/de" + logoutUrl));
        return logoutFilter;
    }

    @Bean
    public ServiceProperties deServiceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(serverName + validation);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public Cas20ServiceTicketValidator deServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(casServerUrlPrefix);
    }

    @Bean
    public SessionAuthenticationStrategy deSessionStrategy() {
        SessionAuthenticationStrategy sessionStrategy = new SessionFixationProtectionStrategy();
        return sessionStrategy;
    }

    @Bean
    public RequestContextListener requestContextListener(){
        return new RequestContextListener();
    }

    public SingleSignOutFilter deSingleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrlPrefix);
        return singleSignOutFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.antMatcher("/de/**")
            .authorizeRequests()
            .antMatchers("/applets/**", "/**/logout", "/**/logged-out", "/*.css", "/*.png").permitAll()
            .anyRequest().authenticated().and()
            .exceptionHandling().authenticationEntryPoint(deCasAuthenticationEntryPoint());

        /*
         * There are two logout filters.
         * The first takes the user to /de/logout, and is configured first via the HttpSecurity object.
         * The second takes the user to /de/logged-out.
         */
        http.logout().logoutSuccessUrl("/de/logout")
            .logoutRequestMatcher(new AntPathRequestMatcher("/de/j_spring_security_logout"))
            .permitAll()
            .configure(http); // Force creation and registration of first logout filter

        http.addFilter(deCasAuthenticationFilter())
            .addFilterBefore(deSingleSignOutFilter(), CasAuthenticationFilter.class)
            .addFilterBefore(deRequestSingleLogoutFilter(), LogoutFilter.class)
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
        auth.authenticationProvider(deCasAuthenticationProvider());
    }

}
