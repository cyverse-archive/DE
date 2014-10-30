package org.iplantc.de.test.fileViewers.client.serviceStubs;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.test.fileViewers.client.json.JsonDataResources;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class FileEditorServiceFacadeStub implements FileEditorServiceFacade{

    @Inject JsonDataResources jsonDataResources;

    @Inject
    public FileEditorServiceFacadeStub(){

    }

    @Override
    public void getManifest(File file, AsyncCallback<String> callback) {

    }

    @Override
    public String getServletDownloadUrl(String path) {
        return null;
    }

    @Override
    public void readCsvChunk(File file, String delimiter, int pageNumber, long chunkSize,
                             AsyncCallback<String> callback) {

        callback.onSuccess(jsonDataResources.readCsvChunkResponse().getText());
    }

    @Override
    public void readChunk(File file, long chunkPosition, long chunkSize,
                          AsyncCallback<String> callback) {

    }

    @Override
    public void getTreeUrl(String pathToFile, boolean refresh, AsyncCallback<String> callback) {

    }

    @Override
    public void viewGenomes(JSONObject pathArray, AsyncCallback<String> callback) {

    }

    @Override
    public void uploadTextAsFile(String destination, String fileContents, boolean newFile,
                                 AsyncCallback<String> callback) {

    }
}
