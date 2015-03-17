package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.services.ToolServices;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class ToolServicesStub implements ToolServices {
    @Override
    public void getDeployedComponents(AsyncCallback<List<Tool>> callback) {

    }

    @Override
    public void searchDeployedComponents(String searchTerm, AsyncCallback<List<Tool>> callback) {

    }
}
