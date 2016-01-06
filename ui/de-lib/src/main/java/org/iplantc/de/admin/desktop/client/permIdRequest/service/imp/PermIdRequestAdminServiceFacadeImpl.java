package org.iplantc.de.admin.desktop.client.permIdRequest.service.imp;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermIdRequestAdminServiceFacade;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

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
    public void updatePermanentIdRequestStatus(String requestId,
                                               PermanentIdRequestUpdate status,
                                               AsyncCallback<String> callback) {
        String address = PERMID_ADMIN_REQUEST + "/" + requestId + "/status";
        Splittable s = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(status));
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, s.getPayload());
        deService.getServiceData(wrapper, callback);
    }

    @Override
    public void getPermanentIdRequestStatuses(AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

}
