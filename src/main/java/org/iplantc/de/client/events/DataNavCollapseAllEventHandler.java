/**
 * 
 */
package org.iplantc.de.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * @author sriram
 * 
 */
public interface DataNavCollapseAllEventHandler extends EventHandler {

    /**
     * Handler method to be called on collapse all click
     * 
     * @param event
     */
    void onCollapse(DataNavCollapseAllEvent event);

}
