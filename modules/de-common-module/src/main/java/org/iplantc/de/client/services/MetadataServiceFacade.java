package org.iplantc.de.client.services;

import org.iplantc.de.client.models.tags.IplantTag;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface MetadataServiceFacade {

    public void createTag(IplantTag tag, AsyncCallback<String> callback);

    public void suggestTag(String text, int limit, AsyncCallback<String> callback);

    public void updateTagDescription(String tagId, String description, AsyncCallback<String> callback);
    
    public void attachTags(List<String> tagIds, String objectId, AsyncCallback<String> callback);
    
    public void detachTags(List<String> tagIds, String objectId, AsyncCallback<String> callback);

}
