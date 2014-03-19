/**
 * 
 */
package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author sriram
 * 
 */
public interface DataSearchHistorySelectedEventHandler extends EventHandler {

    /**
     * called when data search history is selected
     * 
     * @param event
     */
    void onSelection(DataSearchHistorySelectedEvent event);

}
