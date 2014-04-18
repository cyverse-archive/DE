package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.apps.AppGroup;
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

    @Override
    public void rateApp(String appId, int rating, String appName, String comment, String authorEmail, AsyncCallback<String> callback) {

    }

    @Override
    public void updateRating(String appId, int rating, String appName, Long commentId, String comment, String authorEmail, AsyncCallback<String> callback) {

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
    public void appExportable(String id, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void copyApp(String id, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void deleteAppFromWorkspace(String username, String fullUsername, List<String> ids, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void publishToWorld(JSONObject json, AsyncCallback<String> asyncCallback) {

    }

    @Override
    public void getAppDetails(String id, AsyncCallback<String> callback) {

    }
}
