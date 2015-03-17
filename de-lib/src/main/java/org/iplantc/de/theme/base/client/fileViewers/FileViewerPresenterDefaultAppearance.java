package org.iplantc.de.theme.base.client.fileViewers;

import org.iplantc.de.fileViewers.client.FileViewer;

import com.google.gwt.core.client.GWT;

public class FileViewerPresenterDefaultAppearance implements FileViewer.FileViewerPresenterAppearance {


    private final FileViewerStrings fileViewerStrings;

    public FileViewerPresenterDefaultAppearance(){
        this(GWT.<FileViewerStrings> create(FileViewerStrings.class));
    }

    FileViewerPresenterDefaultAppearance(final FileViewerStrings fileViewerStrings){
        this.fileViewerStrings = fileViewerStrings;
    }

    @Override
    public String fileOpenMsg() {
        return fileViewerStrings.fileOpenMsg();
    }

    @Override
    public String initializingFileViewer() {
        return fileViewerStrings.initializingFileViewer();
    }

    @Override
    public String retrieveFileManifestFailed() {
        return fileViewerStrings.retrieveManifestFailed();
    }

    @Override
    public String retrieveFileManifestMask() {
        return fileViewerStrings.retrieveFileManifestMask();
    }

    @Override
    public String retrieveTreeUrlsMask() {
        return fileViewerStrings.retrieveTreeUrlsMask();
    }

    @Override
    public String retrievingFileContentsMask() {
        return fileViewerStrings.retrievingFileContentsMask();
    }

    @Override
    public String savingMask() {
        return fileViewerStrings.savingMask();
    }

    @Override
    public String unableToRetrieveFileData(String fileName) {
        return fileViewerStrings.unableToRetrieveFileData(fileName);
    }
}
