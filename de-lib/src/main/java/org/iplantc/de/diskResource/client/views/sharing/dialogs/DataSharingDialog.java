/**
 *
 */
package org.iplantc.de.diskResource.client.views.sharing.dialogs;


import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.util.CollaboratorsUtil;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.DataSharingView;
import org.iplantc.de.diskResource.client.model.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.model.DiskResourceNameComparator;
import org.iplantc.de.diskResource.client.presenters.sharing.DataSharingPresenterImpl;
import org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceNameCell;
import org.iplantc.de.diskResource.client.views.sharing.DataSharingViewImpl;

import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.HTML;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sriram, jstroot
 */
public class DataSharingDialog extends IPlantDialog implements SelectHandler {

    private final DiskResourceServiceFacade diskResourceService;
    private final DiskResourceUtil diskResourceUtil;
    private final DataSharingView.Appearance appearance;
    private DataSharingView.Presenter sharingPresenter;

    @Inject CollaboratorsUtil collaboratorsUtil;
    @Inject JsonUtil jsonUtil;

    @Inject
    DataSharingDialog(final DiskResourceServiceFacade diskResourceService,
                      final DiskResourceUtil diskResourceUtil,
                      final DataSharingView.Appearance appearance) {
        super(true);
        this.diskResourceService = diskResourceService;
        this.diskResourceUtil = diskResourceUtil;
        this.appearance = appearance;
        setPixelSize(600, 500);
        setHideOnButtonClick(true);
        setModal(true);
        setResizable(false);
        addHelp(new HTML(appearance.sharePermissionsHelp()));
        setHeadingText(appearance.manageSharing());
        setOkButtonText(appearance.done());
        addOkButtonSelectHandler(this);

    }

    @Override
    public void onSelect(SelectEvent event) {
        Preconditions.checkNotNull(sharingPresenter);
        sharingPresenter.processRequest();
    }

    public void show(final List<DiskResource> resourcesToShare) {
        ListStore<DiskResource> drStore = new ListStore<>(new DiskResourceModelKeyProvider());
        DataSharingView view = new DataSharingViewImpl(buildDiskResourceColumnModel(), drStore);
        sharingPresenter = new DataSharingPresenterImpl(diskResourceService,
                                                        resourcesToShare,
                                                        view,
                                                        collaboratorsUtil,
                                                        jsonUtil);
        sharingPresenter.go(this);
        super.show();
    }

    @Override
    public void show() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This method is not supported for this class. " +
                                                    "Use show(List<DiskResource>) instead.");
    }

    private ColumnModel<DiskResource> buildDiskResourceColumnModel() {
        List<ColumnConfig<DiskResource, ?>> list = new ArrayList<>();

        ColumnConfig<DiskResource, DiskResource> name = new ColumnConfig<>(new IdentityValueProvider<DiskResource>("name"),
                                                                           appearance.dataSharingDlgNameColumnWidth(),
                                                                           appearance.nameColumnLabel());
        name.setCell(new DiskResourceNameCell(diskResourceUtil));
        name.setComparator(new DiskResourceNameComparator());
        list.add(name);

        return new ColumnModel<>(list);
    }

}
