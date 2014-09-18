package org.iplantc.admin.belphegor.client.services.impl;

import org.iplantc.de.client.models.apps.AppGroup;
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
    public void getApps(String appCategoryId, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getPagedApps(String analysisGroupId, int limit, String sortField, int offset,
            SortDir sortDir, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getPublicAppCategories(AsyncCallback<List<AppGroup>> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getAppCategories(AsyncCallback<List<AppGroup>> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void searchApp(String search, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void favoriteApp(String workspaceId, String analysisId, boolean fav,
            AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void rateApp(String appWikiPageName, String appId, int rating, long commentId,
            String authorEmail, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void addAppComment(String analysisId, int rating, String appName, String comment,
            String authorEmail, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void editAppComment(String analysisId, int rating, String appName, Long commentId,
            String comment, String authorEmail, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void deleteRating(String analysisId, String toolName, Long commentId,
            AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getDataObjectsForApp(String analysisId, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void publishWorkflow(String body, AsyncCallback<String> callback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void getDCDetails(String appId, AsyncCallback<String> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void appExportable(String id, AsyncCallback<String> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void copyApp(String id, AsyncCallback<String> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void deleteAppFromWorkspace(String username, String fullUsername, List<String> id,
            AsyncCallback<String> asyncCallback) {
        assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

    @Override
    public void publishToWorld(JSONObject json, AsyncCallback<String> asyncCallback) {
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
    public void getAppDetails(String  id, AsyncCallback<String> callback) {
    	 assert false : "Dummy Class to satisfy deferred-binding, this class not used in this module.";
    }

}
