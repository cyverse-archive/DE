package org.iplantc.de.conf;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;


@Configuration
@EnableWebMvcSecurity
@Import({DeWebSecurityConfig.class, AdminWebSecurityConfig.class})
@PropertySource("file:/etc/iplant/de/de.properties")
public class WebSecurityConfig {


}
