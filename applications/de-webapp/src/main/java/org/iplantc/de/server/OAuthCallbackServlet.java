package org.iplantc.de.server;

import com.google.common.base.Strings;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class OAuthCallbackServlet extends HttpServlet {

    private static final String CALLBACK_PATH = "/secured/oauth/access-code";

    private static final String ERROR_PARAM = "error";

    private static final String AUTH_CODE_PARAM = "code";

    private static final Logger LOG = Logger.getLogger(OAuthCallbackServlet.class);

    private DiscoveryEnvironmentProperties deProps;
    protected void setDeProps(final DiscoveryEnvironmentProperties deProps) { this.deProps = deProps; }

    private UrlConnector urlConnector;
    protected void setUrlConnector(final UrlConnector urlConnector) { this.urlConnector = urlConnector; }

    public OAuthCallbackServlet() {}

    public OAuthCallbackServlet(final DiscoveryEnvironmentProperties deProps, final UrlConnector urlConnector) {
        this.deProps = deProps;
        this.urlConnector = urlConnector;
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        if (isError(req)) {
            authorizationErrorResponse(req, resp);
        } else {
            callAuthCodeService(req, resp);
        }
    }

    private void callAuthCodeService(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final String apiName = extractApiName(req.getPathInfo());
        if (Strings.isNullOrEmpty(apiName)) {
            LOG.error("no API name found in URL path: " + req.getPathInfo());
            resp.sendError(resp.SC_BAD_REQUEST);
            return;
        }

        final String authCode = req.getParameter(AUTH_CODE_PARAM);
        if (Strings.isNullOrEmpty(authCode)) {
            LOG.error("no authorization code in query string: " + req.getQueryString());
            resp.sendError(resp.SC_BAD_REQUEST);
            return;
        }

        final HttpGet request = urlConnector.getRequest(req, serviceCallbackUrl(apiName, authCode));
        // TODO: submit the request then display a message to the user.
    }

    private String serviceCallbackUrl(String apiName, String authCode) {
        try {
            return new URIBuilder(deProps.getProtectedDonkeyBaseUrl() + CALLBACK_PATH + "/" + apiName)
                    .addParameter(AUTH_CODE_PARAM, authCode)
                    .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the auth code service callback URI", e);
            throw new RuntimeException(e);
        }
    }

    private void authorizationErrorResponse(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final String errorCode = req.getHeader(ERROR_PARAM);
        LOG.warn("unable to obtain authorization: " + errorCode);
        resp.getWriter().append("Authorization not obtained. Some Discovery Environment features will not ");
        resp.getWriter().append("be available.");
        resp.setContentType("text/plain");
    }

    private boolean isError(HttpServletRequest req) {
        return req.getParameter(ERROR_PARAM) != null;
    }

    private String extractApiName(String pathInfo) {
        return pathInfo.replaceAll(".*/", "");
    }
}
