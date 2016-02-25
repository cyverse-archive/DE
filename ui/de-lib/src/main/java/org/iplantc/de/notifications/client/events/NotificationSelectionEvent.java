package org.iplantc.de.notifications.client.events;

import org.iplantc.de.client.models.notifications.NotificationMessage;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author aramsey
 */
public class NotificationSelectionEvent extends GwtEvent<NotificationSelectionEvent.NotificationSelectionEventHandler> {

    private List<NotificationMessage> items;

    public static Type<NotificationSelectionEventHandler> TYPE =
            new Type<NotificationSelectionEventHandler>();

    public NotificationSelectionEvent(List<NotificationMessage> items) {
        this.items = items;
    }
    public Type<NotificationSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationSelectionEventHandler handler) {
        handler.onNotificationSelection(this);
    }

    public interface NotificationSelectionEventHandler extends EventHandler {
        void onNotificationSelection(NotificationSelectionEvent event);
    }

    public List<NotificationMessage> getNotifications() {
        return items;
    }

    public interface HasNotificationSelectionEventHandlers {
        HandlerRegistration addNotificationSelectionEventHandler (NotificationSelectionEventHandler handler);
    }
}
