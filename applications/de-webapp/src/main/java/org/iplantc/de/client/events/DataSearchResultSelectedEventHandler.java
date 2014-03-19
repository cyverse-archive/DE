/**
 * 
 */
package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author sriram
 * 
 */
public interface DataSearchResultSelectedEventHandler extends EventHandler {

    /**
     * called when user click on data search result
     * 
     * @param event
     */
    void onSelection(DataSearchResultSelectedEvent event);
}
