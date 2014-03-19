package org.iplantc.de.client.services;

import org.iplantc.de.client.models.toolRequests.NewToolRequest;
import org.iplantc.de.client.models.toolRequests.RequestedToolDetails;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Objects of this type can provide the tool request remote services.
 */
public interface ToolRequestProvider {

    /**
     * Asynchronously requests the installation of a tool.
     * 
     * @param request the tool installation request
     * @param callback the callback with the response from the provider
     */
    void requestInstallation(NewToolRequest request, AsyncCallback<RequestedToolDetails> callback);
    
}
