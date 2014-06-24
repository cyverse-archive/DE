/**
 * 
 */
package org.iplantc.de.client.services;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfigBean;

import java.util.List;

/**
 * @author sriram
 *
 */
public interface MetadataServiceFacade {

    public void getFavorites(final FilterPagingLoadConfigBean configBean,
                             AsyncCallback<Folder> asyncCallback);
    
    public void addToFavorites(String UUID, AsyncCallback<String> callback);

    public void removeFromFavorites(String UUID, AsyncCallback<String> callback);

    public void getComments(String UUID, AsyncCallback<String> callback);

    public void addComment(String UUID, String comment, AsyncCallback<String> callback);

    public void markAsRetracted(String UUID, String commentId, boolean retracted, AsyncCallback<String> callback);

    public void attachTags(List<String> tagIds, String objectId, AsyncCallback<String> callback);

    public void detachTags(List<String> tagIds, String objectId, AsyncCallback<String> callback);
    
    public void getTags(String UUID, AsyncCallback<String> callback);
 
}
