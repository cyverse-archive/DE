package org.iplantc.de.client.services.impl;

import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

public class FileSystemMetadataServiceFacadeImpl implements FileSystemMetadataServiceFacade {

    @Override
    public void getFavorites(AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addToFavorites(ArrayList<String> UUID, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeFromFavorites(ArrayList<String> UUID, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getComments(String UUID, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void addComment(String UUID, String comment, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void markAsRetracted(String UUID, String commentId, boolean retracted, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void attachTag(String UUID, String tagId, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void detachTag(String UUID, String tagId, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

}
