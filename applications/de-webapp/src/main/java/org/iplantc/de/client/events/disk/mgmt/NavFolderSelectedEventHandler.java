package org.iplantc.de.client.events.disk.mgmt;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for when a disk resource is selected.
 * 
 * @author amuir
 * 
 */
public interface NavFolderSelectedEventHandler extends EventHandler {
    /**
     * Handle when a disk resource was selected.
     * 
     * @param event event to be handled.
     */
    void onSelected(NavFolderSelectedEvent event);
}