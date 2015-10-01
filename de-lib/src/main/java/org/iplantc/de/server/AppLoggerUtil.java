package org.iplantc.de.server;

import org.iplantc.de.shared.services.BaseServiceCallWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.cas.authentication.CasAuthenticationToken;

import java.util.Map;

/**
 * Utility for creating objects for logging context.
 * @author jstroot
 */
public class AppLoggerUtil {

    private final Logger API_METRICS_LOG = LoggerFactory.getLogger(AppLoggerConstants.API_METRICS_LOGGER);
    private static AppLoggerUtil INSTANCE;

    AppLoggerUtil() {
    }

    public static AppLoggerUtil getInstance(){
        if(INSTANCE == null){
            INSTANCE = new AppLoggerUtil();
        }
        return INSTANCE;
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

    public String createMdcRequestMapAsJsonString(HttpRequestBase request) throws JsonProcessingException {
        Map<String, Object> requestMap = createMdcRequestMap(request);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(requestMap);
    }

    public Map<String, Object> createMdcRequestMap(HttpRequestBase request) {
        String body =  MDC.get("request-body");
        Map<String, Object> requestMap = Maps.newHashMap();
        if(!Strings.isNullOrEmpty(body)){
            requestMap.put("body", body);
        }
        MDC.remove("request-body");

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

    public void addBodyToMdcRequestMap(final String body){
        if(API_METRICS_LOG.isTraceEnabled()
               && !Strings.isNullOrEmpty(body)){
            MDC.put("request-body", body);
        }
    }

    public String createMdcResponseMapJson(HttpResponse response,
                                                    BaseServiceCallWrapper.Type type,
                                                    String endpoint,
                                                    String body,
                                                    long requestTime) throws JsonProcessingException {
        final Map<String, Object> map = updateMdcResponseMap(response, type, endpoint, body, requestTime);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(map);
    }

    public Map<String, Object> updateMdcResponseMap(HttpResponse response,
                                                    BaseServiceCallWrapper.Type type,
                                                    String endpoint,
                                                    String body,
                                                    long requestTime) {
        Map<String, Object> responseMap = Maps.newHashMap();

        responseMap.put("headers", getHeaderMap(response.getAllHeaders()));
        responseMap.put("response-method", type.toString());
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

    private Map<String, String> getQueryStringMap(String queryString){
        final Map<String, String> queryStringMap = Maps.newHashMap();
        String[] params = queryString.split("&");
        for(String param : params){
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            queryStringMap.put(name, value);
        }
        return queryStringMap;
    }

    private Map<String, String> getHeaderMap(Header[] headers){
        Map<String, String> headerMap = Maps.newHashMap();
        for(Header header : headers){
            headerMap.put(header.getName(), header.getValue());
        }
        return headerMap;
    }

}
