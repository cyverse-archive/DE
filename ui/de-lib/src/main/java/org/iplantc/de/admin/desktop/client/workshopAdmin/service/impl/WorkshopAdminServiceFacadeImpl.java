package org.iplantc.de.admin.desktop.client.workshopAdmin.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.iplantc.de.admin.desktop.client.workshopAdmin.service.WorkshopAdminServiceFacade;
import org.iplantc.de.client.models.groups.GroupAutoBeanFactory;
import org.iplantc.de.client.models.groups.Member;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import java.util.List;

/**
 * @author dennis
 */
public class WorkshopAdminServiceFacadeImpl implements WorkshopAdminServiceFacade {

    private static final String WORKSHOP_ADMIN = "org.iplantc.services.admin.workshop";

    @Inject private GroupAutoBeanFactory factory;
    @Inject private DiscEnvApiService deService;

    public WorkshopAdminServiceFacadeImpl() {}

    @Override
    public void getMembers(AsyncCallback<List<Member>> callback) {
        String address = WORKSHOP_ADMIN + "/members";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new MemberListCallbackConverter(callback, factory));
    }
}
