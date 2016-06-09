package org.iplantc.de.server;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An authentication filter that uses CAS to authenticate but displays a landing page instead of
 * immediately redirecting the user to the authentication page.
 *
 * @author jstroot
 */
public class DeCasAuthenticationEntryPoint implements AuthenticationEntryPoint, InitializingBean {

    Logger LOG = Logger.getLogger(DeCasAuthenticationEntryPoint.class.getName());

    private LandingPage landingPage;

    private String rpcSuffix;

    private LogoutSuccessHandler logoutSuccessHandler;

    public void setLandingPage(LandingPage landingPage) {
        this.landingPage = landingPage;
    }

    public void setRpcSuffix(String rpcSuffix) {
        this.rpcSuffix = rpcSuffix;
    }

    public void setLogoutSuccessHandler(LogoutSuccessHandler logoutSuccessHandler) {
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(landingPage, "a LandingPage implementation must be specified");
        Assert.notNull(rpcSuffix, "an RPC call prefix must be specified");
        Assert.notNull(logoutSuccessHandler, "a logout success handler must be specified");
    }

    @Override
    public void commence(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse,
                         final AuthenticationException e) throws IOException, ServletException {

        if (isRpcCall(httpServletRequest) || isGWTCodeSplit(httpServletRequest)) {
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }

        // Display the landing page.
        landingPage.display(httpServletRequest, httpServletResponse);
    }

    private boolean isRpcCall(HttpServletRequest req) {
        return req.getRequestURI().endsWith(rpcSuffix);
    }

    private boolean isGWTCodeSplit(HttpServletRequest req) {
        if (req.getRequestURI().contains("/deferredjs/")) {
            return true;
        }
        return false;
    }


}
