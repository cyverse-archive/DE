package org.iplantc.de.apps.client.events;


import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * An event that is fired when a tool request is submitted
 * @author sriram
 * 
 */
public class NewToolRequestSubmitEvent extends GwtEvent<NewToolRequestSubmitEvent.NewToolRequestSubmitEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.core.tito.client.events.NavigationTreeAddEventHandler
     */
    public static final GwtEvent.Type<NewToolRequestSubmitEventHandler> TYPE = new GwtEvent.Type<NewToolRequestSubmitEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<NewToolRequestSubmitEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NewToolRequestSubmitEventHandler handler) {
        handler.onRequestComplete(this);

    }

    /**
     *
     * An event handler for NewToolRequestSubmitEvent
     * @author sriram
     *
     */
    public static interface NewToolRequestSubmitEventHandler extends EventHandler {
        /**
         * invoked when new tool request complete
         * @param event
         */
        void onRequestComplete(NewToolRequestSubmitEvent event);
    }
}
