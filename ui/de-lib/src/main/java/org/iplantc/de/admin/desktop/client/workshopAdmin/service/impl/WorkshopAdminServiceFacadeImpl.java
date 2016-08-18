package org.iplantc.de.admin.desktop.client.workshopAdmin.service.impl;

import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.GET;
import static org.iplantc.de.shared.services.BaseServiceCallWrapper.Type.PUT;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import org.iplantc.de.admin.desktop.client.workshopAdmin.service.WorkshopAdminServiceFacade;
import org.iplantc.de.client.models.groups.*;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import java.util.ArrayList;
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
        final String address = WORKSHOP_ADMIN + "/members";

        ServiceCallWrapper wrapper = new ServiceCallWrapper(GET, address);
        deService.getServiceData(wrapper, new MemberListCallbackConverter(callback, factory));
    }

    private List<String> membersToSubjectIds(List<Member> members) {
        List<String> result = new ArrayList<>();
        for (Member member : members) {
            result.add(member.getId());
        }
        return result;
    }

    @Override
    public void saveMembers(List<Member> members, AsyncCallback<MemberSaveResult> callback) {
        final String address = WORKSHOP_ADMIN + "/members";

        // Generate the payload.
        final AutoBean<MemberSaveRequest> memberList = factory.getMemberSaveRequest();
        memberList.as().setMembers(membersToSubjectIds(members));
        final Splittable encode = AutoBeanCodex.encode(memberList);

        // Call the service.
        ServiceCallWrapper wrapper = new ServiceCallWrapper(PUT, address, encode.getPayload());
        deService.getServiceData(wrapper, new MemberSaveResultCallbackConverter(callback, factory));
    }
}
