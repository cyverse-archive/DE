package org.iplantc.de.server;

import com.google.common.base.Strings;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class OAuthCallbackServlet extends HttpServlet {

    private static final String CALLBACK_PATH = "oauth/access-code";
    private static final String ERROR_PARAM = "error";
    private static final String AUTH_CODE_PARAM = "code";
    private static final String REDIRECT_URI_PARAM = "redirect_uri";
    private static final String AUTH_DENIED_PARAM = "auth-denied";
    private static final String SERVICE_NAME = "Agave";

    private static final Logger LOG = Logger.getLogger(OAuthCallbackServlet.class);

    private DiscoveryEnvironmentProperties deProps;
    protected void setDeProps(final DiscoveryEnvironmentProperties deProps) { this.deProps = deProps; }

    private UrlConnector urlConnector;
    protected void setUrlConnector(final UrlConnector urlConnector) { this.urlConnector = urlConnector; }

    public OAuthCallbackServlet() {}

    @Override
    public void init() throws ServletException {
        super.init();
        if (deProps == null) {
            deProps = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties(getServletContext());
        }
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        if (isError(req)) {
            authorizationErrorRedirect(req, resp);
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

        final String redirectUri = rebuildRedirectUri(req);
        LOG.warn("Redirect URI: " + redirectUri);

        final HttpGet request = urlConnector.getRequest(req, serviceCallbackUrl(apiName, authCode, redirectUri));
        final HttpClient client = new DefaultHttpClient();
        final HttpResponse response = client.execute(request);

        final int statusCode = response.getStatusLine().getStatusCode();
        final String responseBody = readResponse(response);
        if (statusCode < 200 || statusCode > 299) {
            LOG.warn("error while trying to obtain access token: " + responseBody);
            authorizationErrorRedirect(req, resp);
        }

        resp.sendRedirect(authorizationSuccessRedirectUrl(req, responseBody));
    }

    private String readResponse(HttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        return IOUtils.toString(entity.getContent());
    }

    private String authorizationSuccessRedirectUrl(HttpServletRequest req, String responseBody) {
        try {
            JSONObject json = JSONObject.fromObject(responseBody);
            String queryString = json.getString("state_info");
            return new URIBuilder(req.getContextPath())
                    .setQuery(queryString)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the authorization success redirect URL", e);
            throw new RuntimeException(e);
        }
    }

    private String rebuildRedirectUri(HttpServletRequest req) {
        final String replacementPattern = "\\Q" + req.getContextPath() + "\\E$";
        final String baseUrl = deProps.getDeBaseUrl().replaceAll(replacementPattern, "");
        return baseUrl + req.getRequestURI();
    }

    private String serviceCallbackUrl(String apiName, String authCode, String redirectUri) {
        try {
            return new URIBuilder(deProps.getProtectedDonkeyBaseUrl() + CALLBACK_PATH + "/" + apiName)
                    .addParameter(AUTH_CODE_PARAM, authCode)
                    .addParameter(REDIRECT_URI_PARAM, redirectUri)
                    .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the auth code service callback URI", e);
            throw new RuntimeException(e);
        }
    }

    private void authorizationErrorRedirect(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {
        final String errorCode = req.getHeader(ERROR_PARAM);
        LOG.warn("unable to obtain authorization: " + errorCode);
        resp.sendRedirect(authorizationErrorRedirectUrl(req));
    }

    private String authorizationErrorRedirectUrl(final HttpServletRequest req) {
        try {
            return new URIBuilder(req.getContextPath())
                    .addParameter(AUTH_DENIED_PARAM, SERVICE_NAME)
                    .build()
                    .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the authorization error redirect URL", e);
            throw new RuntimeException(e);
        }
    }

    private boolean isError(HttpServletRequest req) {
        return req.getParameter(ERROR_PARAM) != null;
    }

    private String extractApiName(String pathInfo) {
        return pathInfo.replaceAll(".*/", "");
    }
}
