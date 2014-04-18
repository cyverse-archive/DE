package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.client.services.DeployedComponentServices;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class DeployedComponentServicesStub implements DeployedComponentServices {
    @Override
    public void getAppTemplateDeployedComponent(HasId appTemplateId, AsyncCallback<DeployedComponent> callback) {

    }

    @Override
    public void getDeployedComponents(AsyncCallback<List<DeployedComponent>> callback) {

    }

    @Override
    public void searchDeployedComponents(String searchTerm, AsyncCallback<List<DeployedComponent>> callback) {

    }
}
