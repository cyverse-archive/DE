package org.iplantc.de.server;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * An authentication filter that uses CAS to authenticate but displays a landing page instead of
 * immediately redirecting the user to the authentication page.
 */
public class DeCasAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    private LandingPage landingPage;

    public void setLandingPage(LandingPage landingPage) {
        this.landingPage = landingPage;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(landingPage, "a LandingPage implementation must be specified");
    }

    @Override
    public void commence(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                         final AuthenticationException e) throws IOException, ServletException {
        landingPage.display(httpServletRequest, httpServletResponse);
    }
}
