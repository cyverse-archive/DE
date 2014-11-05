package org.iplantc.de.test.fileViewers.client.appearance;

import org.iplantc.de.fileViewers.client.views.PathListViewer;

import com.google.inject.Inject;

public class FileSetViewerAppearanceTestImpl implements PathListViewer.PathListViewerAppearance {

    @Inject
    public FileSetViewerAppearanceTestImpl(){ }

    @Override
    public String columnHeaderText() {
        return null;
    }

    @Override
    public String pathListViewName(String name) {
        return null;
    }
}
