package org.iplantc.de.server;

import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A {@link LogoutSuccessHandler} implementation that redirects to the CAS logout endpoint and requests redirection back
 * to another page based on the value of a query-string parameter.
 *
 * @author Dennis Roberts
 */
public class CasLogoutSuccessHandler implements LogoutSuccessHandler {

    /**
     * Used to log debugging information.
     */
    private static final Logger LOG = Logger.getLogger(CasLogoutSuccessHandler.class);

    /**
     * The strategy to use when sending redirect requests.
     */
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * The URL used to log out of CAS.
     */
    private String logoutUrl;

    /**
     * The list of possible URLs to redirect the user to after logout is successful.
     */
    private Map<String, String> redirectUrls;

    /**
     * The URL to redirect the user to by default.
     */
    private String defaultRedirectUrl;

    /**
     * The name of the query string parameter used to determine where the user will be redirected to.
     */
    private String redirectUrlSelectorName;

    /**
     * @param logoutUrl the URL used to log out of CAS.
     */
    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    /**
     * @param redirectUrls the URLs to redirect the user to after logout is successful.
     */
    public void setRedirectUrls(Map<String, String> redirectUrls) {
        this.redirectUrls = redirectUrls;
    }

    /**
     * @param defaultRedirectUrl the URL to redirect the user to by default.
     */
    public void setDefaultRedirectUrl(String defaultRedirectUrl) {
        this.defaultRedirectUrl = defaultRedirectUrl;
    }

    /**
     * @param redirectUrlSelectorName the name of the query string parameter used to determine the destination.
     */
    public void setRedirectUrlSelectorName(String redirectUrlSelectorName) {
        this.redirectUrlSelectorName = redirectUrlSelectorName;
    }

    /**
     * Handles a successful logout request.
     *
     * @param req the HTTP servlet request.
     * @param res the HTTP servlet response.
     * @param auth the user's authentication.
     * @throws IOException if an I/O error occurs.
     * @throws ServletException if a servlet error occurs.
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse res, Authentication auth)
            throws IOException, ServletException {
        String fullLogoutUrl = buildFullLogoutUrl(req, determineRedirectUrl(req));
        LOG.debug("fullLogoutUrl =" + fullLogoutUrl);
        redirectStrategy.sendRedirect(req, res, fullLogoutUrl);
    }

    /**
     * Determines the URL to redirect the user to if the user is supposed to be redirected.
     *
     * @param req the HTTP servlet request.
     * @return the URL to redirect the user to.
     */
    private String determineRedirectUrl(HttpServletRequest req) {
        String redirectUrl = defaultRedirectUrl;
        if (redirectUrlSelectorName != null) {
            String selector = req.getParameter(redirectUrlSelectorName);
            if (selector != null && redirectUrls.containsKey(selector)) {
                redirectUrl = redirectUrls.get(selector);
            }
        }
        return redirectUrl;
    }

    /**
     * Builds the full logout URL based on the logout URL and the redirect URL.
     *
     * @param req the HTTP servlet request.
     * @param redirectUrl the URL to redirect the user to after successful logout.
     * @return the full logout URL.
     * @throws ServletException if the logout URL can't be parsed.
     */
    private String buildFullLogoutUrl(HttpServletRequest req, String redirectUrl) throws ServletException {
        try {
            URI uri = new URI(logoutUrl);
            String query = determineQueryString(req, uri.getQuery(), redirectUrl);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), query, uri.getFragment()).toString();
        }
        catch (URISyntaxException e) {
            throw new ServletException("unable to build full logout URL", e);
        }
    }

    /**
     * Determines the query string to use for the logout URL.
     *
     * @param req the HTTP servlet request.
     * @param origQuery the original query string.
     * @param redirectUrl the URL to redirect the suer to after successful logout.
     * @return the query string to use in the full logout URL.
     */
    private String determineQueryString(HttpServletRequest req, String origQuery, String redirectUrl) {
        String query = null;
        if (redirectUrl != null) {
            String redirectParm = "service=" + makeAbsolute(req, redirectUrl);
            query = origQuery == null ? redirectParm : origQuery + "&" + redirectParm;
        }
        return query;
    }

    /**
     * Converts a relative URL to an absolute URL.
     *
     * @param req the HTTP servlet request.
     * @param url the URL to convert.
     * @return an absolute version of the URL.
     */
    private String makeAbsolute(HttpServletRequest req, String url) {
        return UrlUtils.isAbsoluteUrl(url) ? url : req.getContextPath() + url;
   }
}
