package org.iplantc.de.collaborators.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram, jstroot
 */
public class CollaboratorsLoadedEvent extends GwtEvent<CollaboratorsLoadedEvent.CollaboratorsLoadedEventHandler> {

    /**
     * Defines handler for Collaborators loaded event
     *
     * @author sriram
     */
    public static interface CollaboratorsLoadedEventHandler extends EventHandler {

        public void onLoad(CollaboratorsLoadedEvent event);

    }

    /**
     * Defines the GWT Event Type.
     *
     */
    public static final GwtEvent.Type<CollaboratorsLoadedEventHandler> TYPE = new GwtEvent.Type<CollaboratorsLoadedEventHandler>();

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CollaboratorsLoadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CollaboratorsLoadedEventHandler handler) {
        handler.onLoad(this);
    }
}
