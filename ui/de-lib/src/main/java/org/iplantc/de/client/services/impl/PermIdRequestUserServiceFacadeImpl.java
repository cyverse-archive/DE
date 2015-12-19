package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.models.identifiers.PermanentIdRequestType;
import org.iplantc.de.client.services.PermIdRequestUserServiceFacade;
import org.iplantc.de.shared.services.BaseServiceCallWrapper.Type;
import org.iplantc.de.shared.services.DiscEnvApiService;
import org.iplantc.de.shared.services.ServiceCallWrapper;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

public class PermIdRequestUserServiceFacadeImpl implements PermIdRequestUserServiceFacade {

    final DiscEnvApiService deServiceFacade;

    @Inject
    public PermIdRequestUserServiceFacadeImpl(final DiscEnvApiService deServiceFacade) {
        this.deServiceFacade = deServiceFacade;
    }

    @Override
    public void requestPermId(String uuid, PermanentIdRequestType type, AsyncCallback<String> callback) {
        String address = PERMID_REQUEST;
        Splittable s = StringQuoter.createSplittable();
        StringQuoter.create(uuid).assign(s, "folder");
        StringQuoter.create(type.toString()).assign(s, "type");
        GWT.log("request:" + s.getPayload());
        final ServiceCallWrapper wrapper = new ServiceCallWrapper(Type.POST, address, s.getPayload());
        deServiceFacade.getServiceData(wrapper, callback);
    }

}

