package org.iplantc.de.diskResource.client.views.widgets;

import com.google.gwt.editor.client.EditorError;

import java.util.List;

/**
 * An interface for all DiskResource selectors.
 * @author jstroot
 *
 */
public interface DiskResourceSelector {
    public interface HasDisableBrowseButtons {
        void disableBrowseButtons();
    }

    List<EditorError> getErrors();

    void setRequired(boolean required);
}
