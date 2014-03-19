package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public interface DeployedComponentServices {

    void getAppTemplateDeployedComponent(HasId appTemplateId, AsyncCallback<DeployedComponent> callback);

    void getDeployedComponents(AsyncCallback<List<DeployedComponent>> callback);

    void searchDeployedComponents(String searchTerm, AsyncCallback<List<DeployedComponent>> callback);
}
