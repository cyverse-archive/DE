package org.iplantc.de.client.models;

/**
 * Classes who implement this interface have the ability to be minimized.
 * 
 * @author jstroot
 * 
 */
public interface IsMinimizable {

    boolean isMinimized();

    void minimize();

}
