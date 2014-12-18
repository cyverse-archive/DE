package org.iplantc.de.notifications.client.events;

import org.iplantc.de.notifications.client.events.NotificationCountUpdateEvent.NotificationCountUpdateEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * An event that notifies unseen notification count has changed
 * 
 * @author sriram
 * 
 */
public class NotificationCountUpdateEvent extends GwtEvent<NotificationCountUpdateEventHandler> {
    /**
     * @author sriram
     * 
     */
    public interface NotificationCountUpdateEventHandler extends EventHandler {

        /**
         * Handler when notification count changes
         */
        public void onCountUpdate(NotificationCountUpdateEvent ncue);

    }

    private final int total;

    /**
     * Defines the GWT Event Type.
     * 
     * @see NotificationCountUpdateEvent
     */
    public static final GwtEvent.Type<NotificationCountUpdateEventHandler> TYPE = new GwtEvent.Type<NotificationCountUpdateEventHandler>();

    public NotificationCountUpdateEvent(int total) {
        this.total = total;
    }

    /**
     * @return the dataCount
     */
    public int getTotal() {
        return total;
    }

    @Override
    public Type<NotificationCountUpdateEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NotificationCountUpdateEventHandler arg0) {
        arg0.onCountUpdate(this);
    }

}
