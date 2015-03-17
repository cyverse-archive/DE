package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.AbstractStructuredTextViewer;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AbstractStructuredTextViewerDefaultAppearance implements AbstractStructuredTextViewer.AbstractStructuredTextViewerAppearance {
    final FileViewerStrings fileViewerStrings;
    final FileViewerErrorStrings errorStrings;

    public AbstractStructuredTextViewerDefaultAppearance() {
        this(GWT.<FileViewerStrings> create(FileViewerStrings.class),
             GWT.<FileViewerErrorStrings> create(FileViewerErrorStrings.class));
    }

    AbstractStructuredTextViewerDefaultAppearance(final FileViewerStrings fileViewerStrings,
                                                  final FileViewerErrorStrings errorStrings){
        this.fileViewerStrings = fileViewerStrings;
        this.errorStrings = errorStrings;
    }

}
