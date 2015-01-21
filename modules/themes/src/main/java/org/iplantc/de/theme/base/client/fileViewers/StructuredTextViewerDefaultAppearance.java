package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.StructuredTextViewer;

/**
 * @author jstroot
 */
public class StructuredTextViewerDefaultAppearance extends AbstractStructuredTextViewerDefaultAppearance implements StructuredTextViewer.StructuredTextViewerAppearance{

    @Override
    public String createNewDefaultColumnValue(int column) {
        return fileViewerStrings.sampleColumnText(column);
    }

    @Override
    public String defaultViewName() {
        return fileViewerStrings.defaultTabularViewName();
    }

    @Override
    public String gridToolTip() {
        return fileViewerStrings.gridToolTip();
    }

    @Override
    public String viewName(String fileName) {
        return fileViewerStrings.viewName(fileName);
    }
}
