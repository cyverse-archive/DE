package org.iplantc.de.commons.client.views.window.configs;

/**
 * This is the persisted state information particular to the system messages window.
 */
public interface SystemMessagesWindowConfig extends WindowConfig {

    /**
     * retrieves the persisted id of the previously selected message
     */
    String getSelectedMessage();

    /**
     * set the id of the currently selected messaged to be persisted.
     */
    void setSelectedMessage(String id);

}
