package org.iplantc.de.client;

import com.google.gwt.i18n.client.Constants;


/**
 * Constants used by client code not visible to the user.
 */
public interface DEClientConstants extends Constants {

    /**
     * The Servlet path (relative URL) for file upload.
     * 
     * @return the requested URL.
     */
    String fileUploadServlet();

    /**
     * The Servlet path (relative URL) for file downloads.
     * 
     * @return the requested URL.
     */
    String fileDownloadServlet();

    /**
     * URL to redirect the browser to when the user logs out.
     * 
     * @return a string representing the URL.
     */
    String logoutUrl();

    /**
     * The path to DE help file
     * 
     * @return path to help file;
     */
    String deHelpFile();

    /**
     * Applet Main Class
     * 
     * @return Main class name of the iDrop Lite Applet
     */
    String iDropLiteMainClass();

    /**
     * get relative path of Applet jar
     * 
     * @return the relative path
     */
    String iDropLiteArchivePath();

}
