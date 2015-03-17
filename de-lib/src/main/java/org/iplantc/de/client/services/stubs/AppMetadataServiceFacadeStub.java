package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.AppMetadataServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class AppMetadataServiceFacadeStub implements AppMetadataServiceFacade {

    @Override
    public void addToFavorites(String UUID, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeFromFavorites(String UUID, AsyncCallback<String> callback) {
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
    public void markAsRetracted(String UUID,
                                String commentId,
                                boolean retracted,
                                AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void attachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {

    }

    @Override
    public void detachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {

    }

    @Override
    public void getTags(HasId hasId, AsyncCallback<List<Tag>> callback) {

    }

}
