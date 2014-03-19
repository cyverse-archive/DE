package org.iplantc.de.server;

import org.iplantc.de.shared.services.HTTPPart;

import org.apache.http.entity.mime.content.AbstractContentBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.IOException;

/**
 * Used to build the content bodies for multipart HTTP requests.
 * 
 * @author Dennis Roberts
 */
public class MultipartBodyFactory {
    /**
     * Builds content bodies for parts of multipart requests.
     * 
     * @param part the HTTP part.
     * @return the body.
     * @throws IOException if the body can't be created.
     */
    public static AbstractContentBody createBody(HTTPPart part) throws IOException {
        if (part instanceof FileHTTPPart) {
            FileHTTPPart file = (FileHTTPPart)part;
            return new StreamingFileBody(file.getInputStream(), file.getFilename(), file.getMimeType(),
                    file.getLength());
        } else {
            return new StringBody(part.getBody());
        }
    }
}
