package org.iplantc.de.client.services;

import org.iplantc.de.client.models.tags.Tag;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TagsServiceFacade {

    public void createTag(String tagText, AsyncCallback<Tag> callback);

    public void suggestTag(String text, int limit, AsyncCallback<String> callback);

    public void updateTagDescription(Tag tag, AsyncCallback<Void> callback);
}
