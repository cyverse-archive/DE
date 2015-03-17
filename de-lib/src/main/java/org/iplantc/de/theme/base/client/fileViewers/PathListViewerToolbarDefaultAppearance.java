package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.PathListViewerToolbar;

import com.google.gwt.resources.client.ImageResource;

public class PathListViewerToolbarDefaultAppearance extends AbstractToolBarDefaultAppearance implements PathListViewerToolbar.PathListViewerToolbarAppearance {

    @Override
    public String deleteSelectedPathsButtonTooltip() {
        return fileViewerStrings.deleteSelectedPathsButtonTooltip();
    }

    @Override
    public ImageResource deleteSelectedPathsButtonIcon() {
        return resources.delete();
    }

    @Override
    public String addPathsButtonTooltip() {
        return fileViewerStrings.addPathsButtonTooltip();
    }

    @Override
    public ImageResource addPathsButtonIcon() {
        return resources.add();
    }
}
