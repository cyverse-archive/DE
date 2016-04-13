package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.client.services.callbacks.NotificationCallback;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

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
    void getRecentMessages(AsyncCallback<NotificationList> callback);

    void markAsSeen(List<HasId> seenIds, AsyncCallback<String> callback);

    void markAsSeen(HasId id, AsyncCallback<String> callback);

    /**
     * Delete messages from the server.
     *
     * @param arrDeleteIds array of notification ids to delete from the server.
     * @param callback called on RPC completion.
     */
    void deleteMessages(JSONObject deleteIds, AsyncCallback<String> callback);

    /**
     * Retrieves the message counts from the server where the seen parameter is false.
     * 
     * @param callback called on RPC completion
     */
    void getMessageCounts(AsyncCallback<Counts> callback);

    void deleteAll(NotificationCategory category, AsyncCallback<String> callback);

    void markAllNotificationsSeen(AsyncCallback<Void> callback);
    
    void getPermanentIdRequestStatusHistory(String id, AsyncCallback<String> callback);

}
