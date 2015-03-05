package org.iplantc.de.apps.client.presenter.categories.proxy;

import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;

import java.util.List;

/**
 * @author jstroot
 *
 */
public class AppCategoryProxy extends RpcProxy<AppCategory, List<AppCategory>> {

    private final AppServiceFacade serviceFacade;

    @Inject
    public AppCategoryProxy(AppServiceFacade serviceFacade) {
        this.serviceFacade = serviceFacade;
    }

    @Override
    public void load(AppCategory loadConfig, final AsyncCallback<List<AppCategory>> callback) {
        serviceFacade.getAppCategories(callback);
    }

}
