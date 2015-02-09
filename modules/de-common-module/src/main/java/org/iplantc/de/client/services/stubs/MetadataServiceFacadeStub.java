package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.tags.Tag;
import org.iplantc.de.client.services.TagsServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MetadataServiceFacadeStub implements TagsServiceFacade {

    @Override
    public void createTag(String tag, AsyncCallback<Tag> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void suggestTag(String text, int limit, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateTagDescription(Tag tag, AsyncCallback<Void> callback) {
        // TODO Auto-generated method stub

    }

}
