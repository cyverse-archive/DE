package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import java.util.List;

public class GetDeployedComponentsCallbackConverter extends AsyncCallbackConverter<String, List<Tool>> {

    private final ToolAutoBeanFactory factory;

    public GetDeployedComponentsCallbackConverter(AsyncCallback<List<Tool>> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected List<Tool> convertFrom(String object) {
        /*Storage localStorege = Storage.getLocalStorageIfSupported();

        if (localStorege != null) {
            String dcStored = localStorege.getItem("deployedComponents");
            if (dcStored == null) {
                localStorege.setItem("deployedComponents", object);
            }
        }*/
        AutoBean<ToolList> autoBean = AutoBeanCodex.decode(factory, ToolList.class, object);
        List<Tool> items = autoBean.as().getToolList();
        return items;
    }

}
