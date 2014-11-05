package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.services.FileEditorServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorServiceFacadeStub implements FileEditorServiceFacade {
    @Override
    public void getManifest(File idFile, AsyncCallback<String> callback) {

    }

    @Override
    public String getPathListFileIdentifier() {
        return null;
    }

    @Override
    public String getServletDownloadUrl(String path) {
        return null;
    }

    @Override
    public void readCsvChunk(File file, String delimiter, int pageNumber, long chunkSize,
                             AsyncCallback<String> callback) {

    }

    @Override
    public void readChunk(File file, long chunkPosition, long chunkSize,
                          AsyncCallback<String> callback) {

    }

    @Override
    public void getTreeUrl(String idFile, boolean refresh, AsyncCallback<String> callback) {

    }

    @Override
    public void viewGenomes(JSONObject pathArray, AsyncCallback<String> callback) {

    }

    @Override
    public void uploadTextAsFile(String destination, String fileContents, boolean newFile, AsyncCallback<String> callback) {

    }
}
