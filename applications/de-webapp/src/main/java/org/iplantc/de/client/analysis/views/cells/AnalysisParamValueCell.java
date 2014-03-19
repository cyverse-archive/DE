/**
 *
 */
package org.iplantc.de.client.analysis.views.cells;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.apps.integration.ArgumentType;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * @author sriram
 * 
 */
public class AnalysisParamValueCell extends AbstractCell<AnalysisParameter> {

    public AnalysisParamValueCell() {
        super("click");
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, AnalysisParameter value,
            SafeHtmlBuilder sb) {
        String info_type = value.getInfoType();
        // // At present,reference genome info types are not supported by DE
        // viewers
        boolean valid_info_type = isValidInputType(info_type);
        if (value.getType().equals(ArgumentType.Input) && valid_info_type) {
            sb.appendHtmlConstant("<div style=\"cursor:pointer;text-decoration:underline;white-space:pre-wrap;\">"
                    + value.getDisplayValue() + "</div>");
        } else {
            sb.appendHtmlConstant("<div style=\"white-space:pre-wrap;\">" + value.getDisplayValue()
                    + "</div>");
        }

    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent,
            AnalysisParameter value, NativeEvent event, ValueUpdater<AnalysisParameter> valueUpdater) {

        if (value == null) {
            return;
        }

        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        if ("click".equals(event.getType())) {
            String info_type = value.getInfoType();
            boolean valid_info_type = isValidInputType(info_type);
            if (value.getType().equals(ArgumentType.Input) && valid_info_type) {
                launchViewer(value);

            }
        }

    }

    @SuppressWarnings("deprecation")
    private void launchViewer(final AnalysisParameter value) {
        final DiskResourceAutoBeanFactory factory = GWT.create(DiskResourceAutoBeanFactory.class);
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        arr.set(0, new JSONString(value.getDisplayValue()));
        obj.put("paths", arr);
        ServicesInjector.INSTANCE.getDiskResourceServiceFacade().getStat(obj.toString(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject obj = JsonUtil.getObject(result);
                JSONObject json = obj.get("paths").isObject();
                JSONObject fileObj = json.get(value.getDisplayValue()).isObject();
                AutoBean<File> bean = AutoBeanCodex.decode(factory, File.class, fileObj.toString());
                File file = bean.as();
                EventBus.getInstance().fireEvent(
                        new ShowFilePreviewEvent(file, AnalysisParamValueCell.this));

            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.diskResourceDoesNotExist(value.getDisplayValue()));
            }
        });

    }

    public boolean isValidInputType(String info_type) {
        return !info_type.equalsIgnoreCase("ReferenceGenome")
                && !info_type.equalsIgnoreCase("ReferenceSequence")
                && !info_type.equalsIgnoreCase("ReferenceAnnotation");
    }
}
