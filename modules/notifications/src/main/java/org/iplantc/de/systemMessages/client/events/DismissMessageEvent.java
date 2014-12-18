package org.iplantc.de.systemMessages.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This represents the user dismissing something.
 */
public final class DismissMessageEvent extends GwtEvent<DismissMessageEvent.Handler> {
	
    /**
     * Any class that can handle this event needs to extend this interface.
     */
    public interface Handler extends EventHandler {
        /**
         * This method is called when the event is fired.
         * 
         * @param event the event
         */
        void handleDismiss(DismissMessageEvent event);
	}

    /**
     * The type object associated with this class
     */
    public static final Type<Handler> TYPE = new Type<Handler>();

    private final String messageId;
	
    /**
     * the constructor
     * 
     * @param messageId the identifier of the message being dismissed
     */
    public DismissMessageEvent(final String messageId) {
        this.messageId = messageId;
	}
	
    /**
     * @see GwtEvent#getAssociatedType()
     */
	@Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
	}

    /**
     * Retrieves the message being dismissed
     * 
     * @return the id of the message being dismissed
     */
    public String getMessage() {
        return messageId;
    }

    /**
     * @see GwtEvent<T>#dispatch(T)
     */
    @Override
    protected void dispatch(final Handler handler) {
		handler.handleDismiss(this);
	}
	
}
