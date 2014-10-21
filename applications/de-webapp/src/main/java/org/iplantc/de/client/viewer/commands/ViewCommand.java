package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.viewer.views.FileViewer;

import com.google.gwt.json.client.JSONObject;

import java.util.List;

/**
 * Basic interface for command pattern
 * 
 * @author sriram, jstroot
 * 
 */
public interface ViewCommand {
    /**
     * Execute command.
     */
    List<? extends FileViewer> execute(File file,
                                       String infoType,
                                       boolean editing,
                                       Folder parentFolder,
                                       JSONObject manifest,
                                       FileViewer.Presenter presenter);
}
