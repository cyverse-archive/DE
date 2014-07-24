package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.notifications.Counts;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.callbacks.NotificationCallback;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class MessageServiceFacadeStub implements MessageServiceFacade {
    @Override
    public <C extends NotificationCallback> void getNotifications(int limit, int offset, String filter, String sortDir, C callback) {

    }

    @Override
    public void getRecentMessages(AsyncCallback<List<Notification>> callback) {

    }

    @Override
    public void markAsSeen(List<HasId> seenIds, AsyncCallback<String> callback) {

    }

    @Override
    public void markAsSeen(HasId id, AsyncCallback<String> callback) {

    }

    @Override
    public void deleteMessages(JSONObject deleteIds, AsyncCallback<String> callback) {

    }

    @Override
    public <C extends NotificationCallback> void getRecentMessages(C callback) {

    }

    @Override
    public void getMessageCounts(AsyncCallback<Counts> callback) {

    }

    @Override
    public void deleteAll(AsyncCallback<String> callback) {

    }

    @Override
    public void acknowledgeAll(AsyncCallback<String> callback) {

    }
}
