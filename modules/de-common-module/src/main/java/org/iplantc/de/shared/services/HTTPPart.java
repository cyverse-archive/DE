package org.iplantc.de.shared.services;

import java.io.Serializable;

/**
 * Models a portion of multi-part request submission.
 */
public class HTTPPart implements Serializable {
    private static final long serialVersionUID = -2662589032061446564L;
    protected String body = new String();
    protected String name = new String();

    public HTTPPart() {
    }

    public HTTPPart(String body, String name) {
        this.body = body;
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public String getName() {
        return name;
    }
}
