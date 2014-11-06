/**
 * 
 */
package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram
 *
 */
public class InfoTypeEditorDialog extends IPlantDialog {
    
    private DiskResourceServiceFacade facade;
    
    private SimpleComboBox<String> infoTypeCbo;

    private String type;


    public InfoTypeEditorDialog(String currentType) {
        setSize("300", "100");
        this.type = currentType;
        setHeadingText("Select Type");
        this.facade = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
        infoTypeCbo = new SimpleComboBox<String>(new LabelProvider<String>() {

            @Override
            public String getLabel(String item) {
                return item;
            }
            
        });
        infoTypeCbo.setAllowBlank(true);
        infoTypeCbo.setEmptyText("-");
        infoTypeCbo.setTriggerAction(TriggerAction.ALL);
        infoTypeCbo.setEditable(false);
        loadInfoTypes();
        add(infoTypeCbo);
        
    }
    
    public String getSelectedValue() {
        return infoTypeCbo.getCurrentValue();
    }
    
    
    private void loadInfoTypes() {
        facade.getFileTypes(new AsyncCallback<String>() {
            
            @Override
            public void onSuccess(String result) {
               JSONObject obj = JsonUtil.getObject(result);
               JSONArray typesArr = JsonUtil.getArray(obj, "types");
               List<String> types = new ArrayList<String>();
               if(typesArr != null && typesArr.size() >0) {
                  
                   for (int i = 0;i < typesArr.size(); i++) {
                      types.add(typesArr.get(i).isString().stringValue());
                   }
               }
               infoTypeCbo.add(types);
               infoTypeCbo.setValue(type);
            }
            
            @Override
            public void onFailure(Throwable arg0) {
               ErrorHandler.post(arg0);
                
            }
        });
    }

}
