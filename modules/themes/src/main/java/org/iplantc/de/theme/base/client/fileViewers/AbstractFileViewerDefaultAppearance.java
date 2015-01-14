package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.AbstractFileViewer;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AbstractFileViewerDefaultAppearance implements AbstractFileViewer.AbstractFileViewerAppearance{
    private final FileViewerStrings fileViewerStrings;

    public AbstractFileViewerDefaultAppearance() {
        this(GWT.<FileViewerStrings> create(FileViewerStrings.class));
    }

    AbstractFileViewerDefaultAppearance(final FileViewerStrings fileViewerStrings) {
        this.fileViewerStrings = fileViewerStrings;
    }

    @Override
    public String defaultViewName(double defaultName) {
        return fileViewerStrings.defaultViewName(defaultName);
    }
}
