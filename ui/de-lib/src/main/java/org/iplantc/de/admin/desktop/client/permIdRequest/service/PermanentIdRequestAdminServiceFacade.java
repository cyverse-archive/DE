package org.iplantc.de.admin.desktop.client.permIdRequest.service;

import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author sriram
 * 
 */
public interface PermanentIdRequestAdminServiceFacade {

    public final String PERMID_REQUEST = "org.iplantc.services.permIdRequests";

    public final String PERMID_ADMIN_REQUEST = "org.iplantc.services.admin.permIdRequests";

    void getPermanentIdRequests(AsyncCallback<String> callback);

    void updatePermanentIdRequestStatus(String requestId,
                                        PermanentIdRequestUpdate status,
                                        AsyncCallback<String> callback);

    void createPermanentId(String id, AsyncCallback<String> asyncCallback);

}
