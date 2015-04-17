package org.iplantc.de.conf;

import org.iplantc.de.server.CacheControlFilter;
import org.iplantc.de.server.DeCasAuthenticationEntryPoint;
import org.iplantc.de.server.DeLandingPage;
import org.iplantc.de.server.auth.CasLogoutSuccessHandler;

import static org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorage;
import org.jasig.cas.client.proxy.ProxyGrantingTicketStorageImpl;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;


@Configuration
@EnableWebMvcSecurity
@PropertySource("file:/etc/iplant/de/de.properties")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Value("${org.iplantc.discoveryenvironment.cas.server-name}${org.iplantc.discoveryenvironment.cas.validation}")
    private String servicePropertiesService;

    @Value("${org.iplantc.discoveryenvironment.cas.logout-url}") private String singleLogoutFilterProcessesUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}") private String casServerUrlPrefix;
    @Value("${org.iplantc.discoveryenvironment.cas.validation}") private String authenticationFilterProcessesUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.proxy-receptor}") private String proxyReceptorUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.server-name}/login") private String defaultTargetUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/logout") private String logoutUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.server-name}/logged-out") private String defaultRedirectUrl;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/login") private String loginUrl;
    @Value("${org.iplantc.discoveryenvironment.maintenance-file}") private String deMaintenanceFile;
    @Value("${org.iplantc.discoveryenvironment.cas.base-url}/login") private String casLoginUrl;

    @Bean
    public DeLandingPage landingPage() {
        LOG.info("casLoginUrl = {}", casLoginUrl);
        DeLandingPage landingPage = new DeLandingPage();
        landingPage.setCasService(serviceProperties());
        landingPage.setDeMaintenanceFile(deMaintenanceFile);
        landingPage.setLoginUrl(casLoginUrl);
        return landingPage;
    }

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(servicePropertiesService);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        casAuthenticationProvider.setAuthenticationUserDetailsService(authenticationUserDetailsService());
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        casAuthenticationProvider.setKey("discovery_environment_web_application");
        return casAuthenticationProvider;
    }

    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> authenticationUserDetailsService() {
        String[] attributes = {"entitlement"};
        return new GrantedAuthorityFromAssertionAttributesUserDetailsService(attributes);
    }

    @Bean
    public SessionAuthenticationStrategy sessionStrategy() {
        SessionAuthenticationStrategy sessionStrategy = new SessionFixationProtectionStrategy();
        return sessionStrategy;
    }

    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(casServerUrlPrefix);
    }

    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(authenticationFilterProcessesUrl);
        casAuthenticationFilter.setProxyGrantingTicketStorage(proxyGrantingTicketStorage());
        casAuthenticationFilter.setProxyReceptorUrl(proxyReceptorUrl);
        casAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler());
        return casAuthenticationFilter;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        authenticationSuccessHandler.setDefaultTargetUrl(defaultTargetUrl);
        return authenticationSuccessHandler;
    }

    @Bean
    public ProxyGrantingTicketStorage proxyGrantingTicketStorage() {
        return new ProxyGrantingTicketStorageImpl();
    }

//    @Bean
//    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
    // TODO This differs between DE and Belphegor
//        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
//        casAuthenticationEntryPoint.setLoginUrl(loginUrl);
//        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
//        return casAuthenticationEntryPoint;
//    }

    @Bean
    public AuthenticationEntryPoint deCasAuthenticationEntryPoint() {
        DeCasAuthenticationEntryPoint casAuthenticationEntryPoint = new DeCasAuthenticationEntryPoint();
        casAuthenticationEntryPoint.setLandingPage(landingPage());
        casAuthenticationEntryPoint.setRpcPrefix("*.rpc");
        casAuthenticationEntryPoint.setLogoutSuccessHandler(logoutSuccessHandler());
        return casAuthenticationEntryPoint;
    }

    @Bean
    public CasLogoutSuccessHandler logoutSuccessHandler() {
        CasLogoutSuccessHandler logoutSuccessHandler = new CasLogoutSuccessHandler();
        logoutSuccessHandler.setLogoutUrl(logoutUrl);
        logoutSuccessHandler.setDefaultRedirectUrl(defaultRedirectUrl);
        return logoutSuccessHandler;
    }

    @Bean
    public LogoutFilter requestSingleLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(logoutSuccessHandler(),
                                                     new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(singleLogoutFilterProcessesUrl);
        return logoutFilter;
    }

    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setCasServerUrlPrefix(casServerUrlPrefix);
        return singleSignOutFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOG.info("casLoginUrl = {}", casLoginUrl);
        http.authorizeRequests()
            .antMatchers("/applets/**", "/logout", "/logged-out", "/*.css", "/*.png").permitAll()
            .anyRequest().authenticated().and()
//        .exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint());
        .exceptionHandling().authenticationEntryPoint(deCasAuthenticationEntryPoint());

        http.addFilter(casAuthenticationFilter())
            .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
            .addFilterBefore(requestSingleLogoutFilter(), LogoutFilter.class)
            .addFilterAfter(new CacheControlFilter(), CasAuthenticationFilter.class);
        http.headers().cacheControl().addHeaderWriter(new XFrameOptionsHeaderWriter(SAMEORIGIN));

        http.csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
    }
}
