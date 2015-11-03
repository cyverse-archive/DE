package org.iplantc.de.systemMessages.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event indicates that new system messages have been received.
 */
public final class NewSystemMessagesEvent extends GwtEvent<NewSystemMessagesEvent.Handler> {

    /**
     * Classes that implement this interface are able to receive events of this type.
     */
	public interface Handler extends EventHandler {

		/**
		 * This method is called when an MessagesUpdatedEvent is dispatched.
		 * 
		 * @param event The event being dispatched.
		 */
		void onUpdate(NewSystemMessagesEvent event);
		
	}

	/**
	 * The type object associated with MessagesUpdatedEvents objects.
	 */
	public static final Type<Handler> TYPE = new Type<Handler>();
	
	/**
	 * @see GwtEvent#getAssociatedType()
	 */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	/**
	 * @see GwtEvent#dispatch(Handler)
	 */
	@Override
	protected void dispatch(final Handler handler) {
		handler.onUpdate(this);
	}
	
}
