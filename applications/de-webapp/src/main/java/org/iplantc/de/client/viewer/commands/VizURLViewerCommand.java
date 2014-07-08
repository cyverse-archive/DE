package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.viewer.views.ExternalVizualizationURLViwerImpl;
import org.iplantc.de.client.viewer.views.FileViewer;

import java.util.Arrays;
import java.util.List;

/**
 * @author sriram
 * 
 */
public class VizURLViewerCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file,
                                    String infoType,
                                    boolean editing,
                                    Folder parentFolder) {
        FileViewer viewer = new ExternalVizualizationURLViwerImpl(file, infoType);
        return Arrays.asList(viewer);
    }

}
