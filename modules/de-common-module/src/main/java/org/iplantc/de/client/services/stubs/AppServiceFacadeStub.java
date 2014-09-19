package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

public class AppServiceFacadeStub implements AppServiceFacade {
    @Override
    public void getApps(String appCategoryId, AsyncCallback<String> callback) {

    }

    @Override
    public void getPagedApps(String appCategoryId, int limit, String sortField, int offset, SortDir sortDir, AsyncCallback<String> callback) {

    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback) {

    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {

    }

    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {

    }
}
