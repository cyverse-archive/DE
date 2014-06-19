package org.iplantc.de.client.services;

import org.iplantc.de.client.models.tags.IplantTag;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TagsServiceFacade {

    public void createTag(IplantTag tag, AsyncCallback<String> callback);

    public void suggestTag(String text, int limit, AsyncCallback<String> callback);

    public void updateTagDescription(String tagId, String description, AsyncCallback<String> callback);
}
