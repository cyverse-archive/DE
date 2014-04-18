package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.AppServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

public class AppServiceFacadeStub implements AppServiceFacade {
    @Override
    public void getApps(String appGroupId, AsyncCallback<String> callback) {

    }

    @Override
    public void getPagedApps(String appGroupId, int limit, String sortField, int offset, SortDir sortDir, AsyncCallback<String> callback) {

    }

    @Override
    public void getPublicAppGroups(AsyncCallback<List<AppGroup>> callback) {

    }

    @Override
    public void getAppGroups(AsyncCallback<List<AppGroup>> callback) {

    }

    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {

    }
}
