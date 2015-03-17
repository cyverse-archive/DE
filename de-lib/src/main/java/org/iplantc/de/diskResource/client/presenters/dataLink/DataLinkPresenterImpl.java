package org.iplantc.de.diskResource.client.presenters.dataLink;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.diskResource.client.presenters.callbacks.CreateDataLinkCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DeleteDataLinksCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.ListDataLinksCallback;
import org.iplantc.de.diskResource.client.DataLinkView;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkViewFactory;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import java.util.List;

/**
 * @author jstroot
 */
public class DataLinkPresenterImpl implements DataLinkView.Presenter {

    private final DataLinkView view;
    private final DiskResourceServiceFacade drService;
    private final DataLinkFactory dlFactory;
    private final DiskResourceUtil diskResourceUtil;

    @Inject
    DataLinkPresenterImpl(final DiskResourceServiceFacade drService,
                          final DataLinkViewFactory dataLinkViewFactory,
                          final DataLinkFactory dlFactory,
                          final DiskResourceUtil diskResourceUtil,
                          @Assisted List<DiskResource> resources) {
        this.drService = drService;
        this.dlFactory = dlFactory;
        this.diskResourceUtil = diskResourceUtil;
        view = dataLinkViewFactory.create(this, resources);

        // Remove Folders
        List<DiskResource> allowedResources = Lists.newArrayList();
        for(DiskResource m : resources){
            if(!(m instanceof Folder)){
                allowedResources.add(m);
            }
        }
        // Retrieve tickets for root nodes
        getExistingDataLinks(allowedResources);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void getExistingDataLinks(List<DiskResource> resources) {
        view.addRoots(resources);
        drService.listDataLinks(diskResourceUtil.asStringPathList(resources),
                                new ListDataLinksCallback(
                view.getTree(),dlFactory));
    }



    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void deleteDataLink(DataLink value) {
        drService.deleteDataLinks(Lists.newArrayList(value.getId()),
                new DeleteDataLinksCallback(view));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void createDataLinks(List<DiskResource> selectedItems) {
        final List<String> drResourceIds = Lists.newArrayList();
        for(DiskResource dr : selectedItems){
            if(!(dr instanceof DataLink)){
                drResourceIds.add(dr.getPath());
            }
        }

        view.mask();
        drService.createDataLinks(drResourceIds, new CreateDataLinkCallback(view));
    }

    @Override
    public String getSelectedDataLinkDownloadUrl() {
        DiskResource model = view.getTree().getSelectionModel().getSelectedItem();
        if (model instanceof DataLink) {
            return ((DataLink)model).getDownloadUrl();
        }
        return null;
    }

    @Override
    public void openSelectedDataLinkDownloadPage() {
        DiskResource model = view.getTree().getSelectionModel().getSelectedItem();
        if (model instanceof DataLink) {
            String url = ((DataLink)model).getDownloadPageUrl();
            WindowUtil.open(url);
        }
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view.asWidget());
    }
}
