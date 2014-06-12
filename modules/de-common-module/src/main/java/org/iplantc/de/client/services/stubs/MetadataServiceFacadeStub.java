package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.services.MetadataServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MetadataServiceFacadeStub implements MetadataServiceFacade {

    @Override
    public void createTag(String value, String description, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

    @Override
    public void suggestTag(String text, int limit, AsyncCallback<String> callback) {
        // TODO Auto-generated method stub

    }

}
