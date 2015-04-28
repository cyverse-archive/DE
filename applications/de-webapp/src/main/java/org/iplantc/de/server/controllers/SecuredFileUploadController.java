package org.iplantc.de.server.controllers;

import org.iplantc.de.server.util.CasUtils;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
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
import java.net.URL;

/**
 * Performs secured file uploads.
 *
 * @author jstroot
 */
@Controller
public class SecuredFileUploadController {

    private final Logger LOG = LoggerFactory.getLogger(SecuredFileUploadController.class);

    @Value("${org.iplantc.services.file-io.secured.file-upload}") String securedFileUploadUrl;

    @RequestMapping(value = "/de/secured/fileUpload", method = RequestMethod.POST)
    public ResponseEntity<Object> doSecureFileUpload(@RequestParam("user") final String name,
                                   @RequestParam("dest") final String dest,
                                   @RequestParam("file") final MultipartFile file,
                                   MultipartHttpServletRequest request) throws IOException {

        final RestTemplate restTemplate = new RestTemplate();
        // Create special part converter and add it to custom form message converter
        final FormHttpMessageConverter customFormMsgConverter = new FormHttpMessageConverter();
        customFormMsgConverter.addPartConverter(new MultiPartFileMessageConverter());

        // Add all necessary message converters
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(customFormMsgConverter);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        // Create and add request factory
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

        // Create multi value map for multi-part request
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        final ByteArrayResource byteArrayResource = new ByteArrayResource(file.getBytes()){
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
        map.add("file", byteArrayResource);

        // Create request entity with multi-part map
        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map);

        // Create URI template for REST request
        final String uriTemplate = securedFileUploadUrl + "?proxyToken={token}&dest={dest}";
        AttributePrincipal principal = CasUtils.attributePrincipalFromServletRequest(request);
        String proxyToken = principal.getProxyTicketFor(extractServiceName(new URL(securedFileUploadUrl)));

        //  Make request
        final ResponseEntity<Object> stringResponseEntity = restTemplate.postForEntity(uriTemplate, requestEntity, Object.class, proxyToken, dest);

        LOG.debug("result = {}", stringResponseEntity.toString());
        return stringResponseEntity;
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
