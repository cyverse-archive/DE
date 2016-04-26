package org.iplantc.de.server;

import static org.iplantc.de.server.AppLoggerConstants.REQUEST_ID_HEADER;
import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility for creating objects for logging context.
 * @author jstroot
 */
public class AppLoggerUtil {

    private static AppLoggerUtil INSTANCE;
    private final Logger API_METRICS_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);

    AppLoggerUtil() {
    }

    public static AppLoggerUtil getInstance(){
        if(INSTANCE == null){
            INSTANCE = new AppLoggerUtil();
        }
        return INSTANCE;
    }

    /**
     * Adds the specified header name and ip address to the given request, performing validation on
     * the given ipAddress.
     *
     * @param request to which the new header is added.
     * @param headerName the name of the new header.
     * @param ipAddress the IP address to add to the new header.
     * @param <T> Basic request object
     * @return the given request with the new header added.
     */
    public <T extends HttpRequestBase> T addIpHeader(T request,
                                                     final String headerName,
                                                     final String ipAddress) {
        Preconditions.checkArgument(InetAddresses.isInetAddress(ipAddress),
                                    "The given ip address is invalid: " + ipAddress);

        request.addHeader(headerName, ipAddress);

        return request;
    }

    public <T extends HttpRequestBase> T addRequestIdHeader(T request) {
        final String requestId = "UI-" + UUID.randomUUID().toString();
        API_METRICS_LOG.trace("Adding unique request id to constructed request. Request-Id = {}", requestId);
        request.addHeader(REQUEST_ID_HEADER, requestId);
        return request;
    }

    public <T extends HttpResponse> T copyRequestIdHeader(HttpRequestBase request, T response) {

        final List<Header> headers = Arrays.asList(request.getHeaders(REQUEST_ID_HEADER));
        if(headers.size() > 1
            || headers.isEmpty()){
            String message = headers.isEmpty()
                                 ? "Request should have a '" + REQUEST_ID_HEADER + "' header, but does not. -> "
                                 : "Request has too many '" + REQUEST_ID_HEADER + "' headers. -> ";
            throw new IllegalStateException(message + headers.toString());
        }
        response.addHeader(REQUEST_ID_HEADER, headers.get(0).getValue());
        return response;
    }

    public Map<String, Object> createMdcRequestMap(final HttpRequestBase request,
                                                   final String body) {
        Map<String, Object> requestMap = Maps.newHashMap();
        if(API_METRICS_LOG.isTraceEnabled()
               && !Strings.isNullOrEmpty(body)){
            requestMap.put("body", body);
        }

        requestMap.put("uri", request.getURI().getPath());
        requestMap.put("path-info", request.getURI().getPath());
        requestMap.put("protocol", request.getProtocolVersion().toString());
        requestMap.put("headers", getHeaderMap(request.getAllHeaders()));
        requestMap.put("server-port", Integer.toString(request.getURI().getPort()));
        requestMap.put("server-name", request.getURI().getHost());
        requestMap.put("query-string", request.getURI().getQuery());
        requestMap.put("query-params", getQueryStringMap(request.getURI().getQuery()));
        requestMap.put("scheme", request.getURI().getScheme());
        requestMap.put("request-method", request.getMethod());
        return requestMap;
    }

    public String createMdcRequestMapAsJsonString(final HttpRequestBase request) throws JsonProcessingException {
        Map<String, Object> requestMap = createMdcRequestMap(request, null);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(requestMap);
    }

    public Map<String, Object> createMdcResponseMap(final HttpResponse response,
                                                    final BaseServiceCallWrapper.Type type,
                                                    final String endpoint,
                                                    final String body,
                                                    final long requestTime) {
        Map<String, Object> responseMap = Maps.newHashMap();

        responseMap.put("headers", getHeaderMap(response.getAllHeaders()));
        responseMap.put("request-method", type.toString());
        responseMap.put("status", response.getStatusLine().getStatusCode());
        responseMap.put("uri", endpoint);
        responseMap.put("path-info", endpoint);
        responseMap.put("request-time", requestTime);

        if(API_METRICS_LOG.isTraceEnabled()
               && !Strings.isNullOrEmpty(body)){
            responseMap.put("body", body);
        }
        return responseMap;
    }

    public String createMdcResponseMapJson(final HttpResponse response,
                                           final BaseServiceCallWrapper.Type type,
                                           final String endpoint,
                                           final String body,
                                           final long requestTime) throws JsonProcessingException {
        final Map<String, Object> map = createMdcResponseMap(response, type, endpoint, body, requestTime);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }

    public Map<String, Object> createUserInfoMap(CasAuthenticationToken authenticationToken){
        final AttributePrincipal principal = authenticationToken.getAssertion().getPrincipal();
        final Map<String, Object> principalAttributes = principal.getAttributes();
        Map<String, Object> userInfoMap = Maps.newHashMap();
        userInfoMap.put("email", principalAttributes.get("email"));
        userInfoMap.put("first-name", principalAttributes.get("firstName"));
        userInfoMap.put("last-name", principalAttributes.get("lastName"));
        userInfoMap.put("user", principal.getName());

        return userInfoMap;
    }

    private Map<String, String> getHeaderMap(final Header[] headers){
        Map<String, String> headerMap = Maps.newHashMap();
        for(Header header : headers){
            headerMap.put(header.getName(), header.getValue());
        }
        return headerMap;
    }

    private Map<String, String> getQueryStringMap(final String queryString){
        final Map<String, String> queryStringMap = Maps.newHashMap();
        String[] params = queryString.split("&");
        for(String param : params){
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            queryStringMap.put(name, value);
        }
        return queryStringMap;
    }

}
