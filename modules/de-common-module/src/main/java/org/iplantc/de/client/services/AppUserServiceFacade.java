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
    void favoriteApp(String workspaceId, String appId, boolean fav,
            AsyncCallback<String> callback);

    /**
     * @param appWikiPageUrl URL of the app confluence page that contains the comment
     * @param appId
     * @param rating
     * @param commentId The comment ID of a previously added App Comment (see
     *            {@link #addAppComment(String, int, String, String, String, AsyncCallback)})
     * @param authorEmail the app author's email address for sending feedback
     * @param callback
     */
    void rateApp(String appWikiPageUrl, String appId, int rating, long commentId, String authorEmail,
            AsyncCallback<String> callback);

    /**
     * Posts the current user's comment of the given app to the its wiki page. The comment ID generated
     * by Confluence is returned via the callback if the call was successful.
     * 
     * @param appId
     * @param rating
     * @param appWikiPageUrl URL of the app confluence page that contains the comment
     * @param comment the comment text
     * @param authorEmail the app author's email address for sending feedback
     * @param callback
     */
    void addAppComment(String appId, int rating, String appWikiPageUrl, String comment,
            final String authorEmail, AsyncCallback<String> callback);

    /**
     * Posts the current user's rating of the given app, and changes the comment on the wiki page.
     * 
     * @param appId
     * @param rating
     * @param appWikiPageUrl URL of the app confluence page that contains the comment
     * @param commentId Confluence ID of the comment associated with the rating
     * @param comment the comment text
     * @param authorEmail the app author's email address for sending feedback
     * @param callback
     */
    void editAppComment(String appId, int rating, String appWikiPageUrl, Long commentId, String comment,
            String authorEmail, AsyncCallback<String> callback);

    /**
     * Deletes an existing rating for the current user. If a non-null commentId is provided, the comment
     * on the wiki page is also deleted. If the user hasn't rated the application, nothing happens.
     *
     * @param appId
     * @param toolName name of the app (name of the confluence page that contains the comment)
     * @param commentId Confluence comment ID
     * @param callback
     */
    void deleteRating(String appId, String toolName, Long commentId,
            AsyncCallback<String> callback);

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
     *
     * @param appId
     * @param callback
     */
    void getDCDetails(String appId, AsyncCallback<String> callback);

    /**
     * Checks if the given appId is able to be exported to TITo via a copy or edit. The service will
     * respond with JSON that contains a boolean "can-export" key, and a "cause" key if "can-export" is
     * false:
     *
     * <code>
     * { "can-export": false, "cause": "Analysis has multiple templates." }
     * </code>
     *
     * @param appId
     * @param callback
     */
    void appExportable(String appId, AsyncCallback<String> callback);

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
    void deleteAppFromWorkspace(String username, String fullUsername, List<String> appIds,
            AsyncCallback<String> callback);

    /**
     * Adds an app to the given public categories.
     *
     * @param json
     * @param callback
     */
    void publishToWorld(JSONObject json, AsyncCallback<String> callback);


    /**
     * Get app details
     *
     * @param appId
     * @param callback
     */
    void getAppDetails(String appId, AsyncCallback<String> callback);

    void createWorkflows(String body, AsyncCallback<String> callback);
}
