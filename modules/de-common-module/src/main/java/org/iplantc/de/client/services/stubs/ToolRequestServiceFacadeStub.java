package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.toolRequests.NewToolRequest;
import org.iplantc.de.client.models.toolRequests.RequestedToolDetails;
import org.iplantc.de.client.services.ToolRequestServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class ToolRequestServiceFacadeStub implements ToolRequestServiceFacade {
    @Override
    public void requestInstallation(NewToolRequest request, AsyncCallback<RequestedToolDetails> callback) {

    }
}
