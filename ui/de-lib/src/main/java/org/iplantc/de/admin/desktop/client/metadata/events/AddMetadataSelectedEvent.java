package org.iplantc.de.admin.desktop.client.metadata.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class AddMetadataSelectedEvent
        extends GwtEvent<AddMetadataSelectedEvent.AddMetadataSelectedEventHandler> {
    public static interface AddMetadataSelectedEventHandler extends EventHandler {
        void onAddMetadataSelected(AddMetadataSelectedEvent event);
    }

    public interface HasAddMetadataSelectedEventHandlers {
        HandlerRegistration addAddMetadataSelectedEventHandler(AddMetadataSelectedEventHandler handler);
    }
    public static Type<AddMetadataSelectedEventHandler> TYPE =
            new Type<AddMetadataSelectedEventHandler>();

    public Type<AddMetadataSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AddMetadataSelectedEventHandler handler) {
        handler.onAddMetadataSelected(this);
    }
}
