package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.TextViewerImpl;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class TextViewerDefaultAppearance implements TextViewerImpl.TextViewerAppearance {

    private final FileViewerStrings fileViewerStrings;

    public TextViewerDefaultAppearance() {
        this(GWT.<FileViewerStrings> create(FileViewerStrings.class));
    }

    TextViewerDefaultAppearance(final FileViewerStrings fileViewerStrings) {
        this.fileViewerStrings = fileViewerStrings;
    }

    @Override
    public String markdownPreviewWindowHeader() {
        return fileViewerStrings.markdownPreviewWindowHeader();
    }

    @Override
    public String markdownPreviewWindowWidth() {
        return "600";
    }

    @Override
    public String markdownPreviewWindowHeight() {
        return "500";
    }

    @Override
    public String unsupportedPreviewAlertTitle() {
        return fileViewerStrings.unsupportedPreviewAlertTitle();
    }

    @Override
    public String unsupportedPreviewAlertMsg() {
        return fileViewerStrings.unsupportedPreviewAlertMsg();
    }
}
