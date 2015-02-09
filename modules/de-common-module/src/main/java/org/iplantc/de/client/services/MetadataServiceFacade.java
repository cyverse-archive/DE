/**
 * 
 */
package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.tags.Tag;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * @author sriram, jstroot
 *
 */
public interface MetadataServiceFacade {

    public void addToFavorites(String UUID, AsyncCallback<String> callback);

    public void removeFromFavorites(String UUID, AsyncCallback<String> callback);

    public void getComments(String UUID, AsyncCallback<String> callback);

    public void addComment(String UUID, String comment, AsyncCallback<String> callback);

    public void markAsRetracted(String UUID, String commentId, boolean retracted, AsyncCallback<String> callback);

    public void attachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback);

    public void detachTags(List<Tag> tags, HasId hasId, AsyncCallback<Void> callback);
    
    public void getTags(HasId hasId, AsyncCallback<List<Tag>> callback);
 
}
