/**
 *
 */
package org.iplantc.de.diskResource.client.sharing.views;


import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.sharing.presenter.DataSharingPresenter;
import org.iplantc.de.diskResource.client.views.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.views.cells.DiskResourceNameCell;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.user.client.ui.HTML;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author sriram
 *
 */
public class DataSharingDialog extends IPlantDialog {

    public DataSharingDialog(Set<DiskResource> resources) {
        super(true);
        setPixelSize(600, 500);
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        addHelp();
        setHeadingText(I18N.DISPLAY.manageSharing());
        ListStore<DiskResource> drStore = new ListStore<DiskResource>(new DiskResourceModelKeyProvider());
        DataSharingView view = new DataSharingViewImpl(buildDiskResourceColumnModel(), drStore);
        final DataSharingView.Presenter p = new DataSharingPresenter(getSelectedResourcesAsList(resources), view);
        p.go(this);
        setOkButtonText(I18N.DISPLAY.done());
        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                p.processRequest();
            }
        });

    }

    private void addHelp() {
        addHelp(new HTML(I18N.HELP.sharePermissionsHelp()));
    }

    private ColumnModel<DiskResource> buildDiskResourceColumnModel() {
        List<ColumnConfig<DiskResource, ?>> list = new ArrayList<ColumnConfig<DiskResource, ?>>();

        ColumnConfig<DiskResource, DiskResource> name = new ColumnConfig<DiskResource, DiskResource>(
                new IdentityValueProvider<DiskResource>(), 130, I18N.DISPLAY.name());
        name.setCell(new DiskResourceNameCell(this, DiskResourceNameCell.CALLER_TAG.SHARING));

        list.add(name);

        return new ColumnModel<DiskResource>(list);
    }

    private List<DiskResource> getSelectedResourcesAsList(Set<DiskResource> models) {
        List<DiskResource> dr = new ArrayList<DiskResource>();

        for (DiskResource item : models) {
            dr.add(item);
        }

        return dr;

    }

}
