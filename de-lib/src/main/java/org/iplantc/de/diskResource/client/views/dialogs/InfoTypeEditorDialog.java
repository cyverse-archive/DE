/**
 *
 */
package org.iplantc.de.diskResource.client.views.dialogs;

import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author sriram, jstroot
 */
public class InfoTypeEditorDialog extends IPlantDialog {

    private final DiskResourceServiceFacade diskResourceService;

    private final SimpleComboBox<InfoType> infoTypeCbo;

    final Logger LOG = Logger.getLogger(InfoTypeEditorDialog.class.getName());

    @Inject
    InfoTypeEditorDialog(final DiskResourceServiceFacade diskResourceService) {
        this.diskResourceService = diskResourceService;
        setSize("300", "100");
        setHeadingText("Select Type");
        infoTypeCbo = new SimpleComboBox<>(new LabelProvider<InfoType>() {

            @Override
            public String getLabel(InfoType item) {
                return item.toString();
            }

        });
        infoTypeCbo.setAllowBlank(true);
        infoTypeCbo.setEmptyText("-");
        infoTypeCbo.setTriggerAction(TriggerAction.ALL);
        infoTypeCbo.setEditable(false);
        add(infoTypeCbo);

    }

    public InfoType getSelectedValue() {
        return infoTypeCbo.getCurrentValue();
    }

    public void show(final InfoType currentType){
        loadInfoTypes(currentType);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported. Instead, you must use show(InfoType) for this class.");
    }

    private void loadInfoTypes(final InfoType currentType) {
        diskResourceService.getInfoTypes(new AsyncCallback<List<InfoType>>() {

            @Override
            public void onFailure(Throwable arg0) {
                ErrorHandler.post(arg0);
            }

            @Override
            public void onSuccess(List<InfoType> infoTypes) {
                // Skip Path list, it should not be displayed
                infoTypes.remove(InfoType.HT_ANALYSIS_PATH_LIST);
                infoTypeCbo.add(infoTypes);
                infoTypeCbo.setValue(currentType);
                LOG.fine("InfoTypes retrieved: " + infoTypes);
            }
        });
    }

}
