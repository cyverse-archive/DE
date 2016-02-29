package org.iplantc.de.notifications.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class NotificationGridRefreshEvent extends GwtEvent<NotificationGridRefreshEvent.NotificationGridRefreshEventHandler> {
    public static Type<NotificationGridRefreshEventHandler> TYPE =
            new Type<NotificationGridRefreshEventHandler>();

    public Type<NotificationGridRefreshEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(NotificationGridRefreshEventHandler handler) {
        handler.onNotificationGridRefresh(this);
    }

    public interface NotificationGridRefreshEventHandler extends EventHandler {
        void onNotificationGridRefresh(NotificationGridRefreshEvent event);
    }

    public interface HasNotificationGridRefreshEventHandlers {
        HandlerRegistration addNotificationGridRefreshEventHandler(NotificationGridRefreshEventHandler handler);
    }
}
