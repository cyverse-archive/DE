package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.views.PathListViewer;

public class PathListViewerDefaultAppearance extends AbstractToolBarDefaultAppearance implements PathListViewer.PathListViewerAppearance {
    @Override
    public String columnHeaderText() {
        return fileViewerStrings.pathListColumnHeaderText();
    }

    @Override
    public String pathListViewName(String name) {
        return fileViewerStrings.pathListViewName(name);
    }
}
