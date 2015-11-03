package org.iplantc.de.apps.widgets.client.events;

import org.iplantc.de.apps.widgets.client.events.ArgumentSelectedEvent.ArgumentSelectedEventHandler;
import org.iplantc.de.client.models.apps.integration.Argument;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An event to be fired when the user selects an {@link Argument} bound UI element.
 * 
 * @author jstroot
 * 
 */
public class ArgumentSelectedEvent extends GwtEvent<ArgumentSelectedEventHandler> {

    public interface ArgumentSelectedEventHandler extends EventHandler {
        void onArgumentSelected(ArgumentSelectedEvent event);
    }

    public static interface HasArgumentSelectedEventHandlers {
        HandlerRegistration addArgumentSelectedEventHandler(ArgumentSelectedEventHandler handler);
    }


    public static GwtEvent.Type<ArgumentSelectedEventHandler> TYPE = new GwtEvent.Type<ArgumentSelectedEventHandler>();

    private final Argument argument;

    public ArgumentSelectedEvent(Argument argument) {
        this.argument = argument;
    }

    public Argument getArgument() {
        return argument;
    }

    @Override
    public GwtEvent.Type<ArgumentSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ArgumentSelectedEventHandler handler) {
        handler.onArgumentSelected(this);
    }

}
