package org.iplantc.de.client.events.accordian;

import com.google.gwt.event.shared.EventHandler;

/**
 * Event handler for accordian panel collapse.
 * 
 * @author sriram
 * 
 */
public interface AccordionChildPanelExpandEventHandler extends EventHandler {
    /**
     * Handle when an accordian child panel is expanded.
     * 
     * @param event event to be handled.
     */
    public void onExpand(AccordionChildPanelExpandEvent event);
}
