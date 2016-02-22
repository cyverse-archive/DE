package org.iplantc.de.notifications.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class NotificationToolbarDeleteAllClickedEvent
        extends GwtEvent<NotificationToolbarDeleteAllClickedEvent.NotificationToolbarDeleteAllClickedEventHandler> {
    public static Type<NotificationToolbarDeleteAllClickedEventHandler> TYPE =
            new Type<NotificationToolbarDeleteAllClickedEventHandler>();

    public Type<NotificationToolbarDeleteAllClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationToolbarDeleteAllClickedEventHandler handler) {
        handler.onNotificationToolbarDeleteAllClicked(this);
    }

    public static interface NotificationToolbarDeleteAllClickedEventHandler extends EventHandler {
        void onNotificationToolbarDeleteAllClicked(NotificationToolbarDeleteAllClickedEvent event);
    }

    public interface HasNotificationToolbarDeleteAllClickedEventHandlers {
        HandlerRegistration addNotificationToolbarDeleteAllClickedEventHandler(NotificationToolbarDeleteAllClickedEventHandler handler);
    }
}
