package org.iplantc.de.notifications.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class NotificationToolbarDeleteClickedEvent
        extends GwtEvent<NotificationToolbarDeleteClickedEvent.NotificationToolbarDeleteClickedEventHandler> {
    public static Type<NotificationToolbarDeleteClickedEventHandler> TYPE =
            new Type<NotificationToolbarDeleteClickedEventHandler>();

    public Type<NotificationToolbarDeleteClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationToolbarDeleteClickedEventHandler handler) {
        handler.onNotificationToolbarDeleteClicked(this);
    }

    public static interface NotificationToolbarDeleteClickedEventHandler extends EventHandler {
        void onNotificationToolbarDeleteClicked(NotificationToolbarDeleteClickedEvent event);
    }

    public interface HasNotificationToolbarDeleteClickedEventHandlers {
        HandlerRegistration addNotificationToolbarDeleteClickedEventHandler(NotificationToolbarDeleteClickedEventHandler handler);
    }
}
