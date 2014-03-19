package org.iplantc.de.client.events;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.event.shared.EventHandler;

/**
 * Defines a handler for a form file upload completed event.
 * 
 * Subclasses of this handler are called when file upload is complete by the FileUploadPanel
 * 
 * @see org.iplantc.de.client.events.DefaultUploadCompleteHandler
 */
public abstract class UploadCompleteHandler implements EventHandler {
    private final String idParentFolder;

    /**
     * Instantiate from a parent id.
     * 
     * @param idParent unique id for parent folder.
     */
    public UploadCompleteHandler(String idParent) {
        if (idParent == null || idParent.isEmpty()) {
            throw new IllegalArgumentException(I18N.DISPLAY.idParentInvalid());
        }

        this.idParentFolder = idParent;
    }

    /**
     * Retrieve parent id.
     * 
     * @return parent folder's unique id.
     */
    public String getParentId() {
        return idParentFolder;
    }

    /**
     * Upload has completed.
     * 
     * @param sourceUrl the URL the file is being imported from, or the filename if uploading a local
     *            file
     * @param response string representation of our completion response.
     */
    public abstract void onCompletion(String sourceUrl, String response);

    /**
     * Upload completion has finished.
     */
    public abstract void onAfterCompletion();
}
