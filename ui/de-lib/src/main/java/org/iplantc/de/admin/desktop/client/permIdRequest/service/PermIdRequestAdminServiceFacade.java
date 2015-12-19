package org.iplantc.de.admin.desktop.client.permIdRequest.service;

import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PermIdRequestAdminServiceFacade {

    public final String PERMID_REQUEST = "org.iplantc.services.permIdRequests";

    public final String PERMID_ADMIN_REQUEST = "org.iplantc.services.admin.permIdRequests";

    void getPermanentIdRequests(AsyncCallback<String> callback);

    void updatePermanentIdRequestStatus(PermanentIdRequestStatus status, AsyncCallback<String> callback);

    void getPermanentIdRequestStatuses(AsyncCallback<String> callback);

}
