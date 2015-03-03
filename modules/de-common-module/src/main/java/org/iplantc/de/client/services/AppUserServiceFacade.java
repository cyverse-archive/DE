package org.iplantc.de.client.services;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface AppUserServiceFacade extends AppServiceFacade {

    /**
     * @param workspaceId
     * @param appId
     * @param fav
     * @param callback
     */
    void favoriteApp(String workspaceId, String appId, boolean fav, AsyncCallback<String> callback);

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
     * 
     * @param workflowId unique identifier for the workflow.
     * @param callback called when the RPC call is complete.
     */
    void editWorkflow(String workflowId, AsyncCallback<String> callback);

    /**
     * Retrieves a new copy of a workflow from the database for editing in the client.
     * 
     * @param workflowId
     * @param callback
     */
    void copyWorkflow(String workflowId, AsyncCallback<String> callback);

    /**
     * @param appId
     * @param callback
     */
    void copyApp(String appId, AsyncCallback<String> callback);

    /**
     * @param username
     * @param fullUsername
     * @param appIds
     * @param callback
     */
    void deleteAppsFromWorkspace(String username,
                                 String fullUsername,
                                 List<String> appIds,
                                 AsyncCallback<String> callback);

    /**
     * Adds an app to the given public categories.
     * 
     * @param json
     * @param appId
     * @param callback
     */
    void publishToWorld(JSONObject json, String appId, AsyncCallback<String> callback);

    /**
     * Get app details
     * 
     * @param appId
     * @param callback
     */
    void getAppDetails(String appId, AsyncCallback<String> callback);

    void getAppDoc(String appId, AsyncCallback<String> callback);

    void saveAppDoc(String appId, String doc, AsyncCallback<String> callback);

    void createWorkflows(String body, AsyncCallback<String> callback);

    void rateApp(String appWikiPageUrl,
                 String appId,
                 int rating,
                 long commentId,
                 String authorEmail,
                 AsyncCallback<String> callback);

            void
            deleteRating(String appId,
                         String appWikiPageUrl,
                         Long commentId,
                         AsyncCallback<String> callback);
}
