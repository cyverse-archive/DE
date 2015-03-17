package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;

public class FileSystemMetadataServiceFacadeStub implements FileSystemMetadataServiceFacade {

    @Override
    public void getFavorites(final List<InfoType> infoTypeFilters,
                             final TYPE entityType,
                             final FilterPagingLoadConfigBean configBean, AsyncCallback<Folder> callback) {
        // TODO Auto-generated method stub

    }

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
    public void markAsRetracted(String UUID, String commentId, boolean retracted, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void attachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void detachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getTags(HasId hasId, AsyncCallback<List<Tag>> callback) {
        // TODO Auto-generated method stub

    }

}
