package org.iplantc.de.apps.client.presenter.categories.proxy;

import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;

import java.util.List;

/**
 * @author jstroot
 */
public class PublicAppCategoryProxy extends RpcProxy<AppCategory, List<AppCategory>> {

    private final AppServiceFacade appService;

    private boolean loadHpc;

    @Inject
    public PublicAppCategoryProxy(AppServiceFacade appService) {
        this.appService = appService;
    }


    @Override
    public void load(AppCategory loadConfig, final AsyncCallback<List<AppCategory>> callback) {
        appService.getPublicAppCategories(callback, loadHpc);
    }

    public boolean isLoadHpc() {
        return loadHpc;
    }

    public void setLoadHpc(boolean loadHpc) {
        this.loadHpc = loadHpc;
    }
}
