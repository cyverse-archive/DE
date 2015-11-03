package org.iplantc.de.tools.requests.client.views;

import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.HasSubmitCompleteHandlers;
import com.sencha.gxt.widget.core.client.form.IsField;

/**
 * This class manages the view and submission of an upload form.
 * 
 * TODO move this class to ui-commons and consider converting the simple upload form to using it.
 */
public interface Uploader extends IsField<String>, HasSubmitCompleteHandlers {

    /**
     * Mark the upload file as invalid.
     * 
     * @param reason The reason the file is invalid.
     */
    void markInvalid(String reason);

    /**
     * Submit the file to the server.
     */
    void submit();

}
