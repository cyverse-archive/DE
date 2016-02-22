package org.iplantc.de.admin.desktop.client.permIdRequest.service.imp;

import org.iplantc.de.admin.desktop.client.permIdRequest.service.PermanentIdRequestAdminServiceFacade;
import org.iplantc.de.client.models.identifiers.PermanentIdRequestUpdate;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * 
 * @author sriram
 * 
 */
public class PermanentIdRequestAdminServiceFacadeImpl implements PermanentIdRequestAdminServiceFacade {

    @Inject
    private DiscEnvApiService deService;

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
    public void createPermanentId(String requestId, AsyncCallback<String> asyncCallback) {
        String address = PERMID_ADMIN_REQUEST + "/" + requestId + "/ezid";
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, "{}");
        deService.getServiceData(wrapper, asyncCallback);

    }

    @Override
    public void getRequestDetails(String id, AsyncCallback<String> asyncCallback) {
        String address = PERMID_ADMIN_REQUEST + "/" + id;
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.GET, address);
        deService.getServiceData(wrapper, asyncCallback);
    }

}
