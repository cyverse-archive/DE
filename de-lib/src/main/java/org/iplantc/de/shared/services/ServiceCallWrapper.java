package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Provides a wrapper about remote service calls.
 */
public class ServiceCallWrapper extends BaseServiceCallWrapper implements IsSerializable {
    private static final long serialVersionUID = 8930304388034394781L;
    private String body = "";

    public ServiceCallWrapper() {
    }

    public ServiceCallWrapper(String address) {
        super(address);
    }

    public ServiceCallWrapper(Type type, String address) {
        super(type, address);
    }

    public ServiceCallWrapper(Type type, String address, String body) {
        this(type, address);
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
