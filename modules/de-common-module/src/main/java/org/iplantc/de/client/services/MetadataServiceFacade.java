package org.iplantc.de.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MetadataServiceFacade {

    public void createTag(String value, String description, AsyncCallback<String> callback);

    public void suggestTag(String text, AsyncCallback<String> callback);

}
