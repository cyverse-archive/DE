package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.deployedComps.DeployedComponent;
import org.iplantc.de.client.models.deployedComps.DeployedComponentAutoBeanFactory;
import org.iplantc.de.client.models.deployedComps.DeployedComponentList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class GetDeployedComponentsCallbackConverter extends AsyncCallbackConverter<String, List<DeployedComponent>> {

    private final DeployedComponentAutoBeanFactory factory;

    public GetDeployedComponentsCallbackConverter(AsyncCallback<List<DeployedComponent>> callback, DeployedComponentAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<DeployedComponent> convertFrom(String object) {
        /*Storage localStorege = Storage.getLocalStorageIfSupported();

        if (localStorege != null) {
            String dcStored = localStorege.getItem("deployedComponents");
            if (dcStored == null) {
                localStorege.setItem("deployedComponents", object);
            }
        }*/
        AutoBean<DeployedComponentList> autoBean = AutoBeanCodex.decode(factory, DeployedComponentList.class, object);
        List<DeployedComponent> items = autoBean.as().getDCList();
        return items;
    }

}
