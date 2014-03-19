package org.iplantc.de.commons.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * Defines handler for Collaborators laoded event
 * 
 * @author sriram
 * 
 */
public interface CollaboratorsLoadedEventHandler extends EventHandler {

    public void onLoad(CollaboratorsLoadedEvent event);

}
