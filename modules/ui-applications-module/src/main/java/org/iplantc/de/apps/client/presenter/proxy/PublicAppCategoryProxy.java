package org.iplantc.de.apps.client.presenter.proxy;

import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppUserServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;

import java.util.List;

public class PublicAppCategoryProxy extends RpcProxy<AppCategory, List<AppCategory>> {

    private final AppUserServiceFacade appService;

    @Inject
    public PublicAppCategoryProxy(AppUserServiceFacade appService) {
        this.appService = appService;
    }

    @Override
    public void load(AppCategory loadConfig, final AsyncCallback<List<AppCategory>> callback) {
        appService.getPublicAppCategories(callback);
    }
}
