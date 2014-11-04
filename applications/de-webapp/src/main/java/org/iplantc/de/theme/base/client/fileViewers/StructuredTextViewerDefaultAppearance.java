package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.StructuredTextViewer;

import com.google.gwt.core.client.GWT;

public class StructuredTextViewerDefaultAppearance extends AbstractStructuredTextViewerDefaultAppearance implements StructuredTextViewer.StructuredTextViewerAppearance{

    private final FileViewerStrings displayStrings;

    public StructuredTextViewerDefaultAppearance() {
        this(GWT.<FileViewerStrings> create(FileViewerStrings.class));
    }

    StructuredTextViewerDefaultAppearance(final FileViewerStrings displayStrings){
        this.displayStrings = displayStrings;
    }

    @Override
    public String createNewDefaultColumnValue(int column) {
        return displayStrings.sampleColumnText(column);
    }

    @Override
    public String defaultViewName() {
        return displayStrings.defaultViewName();
    }

    @Override
    public String gridToolTip() {
        return displayStrings.gridToolTip();
    }

    @Override
    public String viewName(String fileName) {
        return displayStrings.viewName(fileName);
    }
}
