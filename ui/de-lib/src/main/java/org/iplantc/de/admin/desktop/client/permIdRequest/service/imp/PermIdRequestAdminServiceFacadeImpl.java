package org.iplantc.de.admin.desktop.client.permIdRequest.service.imp;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermIdRequestAdminServiceFacade;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestStatus;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class PermIdRequestAdminServiceFacadeImpl implements PermIdRequestAdminServiceFacade {

    @Inject
    private DiscEnvApiService deService;

    @Inject
    public PermIdRequestAdminServiceFacadeImpl() {

    }

    @Override
    public void getPermanentIdRequests(AsyncCallback<String> callback) {
        String address = PERMID_ADMIN_REQUEST;
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void updatePermanentIdRequestStatus(PermanentIdRequestStatus status,
                                               AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getPermanentIdRequestStatuses(AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

}
