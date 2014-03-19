package org.iplantc.de.apps.client.events.handlers;


import org.iplantc.de.apps.client.events.NewToolRequestSubmitEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * An event handler for NewToolRequestSubmitEvent
 * @author sriram
 * 
 */
public interface NewToolRequestSubmitEventHandler extends EventHandler {
    /**
     * invoked when new tool request complete
     * @param event
     */
    void onRequestComplete(NewToolRequestSubmitEvent event);
}
