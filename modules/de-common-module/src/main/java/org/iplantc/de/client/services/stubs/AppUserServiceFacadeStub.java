package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.proxy.AppListLoadResult;
import org.iplantc.de.client.services.AppUserServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

public class AppUserServiceFacadeStub implements AppUserServiceFacade {
    @Override
    public void favoriteApp(HasId appId, boolean fav,
                            AsyncCallback<Void> callback) {

    }

    @Override
    public void getApps(HasId appCategory, AsyncCallback<List<App>> callback) {

    }

    @Override
    public void getPagedApps(String appCategoryId, int limit, String sortField, int offset, SortDir sortDir, AsyncCallback<String> callback) {

    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback, boolean loadHpc) {

    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {

    }

    @Override
    public void searchApp(String search, AsyncCallback<AppListLoadResult> callback) {

    }

    @Override
    public void deleteRating(App app, AsyncCallback<AppFeedback> callback) {

    }

    @Override
    public void getDataObjectsForApp(String appId, AsyncCallback<String> callback) {

    }

    @Override
    public void publishWorkflow(String workflowId, String body, AsyncCallback<String> callback) {

    }

    @Override
    public void editWorkflow(HasId workflowId, AsyncCallback<String> callback) {

    }

    @Override
    public void copyWorkflow(String workflowId, AsyncCallback<String> callback) {

    }

    @Override
    public void copyApp(HasId app, AsyncCallback<AppTemplate> asyncCallback) {

    }

    @Override
    public void deleteAppsFromWorkspace(List<App> apps,
                                        AsyncCallback<Void> asyncCallback) {

    }

    @Override
    public void publishToWorld(JSONObject json, String appId, AsyncCallback<Void> asyncCallback) {

    }

    @Override
    public void getAppDetails(App app, AsyncCallback<App> callback) {

    }

    @Override
    public void createWorkflows(String body, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void rateApp(App app, int rating, AsyncCallback<String> callback) {

    }

    @Override
    public void getAppDoc(HasId app, AsyncCallback<AppDoc> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveAppDoc(HasId appId, String doc, AsyncCallback<AppDoc> callback) {
        // TODO Auto-generated method stub

    }
}
