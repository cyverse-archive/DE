package org.iplantc.de.commons.client;

import com.google.gwt.i18n.client.Constants;

/** Configurable constants shared by applications */
public interface CommonUiConstants extends Constants {

    /**
     * URL for error icon image.
     * 
     * @return a string representing the URL.
     */
    String iconError();

    /**
     * URL to forums
     * 
     * @return a string representing the URL.
     */
    String forumsUrl();

    /**
     * URL for landing page.
     * 
     * @return a string representing the URL.
     */
    String iplantHome();

    /**
     * URL to iplant services
     * 
     * @return a string representing the URL.
     */
    String supportUrl();

    /**
     * Characters that are not allowed in App names.
     * 
     * @return string representing the character set.
     */
    String appNameRestrictedChars();

    /**
     * Characters that are not allowed at the beginning of App names.
     * 
     * @return string representing the character set.
     */
    String appNameRestrictedStartingChars();

    /**
     * The default tag prefix used by the window manager when creating a "handle" for a window.
     * 
     * @return a string representing the default prefix used for a window
     */
    String windowTag();

    /**
     * key board short cuts
     * 
     * @return
     */
    String dataKeyShortCut();

    String appsKeyShortCut();

    String analysisKeyShortCut();

    String notifyKeyShortCut();

    String closeKeyShortCut();
    
    /**
     * iDrop download URL
     * 
     * @return
     */
    String idropUrl();
}
