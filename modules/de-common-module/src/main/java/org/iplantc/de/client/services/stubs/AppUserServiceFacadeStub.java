package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.services.AppUserServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.SortDir;

import java.util.List;

public class AppUserServiceFacadeStub implements AppUserServiceFacade {
    @Override
    public void favoriteApp(String workspaceId, String appId, boolean fav, AsyncCallback<String> callback) {

    }

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

    @Override
    public void rateApp(String appWikiPageName, String appId, int rating, long commentId,
            String authorEmail, AsyncCallback<String> callback) {
    }

    @Override
    public void addAppComment(String appId, int rating, String appName, String comment,
            String authorEmail, AsyncCallback<String> callback) {

    }

    @Override
    public void editAppComment(String appId, int rating, String appName, Long commentId, String comment, String authorEmail, AsyncCallback<String> callback) {

    }

    @Override
    public void deleteRating(String appId, String toolName, Long commentId, AsyncCallback<String> callback) {

    }

    @Override
    public void getDataObjectsForApp(String appId, AsyncCallback<String> callback) {

    }

    @Override
    public void publishWorkflow(String body, AsyncCallback<String> callback) {

    }

    @Override
    public void editWorkflow(String workflowId, AsyncCallback<String> callback) {

    }

    @Override
    public void copyWorkflow(String workflowId, AsyncCallback<String> callback) {

    }

    @Override
    public void getDCDetails(String appId, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void appExportable(String appId, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void copyApp(String appId, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void deleteAppFromWorkspace(String username, String fullUsername, List<String> appIds, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void publishToWorld(JSONObject json, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void getAppDetails(String appId, AsyncCallback<String> callback) {

    }
}
