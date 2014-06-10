/**
 * 
 */
package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;

/**
 * @author sriram
 *
 */
public interface FileSystemMetadataServiceFacade {

    public void getFavorites(AsyncCallback<String> callback);
    
    public void addToFavorites(ArrayList<String> UUID, AsyncCallback<String> callback);

    public void removeFromFavorites(ArrayList<String> UUID, AsyncCallback<String> callback);

    public void getComments(String UUID, AsyncCallback<String> callback);

    public void addComment(String UUID, String comment, AsyncCallback<String> callback);

    public void markAsRetracted(String UUID, String commentId, boolean retracted, AsyncCallback<String> callback);

    public void attachTag(String UUID, String tagId, AsyncCallback<String> callback);

    public void detachTag(String UUID, String tagId, AsyncCallback<String> callback);
 
}
