package org.iplantc.de.server;

import org.iplantc.de.shared.services.HTTPPart;

import java.io.InputStream;

public class FileHTTPPart extends HTTPPart {
    private static final long serialVersionUID = -2662589032061446564L;
    private InputStream inputStream;
    private String filename;
    private String mimeType;
    private long length;

    public FileHTTPPart() {
    }

    public FileHTTPPart(String body, String disposition) {
        super(body, disposition);
        inputStream = null;
        length = 0;
    }

    public FileHTTPPart(InputStream is, String name, String filename, String mimeType, long length) {
        this.inputStream = is;
        this.name = name;
        this.filename = filename;
        this.mimeType = mimeType;
        this.length = length;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getFilename() {
        return filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getLength() {
        return length;
    }
}
