package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface DeployedComponentServices {

    void getAppTemplateDeployedComponent(HasId appTemplateId, AsyncCallback<Tool> callback);

    void getDeployedComponents(AsyncCallback<List<Tool>> callback);

    void searchDeployedComponents(String searchTerm, AsyncCallback<List<Tool>> callback);
}
