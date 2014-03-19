/**
 * 
 */
package org.iplantc.de.commons.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 * 
 */
public class CollaboratorsLoadedEvent extends GwtEvent<CollaboratorsLoadedEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.client.events.CollaboratorsLoadedEventHandler
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
