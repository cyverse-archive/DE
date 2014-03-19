package org.iplantc.de.client.events;

import org.iplantc.de.client.events.SystemMessageCountUpdateEvent.Handler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * an event that updates the number of unseen system messages
 */
public final class SystemMessageCountUpdateEvent extends GwtEvent<Handler> {

    /**
     * classes that can respond to this event need to implement this interface
     */
    public interface Handler extends EventHandler {

        /**
         * this method is called when a count update occurs.
         * 
         * @param event the event containing the count update
         */
        public void onCountUpdate(SystemMessageCountUpdateEvent event);
    }

    /**
     * the type associated with this event
     */
    public static final Type<Handler> TYPE = new Type<Handler>();

    private final int count;

    /**
     * the constructor
     * 
     * @param count the new unseen system message count
     */
    public SystemMessageCountUpdateEvent(final int count) {
        this.count = count;
    }

    /**
     * @see GwtEvent#getAssociatedType()
     */
    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    /**
     * returns the updated count
     * 
     * @return the update count
     */
    public int getCount() {
        return count;
    }

    /**
     * @see GwtEvent<T>#dispatch(T)
     */
    @Override
    protected void dispatch(final Handler handler) {
        handler.onCountUpdate(this);
    }

}
