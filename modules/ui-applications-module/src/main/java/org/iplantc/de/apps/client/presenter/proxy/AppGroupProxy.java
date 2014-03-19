package org.iplantc.de.apps.client.presenter.proxy;

import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;

import java.util.List;

/**
 * @author jstroot
 *
 */
public class AppGroupProxy extends RpcProxy<AppGroup, List<AppGroup>> {

    private final AppServiceFacade serviceFacade;

    @Inject
    public AppGroupProxy(AppServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    @Override
    public void load(AppGroup loadConfig, final AsyncCallback<List<AppGroup>> callback) {
        serviceFacade.getAppGroups(callback);
    }

}
