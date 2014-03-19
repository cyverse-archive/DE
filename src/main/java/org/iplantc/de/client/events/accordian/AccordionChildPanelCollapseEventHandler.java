package org.iplantc.de.client.events.accordian;

import com.google.gwt.event.shared.EventHandler;

/**
 * An event handler for AccordionChildPanelCollapseEvent
 * 
 * @author sriram
 * 
 */
public interface AccordionChildPanelCollapseEventHandler extends EventHandler {
    /**
     * Call back method when a panel is collapsed
     * 
     * @param event event to be handled.
     */
    public void onCollapse(AccordionChildPanelCollapseEvent event);
}
