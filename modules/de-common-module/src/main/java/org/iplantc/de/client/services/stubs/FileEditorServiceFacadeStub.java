package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.services.FileEditorServiceFacade;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileEditorServiceFacadeStub implements FileEditorServiceFacade {
    @Override
    public void getManifest(String idFile, AsyncCallback<String> callback) {

    }

    @Override
    public String getServletDownloadUrl(String path) {
        return null;
    }

    @Override
    public void getData(String url, AsyncCallback<String> callback) {

    }

    @Override
    public void getDataChunk(String url, JSONObject body, AsyncCallback<String> callback) {

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
