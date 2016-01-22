package org.iplantc.de.client.services;

import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PermIdRequestUserServiceFacade {

    public final String PERMID_REQUEST = "org.iplantc.services.permIdRequests";

    void requestPermId(String uuid, PermanentIdRequestType type, AsyncCallback<String> callback);

}
