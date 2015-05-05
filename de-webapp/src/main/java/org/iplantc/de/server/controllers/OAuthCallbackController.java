package org.iplantc.de.server.controllers;

import org.iplantc.de.server.auth.UrlConnector;

import com.google.common.base.Strings;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jstroot
 * @author Dennis Roberts
 */
@Controller
public class OAuthCallbackController {

    /**
     * The authorization response is really a GET request initiated by a redirection from the OAuth
     * server. This class stores information about and the authorization response and provides some
     * methods to perform a few tasks using the information from the authorization response.
     */
    private class AuthorizationResponse {
        private final String apiName;
        private final String authCode;
        private final String contextPath;
        private final ErrorCodes errorCode;
        private final String errorDescription;
        private final String errorUri;
        private final String state;

        public AuthorizationResponse(HttpServletRequest request, String apiName, String contextPath) {
            this.apiName = apiName;
            authCode = request.getParameter(AUTH_CODE_PARAM);
            state = request.getParameter(STATE_PARAM);
            errorDescription = request.getParameter(ERROR_DESCRIPTION_PARAM);
            errorUri = request.getParameter(ERROR_URI_PARAM);
            errorCode = determineErrorCode(request.getParameter(ERROR_PARAM));
            this.contextPath = contextPath;
        }

        public void authorizationErrorRedirect(final HttpServletResponse response) throws IOException {
            logAuthorizationErrorRedirect();
            response.sendRedirect(authorizationErrorRedirectUrl());
        }

        public String getApiName() {
            return apiName;
        }

        public String getAuthCode() {
            return authCode;
        }

        public boolean isError() {
            return errorCode != null;
        }

        public void serviceErrorRedirect(final HttpServletResponse response) throws IOException {
            response.sendRedirect(serviceErrorRedirectUrl());
        }

        private void addParameter(final URIBuilder uriBuilder, final String name,
                                  final String value) {
            if (!Strings.isNullOrEmpty(value)) {
                uriBuilder.addParameter(name, value);
            }
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

        private ErrorCodes determineErrorCode(String providedErrorCode) {
            return !Strings.isNullOrEmpty(providedErrorCode) ? ErrorCodes.fromString(providedErrorCode)
                       : Strings.isNullOrEmpty(apiName) ? ErrorCodes.ERR_OAUTH_CONFIG
                             : Strings.isNullOrEmpty(authCode) ? ErrorCodes.ERR_MISSING_AUTH_CODE
                                   : Strings.isNullOrEmpty(state) ? ErrorCodes.ERR_MISSING_STATE
                                         : null;
        }

        private String getErrorDescription() {
            return !Strings.isNullOrEmpty(errorDescription) ? errorDescription
                       : errorCode != null ? errorCode.getErrorDescription()
                             : null;
        }

        private String getState() {
            return state;
        }

        private void logAuthorizationErrorRedirect() {
            if (errorCode == ErrorCodes.ERR_ACCESS_DENIED) {
                LOG.warn("access denied by user or server.");
            } else {
                LOG.error("unable to obtain authorization: {}", errorCode);
            }
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
    }

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
        private final String errorDescription;

        ErrorCodes(final String errorCode, final String errorDescription) {
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

        public String getErrorCode() {
            return errorCode;
        }

        public String getErrorDescription() {
            return errorDescription;
        }

        @Override
        public String toString() {
            return errorCode;
        }
    }

    private static final String API_NAME_PARAM = "api_name";
    private static final String AUTH_CODE_PARAM = "code";
    private static final String CALLBACK_PATH = "oauth/access-code";
    private static final String ERROR_DESCRIPTION_PARAM = "error_description";
    private static final String ERROR_PARAM = "error";
    private static final String ERROR_URI_PARAM = "error_uri";
    private static final String STATE_PARAM = "state";
    private static final String CONTEXT_PATH = "/de";

    private final Logger LOG = LoggerFactory.getLogger(OAuthCallbackController.class);

    @Value("${org.iplantc.discoveryenvironment.muleServiceBaseUrl}") private String serviceUrl;
    @Autowired private UrlConnector urlConnector;

    @RequestMapping(CONTEXT_PATH + "/oauth/callback/{api_name}")
    public void handleCallback(final HttpServletRequest req,
                               final HttpServletResponse resp,
                               @PathVariable final String api_name) throws IOException {

        final AuthorizationResponse authResponse = new AuthorizationResponse(req, api_name, CONTEXT_PATH);
        if (authResponse.isError()) {
            authResponse.authorizationErrorRedirect(resp);
        } else {
            callAuthCodeService(authResponse, req, resp);
        }
    }

    private String authorizationSuccessRedirectUrl(HttpServletRequest req, String responseBody) {
        try {
            JSONObject json = JSONObject.fromObject(responseBody);
            String queryString = json.getString("state_info");
            return new URIBuilder(CONTEXT_PATH)
                       .setQuery(queryString)
                       .build()
                       .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the authorization success redirect URL", e);
            throw new RuntimeException(e);
        }
    }

    private void callAuthCodeService(final AuthorizationResponse authResponse,
                                     final HttpServletRequest req,
                                     final HttpServletResponse resp)
        throws IOException {
        final CloseableHttpClient client = HttpClients.createDefault();
        try {
            final HttpGet request = urlConnector.getRequest(req, serviceCallbackUrl(authResponse));
            final HttpResponse response = client.execute(request);
            final int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = readResponse(response);
            if (statusCode < 200 || statusCode > 299) {
                LOG.warn("error while trying to obtain access token: {}", responseBody);
                authResponse.serviceErrorRedirect(resp);
            } else {
                resp.sendRedirect(authorizationSuccessRedirectUrl(req, responseBody));
            }
        } finally {
            IOUtils.closeQuietly(client);
        }
    }

    private String readResponse(HttpResponse response) throws IOException {
        final HttpEntity entity = response.getEntity();
        return IOUtils.toString(entity.getContent());
    }

    private String serviceCallbackUrl(final AuthorizationResponse authResponse) {
        try {
            final String baseUrl = serviceUrl + CALLBACK_PATH;
            return new URIBuilder(baseUrl + "/" + authResponse.getApiName())
                       .addParameter(AUTH_CODE_PARAM, authResponse.getAuthCode())
                       .addParameter(STATE_PARAM, authResponse.getState())
                       .toString();
        } catch (URISyntaxException e) {
            LOG.error("unable to build the auth code service callback URI", e);
            throw new RuntimeException(e);
        }
    }

}
