package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.StructuredTextViewer;

import com.google.gwt.core.client.GWT;

public class StructuredTextViewerDefaultAppearance extends AbstractStructuredTextViewerDefaultAppearance implements StructuredTextViewer.StructuredTextViewerAppearance{

    private final StructuredTextViewerMessages messages;

    public StructuredTextViewerDefaultAppearance() {
        this(GWT.<StructuredTextViewerMessages> create(StructuredTextViewerMessages.class));
    }

    StructuredTextViewerDefaultAppearance(final StructuredTextViewerMessages messages){
        this.messages = messages;
    }

    @Override
    public String createNewDefaultColumnValue(int column) {
        return messages.sampleColumnText(column);
    }

    @Override
    public String defaultViewName() {
        return messages.defaultViewName();
    }

    @Override
    public String gridToolTip() {
        return messages.gridToolTip();
    }

    @Override
    public String viewName(String fileName) {
        return messages.viewName(fileName);
    }
}
