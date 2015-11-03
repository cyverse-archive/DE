package org.iplantc.de.commons.client.util;



/**
 * A util class for opening pop-ups .If pop-up is blocked by the client, Call showDialogOnPopUpBlock
 * method to display url as link
 * 
 * @author sriram
 * 
 */
public class WindowUtil {
    /**
     * Attempts to open a new window for the given URL and tries to detect if the window was
     * "Popup blocked". Displays a "popup warning" dialog with showDialogOnPopUpBlock if the window open
     * was blocked.
     * 
     * @param url
     */
    public static void open(final String url) {
        open(url, "");
    }

    /**
     * Attempts to open a new window for the given URL and options, and tries to detect if the window was
     * "Popup blocked". Displays a "popup warning" dialog with showDialogOnPopUpBlock if the window open
     * was blocked.
     * 
     * @param url
     * @param options
     */
    public static void open(final String url, final String options) {
      open(url, "", options);
    }

    /**
     * Attempts to open a new window for the given URL and options, and tries to detect if the window was
     * "Popup blocked".
     * 
     * @param url
     * @param window_name
     * @param options
     * @return False if the window open was blocked, otherwise focuses the new window and returns true.
     */
    public static native boolean open(String url, String window_name, String options) /*-{
        var popup = $wnd.open(url, window_name, options);

        if (!popup || popup.closed || typeof popup == 'undefined' || typeof popup.closed == 'undefined') {
            return false;
        }

        popup.focus();
        return true;
    }-*/;
}
