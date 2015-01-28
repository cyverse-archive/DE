/**
 *
 */
package org.iplantc.de.diskResource.client.sharing.views;


import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.sharing.presenter.DataSharingPresenter;
import org.iplantc.de.diskResource.client.views.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.views.DiskResourceNameComparator;
import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceNameCell;
import org.iplantc.de.resources.client.messages.IplantContextualHelpStrings;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

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
 */
public class DataSharingDialog extends IPlantDialog {

    private final IplantDisplayStrings displayStrings;
    private final IplantContextualHelpStrings helpStrings;

    @Inject
    DataSharingDialog(final DiskResourceServiceFacade diskResourceService,
                      final IplantDisplayStrings displayStrings,
                      final IplantContextualHelpStrings helpStrings,
                      @Assisted final Set<DiskResource> resources) {
        super(true);
        this.displayStrings = displayStrings;
        this.helpStrings = helpStrings;
        setPixelSize(600, 500);
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        addHelp();
        setHeadingText(displayStrings.manageSharing());
        ListStore<DiskResource> drStore = new ListStore<>(new DiskResourceModelKeyProvider());
        DataSharingView view = new DataSharingViewImpl(buildDiskResourceColumnModel(), drStore);
        final DataSharingView.Presenter p = new DataSharingPresenter(diskResourceService,
                                                                     getSelectedResourcesAsList(resources), view);
        p.go(this);
        setOkButtonText(displayStrings.done());
        addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                p.processRequest();
            }
        });

    }

    private void addHelp() {
        addHelp(new HTML(helpStrings.sharePermissionsHelp()));
    }

    private ColumnModel<DiskResource> buildDiskResourceColumnModel() {
        List<ColumnConfig<DiskResource, ?>> list = new ArrayList<>();

        ColumnConfig<DiskResource, DiskResource> name = new ColumnConfig<>(new IdentityValueProvider<DiskResource>("name"), 100, displayStrings.name());
        name.setCell(new DiskResourceNameCell());
        name.setComparator(new DiskResourceNameComparator());
        name.setWidth(130);

        list.add(name);

        return new ColumnModel<>(list);
    }

    private List<DiskResource> getSelectedResourcesAsList(Set<DiskResource> models) {
        List<DiskResource> dr = new ArrayList<>();

        for (DiskResource item : models) {
            dr.add(item);
        }

        return dr;

    }

}
