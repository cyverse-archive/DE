package org.iplantc.de.server.oauth;

import org.iplantc.de.server.DiscoveryEnvironmentProperties;
import org.iplantc.de.server.auth.UrlConnector;

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

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class OAuthCallbackServlet extends HttpServlet {


    /**
     * An enumerated type for error codes that can be sent back to the main page of the DE.
     */
    private enum ErrorCodes {
        ERR_INVALID_REQUEST("invalid_request", "The authorization request sent to the OAuth server by the DE was invalid."),
        ERR_UNAUTHORIZED_CLIENT("unauthorized_client", "The DE is not authorized to request access to the API."),
        ERR_ACCESS_DENIED("access_denied", "Either the OAuth server or the user denied access."),
        ERR_UNSUPPORTED_RESPONSE_TYPE("unsupported_response_type", "The OAuth server doesn't support the requested response type."),
        ERR_INVALID_SCOPE("invalid_scope", "The OAuth server doesn't support the requested scope."),
        ERR_SERVER("server_error", "The OAuth server encountered an error."),
        ERR_TEMPORARILY_UNAVAILABLE("temporarily_unavailable", "The OAuth server is temporarily unavailable."),
        ERR_OAUTH_CONFIG("invalid_oauth_config", "The DE's OAuth configuration is invalid."),
        ERR_MISSING_AUTH_CODE("no_auth_code_provided", "No authorization code or error code was sent by the OAuth server."),
        ERR_MISSING_STATE("no_state_id_provided", "No state information was sent by the OAuth server."),
        ERR_SERVICE("general_service_error", "The DE service encountered an error.");

        private final String errorCode;
        public String getErrorCode() { return errorCode; }

        private final String errorDescription;
        public String getErrorDescription() { return errorDescription; }

        private ErrorCodes(final String errorCode, final String errorDescription) {
            this.errorCode = errorCode;
            this.errorDescription = errorDescription;
        }

        public static ErrorCodes fromString(final String errorCode) {
            for (ErrorCodes code : ErrorCodes.values()) {
                if (code.errorCode.equals(errorCode)) {
                    return code;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return errorCode;
        }
    }

    private static final String CALLBACK_PATH = "oauth/access-code";
    private static final String ERROR_PARAM = "error";
    private static final String ERROR_DESCRIPTION_PARAM = "error_description";
    private static final String ERROR_URI_PARAM = "error_uri";
    private static final String AUTH_CODE_PARAM = "code";
    private static final String STATE_PARAM = "state";
    private static final String API_NAME_PARAM = "api_name";

    private static final Logger LOG = Logger.getLogger(OAuthCallbackServlet.class);

    private DiscoveryEnvironmentProperties deProps;
    private UrlConnector urlConnector;
    // Default descriptions for request error codes.

    @Override
    public void init() throws ServletException {
        super.init();
        if (deProps == null) {
            try {
                deProps = DiscoveryEnvironmentProperties.getDiscoveryEnvironmentProperties();
            } catch (IOException e) {
                throw new ServletException(e);
            }
        }
    }

    protected void setDeProps(final DiscoveryEnvironmentProperties deProps) { this.deProps = deProps; }

    protected void setUrlConnector(final UrlConnector urlConnector) { this.urlConnector = urlConnector; }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
            throws ServletException, IOException {
        final AuthorizationResponse authResponse = new AuthorizationResponse(req);
        if (authResponse.isError()) {
            authResponse.authorizationErrorRedirect(resp);
        } else {
            callAuthCodeService(authResponse, req, resp);
        }
    }

    private void callAuthCodeService(final AuthorizationResponse authResponse,
                                     final HttpServletRequest req,
                                     final HttpServletResponse resp)
            throws IOException {
        final HttpClient client = new DefaultHttpClient();
        try {
            final HttpGet request = urlConnector.getRequest(req, serviceCallbackUrl(authResponse));
            final HttpResponse response = client.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = readResponse(response);
            if (statusCode < 200 || statusCode > 299) {
                LOG.warn("error while trying to obtain access token: " + responseBody);
                authResponse.serviceErrorRedirect(resp);
            } else {
                resp.sendRedirect(authorizationSuccessRedirectUrl(req, responseBody));
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
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

    private String serviceCallbackUrl(final AuthorizationResponse authResponse) {
        try {
            final String baseUrl = deProps.getProtectedDonkeyBaseUrl() + CALLBACK_PATH;
            return new URIBuilder(baseUrl + "/" + authResponse.getApiName())
                    .addParameter(AUTH_CODE_PARAM, authResponse.getAuthCode())
                    .addParameter(STATE_PARAM, authResponse.getState())
                    .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the auth code service callback URI", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * The authorization response is really a GET request initiated by a redirection from the OAuth
     * server. This class stores information about and the authorization response and provides some
     * methods to perform a few tasks using the information from the authorization response.
     */
    private class AuthorizationResponse {
        private final String apiName;
        public String getApiName() { return apiName; }

        private final String authCode;
        public String getAuthCode() { return authCode; }

        private final String state;
        private String getState() { return state; }

        private final ErrorCodes errorCode;
        private final String errorDescription;
        private final String errorUri;
        private final String contextPath;

        public AuthorizationResponse(HttpServletRequest request) {
            apiName = request.getPathInfo().replaceAll(".*/", "");
            authCode = request.getParameter(AUTH_CODE_PARAM);
            state = request.getParameter(STATE_PARAM);
            errorDescription = request.getParameter(ERROR_DESCRIPTION_PARAM);
            errorUri = request.getParameter(ERROR_URI_PARAM);
            errorCode = determineErrorCode(request.getParameter(ERROR_PARAM));
            contextPath = request.getContextPath();
        }

        private ErrorCodes determineErrorCode(String providedErrorCode) {
            return !Strings.isNullOrEmpty(providedErrorCode) ? ErrorCodes.fromString(providedErrorCode)
                 : Strings.isNullOrEmpty(apiName)            ? ErrorCodes.ERR_OAUTH_CONFIG
                 : Strings.isNullOrEmpty(authCode)           ? ErrorCodes.ERR_MISSING_AUTH_CODE
                 : Strings.isNullOrEmpty(state)              ? ErrorCodes.ERR_MISSING_STATE
                 :                                             null;
        }

        private String getErrorDescription() {
            return !Strings.isNullOrEmpty(errorDescription) ? errorDescription
                 : errorCode != null                        ? errorCode.getErrorDescription()
                 :                                            null;
        }

        public boolean isError() {
            return errorCode != null;
        }

        public void authorizationErrorRedirect(final HttpServletResponse response) throws IOException {
            logAuthorizationErrorRedirect();
            response.sendRedirect(authorizationErrorRedirectUrl());
        }

        private String authorizationErrorRedirectUrl() {
            try {
                final URIBuilder uriBuilder = new URIBuilder(contextPath);
                addParameter(uriBuilder, ERROR_PARAM, errorCode.getErrorCode());
                addParameter(uriBuilder, ERROR_DESCRIPTION_PARAM, getErrorDescription());
                addParameter(uriBuilder, ERROR_URI_PARAM, errorUri);
                addParameter(uriBuilder, API_NAME_PARAM, apiName);
                return uriBuilder.toString();
            } catch (URISyntaxException e) {
                LOG.error("unable to build the authorization error redirect URL", e);
                throw new RuntimeException(e);
            }
        }

        public void serviceErrorRedirect(final HttpServletResponse response) throws IOException {
            response.sendRedirect(serviceErrorRedirectUrl());
        }

        private String serviceErrorRedirectUrl() {
            try {
                final ErrorCodes errorCode = ErrorCodes.ERR_SERVICE;
                final URIBuilder uriBuilder = new URIBuilder(contextPath);
                addParameter(uriBuilder, ERROR_PARAM, errorCode.getErrorCode());
                addParameter(uriBuilder, ERROR_DESCRIPTION_PARAM, errorCode.getErrorDescription());
                addParameter(uriBuilder, API_NAME_PARAM, apiName);
                return uriBuilder.toString();
            } catch (URISyntaxException e) {
                LOG.error("unable to build the service error redirect URL", e);
                throw new RuntimeException(e);
            }
        }

        private void addParameter(final URIBuilder uriBuilder, final String name, final String value) {
            if (!Strings.isNullOrEmpty(value)) {
                uriBuilder.addParameter(name, value);
            }
        }

        private void logAuthorizationErrorRedirect() {
            if (errorCode == ErrorCodes.ERR_ACCESS_DENIED) {
                LOG.warn("access denied by user or server.");
            } else {
                LOG.error("unable to obtain authorization: " + errorCode);
            }
        }
    }
}
