package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.integration.AppTemplate;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppUserServiceFacade extends AppServiceFacade {

    void favoriteApp(HasId appId, boolean fav, AsyncCallback<Void> callback);

    /**
     * Retrieves the name and a list of inputs and outputs for the given app. The response JSON will be
     * formatted as follows:
     * 
     * <pre>
     * {
     *     "id": "app-id",
     *     "name": "analysis-name",
     *     "inputs": [{...property-details...},...],
     *     "outputs": [{...property-details...},...]
     * }
     * </pre>
     * 
     * @param appId unique identifier of the app.
     * @param callback called when the RPC call is complete.
     */
    void getDataObjectsForApp(String appId, AsyncCallback<String> callback);

    /**
     * Publishes a workflow / pipeline to user's workspace
     * 
     * @param body post body json
     * @param callback called when the RPC call is complete
     */
    void publishWorkflow(String workflowId, String body, AsyncCallback<String> callback);

    /**
     * Retrieves a workflow from the database for editing in the client.
     *  @param workflowId unique identifier for the workflow.
     * @param callback called when the RPC call is complete.
     */
    void editWorkflow(HasId workflowId, AsyncCallback<String> callback);

    /**
     * Retrieves a new copy of a workflow from the database for editing in the client.
     */
    void copyWorkflow(String workflowId, AsyncCallback<String> callback);

    void copyApp(HasId app, AsyncCallback<AppTemplate> callback);

    void deleteAppsFromWorkspace(List<App> apps,
                                 AsyncCallback<Void> callback);

    /**
     * Adds an app to the given public categories.
     */
    void publishToWorld(JSONObject json, String appId, AsyncCallback<Void> callback);

    /**
     * Get app details
     */
    void getAppDetails(App app, AsyncCallback<App> callback);

    void getAppDoc(HasId app, AsyncCallback<AppDoc> callback);

    void saveAppDoc(HasId appId, String doc, AsyncCallback<AppDoc> callback);

    void createWorkflows(String body, AsyncCallback<String> callback);

    void rateApp(App app,
                 int rating,
                 AsyncCallback<AppFeedback> callback);

    void deleteRating(App app, AsyncCallback<AppFeedback> callback);

    void getPermissions(List<App> apps, AsyncCallback<String> callback);

    void shareApp(List<App> apps, List<String> users, String permission, AsyncCallback<String> callback);
    
    void unshareApp(List<App> apps, List<String> users, AsyncCallback<String> callback);


}
