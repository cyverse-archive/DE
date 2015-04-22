package org.iplantc.de.server.controllers;

import org.iplantc.de.server.util.CasUtils;

import com.google.common.collect.Sets;

import org.apache.http.client.utils.URIBuilder;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * Temporarily turned into POJO
 * TODO lookup error response handling
 * @author jstroot
 */
@Controller
public class FileUploadController {

    private final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

    private static final Set<String> HEADERS_TO_SKIP = Sets.newHashSet("content-length");
    @Value("${org.iplantc.services.file-io.secured.file-upload}") String securedFileUploadUrl;

    @RequestMapping(value = "/de/secured/fileUpload", method = RequestMethod.POST)
    public void doSecureFileUpload(@RequestParam("user") final String name,
                                   @RequestParam("dest") final String dest,
                                   @RequestParam("file") final MultipartFile file,
                                   MultipartHttpServletRequest request) throws IOException {
//        final RequestCallback requestCallback = new RequestCallback() {
//            @Override
//            public void doWithRequest(final ClientHttpRequest request) throws IOException {
//                request.getHeaders().add("Content-type", "application/octet-stream");
//                IOUtils.copy(file.getInputStream(), request.getBody());
//            }
//        };

        final RestTemplate restTemplate = new RestTemplate();
        final FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        formHttpMessageConverter.addPartConverter(new MultiPartFileMessageConverter());

        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(formHttpMessageConverter);

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setBufferRequestBody(false);
        restTemplate.setRequestFactory(requestFactory);
        final String uploadUrl = buildUploadUrl(securedFileUploadUrl,
                                                dest,
                                                request);
        // TODO Look into using uri template
        LOG.debug("Upload Url {}", uploadUrl);
//        final String uriTemplate = securedFileUploadUrl + "?ip-address={ipAddress}&proxyToken={token}&dest={dest}";
        final String uriTemplate = securedFileUploadUrl + "?proxyToken={token}&dest={dest}";

        AttributePrincipal principal = CasUtils.attributePrincipalFromServletRequest(request);
        final String ipAddress = request.getRemoteAddr();
        String proxyToken = principal.getProxyTicketFor(extractServiceName(new URL(securedFileUploadUrl)));
        HttpHeaders headers = copyHeaders(request);
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        final ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
//        final InputStreamResource inputStreamResource = new InputStreamResource(file.getInputStream()){
//            @Override
//            public String getFilename() {
//                return file.getOriginalFilename();
//            }
//        };
        map.add("file", byteArrayResource);
//        map.add("file", inputStreamResource);
//        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map);
//        final ResponseEntity<String> exchange;
//        try {
//            exchange = restTemplate.exchange(uriTemplate, HttpMethod.POST, requestEntity, String.class, ipAddress, proxyToken, dest);
//            LOG.debug("Result2 = {}", exchange.toString());
//        } catch (RestClientException e) {
//            e.printStackTrace();
//        }
        final ResponseEntity<String> stringResponseEntity = restTemplate.postForEntity(uriTemplate, requestEntity, String.class, proxyToken, dest);

        LOG.debug("result = {}", stringResponseEntity.toString());
//        return result;
    }

    private HttpHeaders copyHeaders(MultipartHttpServletRequest request) {
        Enumeration<String> names = request.getHeaderNames();
        HttpHeaders headers = new HttpHeaders();
        while (names.hasMoreElements()) {
            String name = names.nextElement();

            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                String value = values.nextElement();
                if(!HEADERS_TO_SKIP.contains(name.toLowerCase())) {
                    headers.add(name, value);
                }
            }
        }
        return headers;
    }

    private String buildUploadUrl(final String securedFileUploadUrl,
                                  final String dest,
                                  final MultipartHttpServletRequest request) {

        AttributePrincipal principal = CasUtils.attributePrincipalFromServletRequest(request);
        String uploadUrl = null;
        try {
            String proxyToken = principal.getProxyTicketFor(extractServiceName(new URL(securedFileUploadUrl)));
            uploadUrl = new URIBuilder(securedFileUploadUrl)
                                   .addParameter("proxyToken", proxyToken)
                                   .addParameter("dest", dest).build().toString();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }

        return uploadUrl;
    }

    private String extractServiceName(URL url) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(url.getProtocol()).append("://");
        builder.append(url.getHost());
        if (url.getPort() >= 0) {
            builder.append(":").append(url.getPort());
        }
        return builder.toString();
    }


    private class MultiPartFileMessageConverter extends AbstractHttpMessageConverter<MultipartFile> {

        @Override
        protected boolean supports(Class<?> clazz) {
            return MultipartFile.class.isAssignableFrom(clazz);
        }

        @Override
        protected MultipartFile readInternal(Class<? extends MultipartFile> clazz,
                                             HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return null;
        }

        @Override
        protected void writeInternal(MultipartFile multipartFile,
                                     HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            final int length = StreamUtils.copy(multipartFile.getInputStream(), outputMessage.getBody());
            LOG.info("File size = {}", length);
        }
    }
}
