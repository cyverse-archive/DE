package org.iplantc.de.admin.desktop.client.permIdRequest.service;

import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PermIdRequestAdminServiceFacade {

    void getPermanentIdRequests(AsyncCallback<String> callback);

    void getMetadata(String uuid, AsyncCallback<String> callback);

    void updateMetatdata(String uuid, String metadata, AsyncCallback<String> callback);

    void updatePermanentIdRequestStatus(PermanentIdRequestStatus status, AsyncCallback<String> callback);

    void getPermanentIdRequestStatuses(AsyncCallback<String> callback);

}
