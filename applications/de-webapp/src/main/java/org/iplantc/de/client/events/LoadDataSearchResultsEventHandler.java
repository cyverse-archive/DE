/**
 * 
 */
package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author sriram
 * 
 */
public interface LoadDataSearchResultsEventHandler extends EventHandler {

    /**
     * Called when search results are ready to be loaded
     * 
     * @param event
     */
    void onLoad(LoadDataSearchResultsEvent event);

}
