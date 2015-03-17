package org.iplantc.de.notifications.client.utils;

import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncement;
import org.iplantc.de.commons.client.info.IplantAnnouncer;

import com.sencha.gxt.core.client.dom.XDOM;

/**
 * Provides a custom queue and location for presenting notifications to the user.
 * 
 * @author psarando
 * 
 */
public class NotifyInfo extends IplantAnnouncer {
    private static NotifyInfo instance;

    protected NotifyInfo() {

    }

    public static NotifyInfo getInstance() {
        if (instance == null) {
            instance = new NotifyInfo();
        }

        return instance;
    }

    /**
     * Schedule a notification to display to the user.
     * 
     * @param text represents the message text to display.
     */
    public void display(final String text) {
        schedule(text);
    }

    /**
     * Schedule a notification containing a warning or alert to display to the user.
     * 
     * @param text represents the warning text to display.
     */
    public void displayWarning(String text) {
        schedule(new ErrorAnnouncementConfig(text));
    }

    @Override
    protected void positionAnnouncer() {
        if (announcements.isEmpty()) {
            return;
        }

        IplantAnnouncement popup = announcements.peek();

        int x = XDOM.getViewportWidth() - popup.getOffsetWidth() - 10;
        int y = XDOM.getViewportHeight() - popup.getOffsetHeight() - 64;
        popup.setPagePosition(x, y);
    }
}
