package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.StructuredTextViewToolBar;

import com.google.gwt.resources.client.ImageResource;

public class StructuredTextViewToolbarDefaultAppearance extends AbstractToolBarDefaultAppearance implements StructuredTextViewToolBar.StructureTextViewerToolbarAppearance{

    @Override
    public ImageResource addRowButtonIcon() {
        return resources.add();
    }

    @Override
    public ImageResource deleteRowButtonIcon() {
        return resources.delete();
    }

    @Override
    public String skipRowsLabelText() {
        return fileViewerStrings.skipLinesLabel();
    }

    @Override
    public String skipRowsCountWidth() {
        return "30";
    }

    @Override
    public String cbxHeaderRowsLabel() {
        return fileViewerStrings.headerRowsLabel();
    }

    @Override
    public String addRowButtonTooltip() {
        return fileViewerStrings.addRowButtonTooltip();
    }

    @Override
    public String deleteRowButtonTooltip() {
        return fileViewerStrings.deleteRowButtonTooltip();
    }
}
