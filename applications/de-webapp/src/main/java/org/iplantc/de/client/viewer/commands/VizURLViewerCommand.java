package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.viewer.views.ExternalVizualizationURLViwerImpl;
import org.iplantc.de.client.viewer.views.FileViewer;

import com.google.gwt.json.client.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * @author sriram, jstroot
 * 
 */
public class VizURLViewerCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(final File file,
                                    final String infoType,
                                    final boolean editing,
                                    final Folder parentFolder,
                                    final JSONObject manifest,
                                    final FileViewer.Presenter presenter) {
        FileViewer viewer = new ExternalVizualizationURLViwerImpl(file, infoType);
        return Arrays.asList(viewer);
    }

}
