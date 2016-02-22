package org.iplantc.de.notifications.client.events;

import org.iplantc.de.client.models.notifications.NotificationCategory;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class NotificationToolbarSelectionEvent
        extends GwtEvent<NotificationToolbarSelectionEvent.NotificationToolbarSelectionEventHandler> {

    private NotificationCategory notificationCategory;

    public NotificationToolbarSelectionEvent (NotificationCategory notificationCategory) {
        this.notificationCategory = notificationCategory;
    }

    public NotificationCategory getNotificationCategory() {
        return notificationCategory;
    }

    public static Type<NotificationToolbarSelectionEventHandler> TYPE =
            new Type<NotificationToolbarSelectionEventHandler>();

    public Type<NotificationToolbarSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationToolbarSelectionEventHandler handler) {
        handler.onNotificationToolbarSelection(this);
    }

    public static interface NotificationToolbarSelectionEventHandler extends EventHandler {
        void onNotificationToolbarSelection(NotificationToolbarSelectionEvent event);
    }

    public interface HasNotificationToolbarSelectionEventHandlers {
        HandlerRegistration addNotificationToolbarSelectionEventHandler(NotificationToolbarSelectionEventHandler handler);
    }
}
