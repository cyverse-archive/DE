package org.iplantc.de.admin.desktop.client.services.impl;

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

/**
 * This class is a dummy class to satisfy GWT deferred-binding. By design, the Belphegor admin module
 * does not require the methods defined by {@link AppUserServiceFacade}.
 *
 * @author jstroot
 *
 */
public class AppAdminUserServiceFacade implements AppUserServiceFacade {

    @Override
    public void getApps(HasId appCategory, AsyncCallback<List<App>> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getPagedApps(String appCategoryId, int limit, String sortField, int offset,
                             SortDir sortDir, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppCategory>> callback, boolean loadHpc) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppCategory>> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void searchApp(String search, AsyncCallback<AppListLoadResult> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void favoriteApp(String workspaceId, String appId, boolean fav,
                            AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void deleteRating(App app, AsyncCallback<AppFeedback> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getDataObjectsForApp(String appId, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void publishWorkflow(String workflowId, String body, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void copyApp(HasId id, AsyncCallback<AppTemplate> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void deleteAppsFromWorkspace(String username, String fullUsername, List<String> id,
                                       AsyncCallback<String> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void publishToWorld(JSONObject json, String appId, AsyncCallback<String> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void editWorkflow(String workflowId, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void copyWorkflow(String workflowId, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getAppDetails(App app, AsyncCallback<App> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void createWorkflows(String body, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void rateApp(App app, int rating, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getAppDoc(HasId app, AsyncCallback<AppDoc> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";

    }

    @Override
    public void saveAppDoc(HasId appId, String doc, AsyncCallback<AppDoc> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";

    }

}
