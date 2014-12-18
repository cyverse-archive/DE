package org.iplantc.de.systemMessages.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * This event represents a user request to display the system messages.
 */
public final class ShowSystemMessagesEvent extends GwtEvent<ShowSystemMessagesEvent.Handler> {

	/**
	 * The handler
	 */
	public interface Handler extends EventHandler {	
		void showSystemMessages(ShowSystemMessagesEvent event);
	}
	
	public static final GwtEvent.Type<Handler> TYPE = new GwtEvent.Type<Handler>();
	
	@Override
	public GwtEvent.Type<Handler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(final Handler handler) {
		handler.showSystemMessages(this);
	}
	
}
