package org.iplantc.de.client.viewer.commands;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.viewer.views.FileViewer;
import org.iplantc.de.client.viewer.views.ShellScriptViewerImpl;

import java.util.Arrays;
import java.util.List;

public class ShellScriptViewCommand implements ViewCommand {

    @Override
    public List<FileViewer> execute(File file, String infoType, boolean editing, Folder parentFolder) {
        FileViewer view = new ShellScriptViewerImpl(file, editing);
        return Arrays.asList(view);
    }

}
