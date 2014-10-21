package org.iplantc.de.client.services.converters;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

public class GetAppTemplateDeployedComponentConverter extends AsyncCallbackConverter<String, Tool> {

    private final ToolAutoBeanFactory factory;

    public GetAppTemplateDeployedComponentConverter(AsyncCallback<Tool> callback, ToolAutoBeanFactory factory) {
        super(callback);
        this.factory = factory;
    }

    @Override
    protected Tool convertFrom(String object) {

        AutoBean<ToolList> autoBean = AutoBeanCodex.decode(factory, ToolList.class, object);
        if ((autoBean.as().getToolList() != null) && !autoBean.as().getToolList().isEmpty()) {
            // JDS This converter is for an AppTemplate, which should only have 1 deployed component.
            return autoBean.as().getToolList().get(0);
        }
        return null;
    }

}
