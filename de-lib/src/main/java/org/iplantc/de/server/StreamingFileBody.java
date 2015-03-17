package org.iplantc.de.server;

import org.apache.http.entity.mime.content.InputStreamBody;

import java.io.InputStream;

/**
 * An input stream body that is aware of the content length.
 * 
 * @author Dennis Roberts
 */
public class StreamingFileBody extends InputStreamBody {
    /**
     * The length of the body in bytes.
     */
    private long length;

    /**
     * Create a new body.
     * 
     * @param in the input stream.
     * @param filename the name of the file.
     * @param length the content length;
     */
    public StreamingFileBody(InputStream in, String filename, long length) {
        super(in, filename);
        this.length = length;
    }

    /**
     * Create a new body.
     * 
     * @param in the input stream.
     * @param filename the name of the file.
     * @param mimeType the MIME type of the file.
     * @param length the content length;
     */
    public StreamingFileBody(InputStream in, String filename, String mimeType, long length) {
        super(in, mimeType, filename);
        this.length = length;
    }

    /**
     * @return the length of the body in bytes.
     */
    @Override
    public long getContentLength() {
        return length;
    }
}
