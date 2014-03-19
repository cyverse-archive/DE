package org.iplantc.de.client.services;

import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.services.callbacks.NotificationCallback;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MessageServiceFacade {

    /**
     * Get notifications from the server.
     *
     * @param maxNotifications the maximum number of notifications to retrieve.
     * @param callback called on RPC completion.
     */
    <C extends NotificationCallback> void getNotifications(int limit, int offset, String filter, String sortDir, C callback);

    /**
     * Get messages from the server.
     *
     * @param callback called on RPC completion.
     */
    void getRecentMessages(AsyncCallback<String> callback);

    void markAsSeen(JSONObject seenIds, AsyncCallback<String> callback);

    /**
     * Delete messages from the server.
     *
     * @param arrDeleteIds array of notification ids to delete from the server.
     * @param callback called on RPC completion.
     */
    void deleteMessages(JSONObject deleteIds, AsyncCallback<String> callback);

    /**
     * Get messages from the server.
     *
     * @param callback called on RPC completion.
     */
    <C extends NotificationCallback> void getRecentMessages(C callback);

    /**
     * Retrieves the message counts from the server where the seen parameter is false.
     * 
     * @param callback called on RPC completion
     */
    void getMessageCounts(AsyncCallback<Counts> callback);

    void deleteAll(AsyncCallback<String> callback);

    void acknowledgeAll(AsyncCallback<String> callback);

}