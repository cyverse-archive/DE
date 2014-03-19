package org.iplantc.de.resources.client;

import com.google.gwt.resources.client.CssResource;

/**
 * This is the styling applied to the Announcer widget.
 */
public interface AnnouncerStyle extends CssResource {

    /**
     * The close button styling when the mouse is not over the button.
     * 
     * @return the style name
     */
    String closeButton();

    /**
     * The close button styling when the mouse is over the button.
     * 
     * @return the style name
     */
    String closeButtonOver();

    /**
     * The styling applied to the main announcer panel.
     * 
     * @return the style name
     */
    String panel();

    /**
     * The styling applied to error announcements.
     * 
     * @return the style name
     */
    String panelError();

    /**
     * The additive styling applied to the main announcer panel when there are multiple announcements.
     * 
     * @return the additive style name
     */
    String panelMultiple();

    /**
     * The styling applied to the content widget area of the panel.
     * 
     * @return the style name
     */
    String content();

}
