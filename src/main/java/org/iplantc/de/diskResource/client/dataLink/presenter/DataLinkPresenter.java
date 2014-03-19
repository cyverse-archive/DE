package org.iplantc.de.diskResource.client.dataLink.presenter;

import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.diskResource.client.dataLink.presenter.callbacks.CreateDataLinkCallback;
import org.iplantc.de.diskResource.client.dataLink.presenter.callbacks.DeleteDataLinksCallback;
import org.iplantc.de.diskResource.client.dataLink.presenter.callbacks.ListDataLinksCallback;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasOneWidget;

import java.util.List;


public class DataLinkPresenter<M extends DiskResource> implements DataLinkPanel.Presenter<M> {

    private final DataLinkPanel<M> view;
    private final DiskResourceServiceFacade drService = ServicesInjector.INSTANCE.getDiskResourceServiceFacade();
    private final DataLinkFactory dlFactory = GWT.create(DataLinkFactory.class);

    public DataLinkPresenter(List<M> resources) {
        view = new DataLinkPanel<M>(resources);
        view.setPresenter(this);

        // Remove Folders
        List<M> allowedResources = Lists.newArrayList();
        for(M m : resources){
            if(!(m instanceof Folder)){
                allowedResources.add(m);
            }
        }
        // Retrieve tickets for root nodes
        getExistingDataLinks(allowedResources);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void getExistingDataLinks(List<M> resources) {
        view.addRoots(resources);
        drService.listDataLinks(DiskResourceUtil.asStringIdList(resources), new ListDataLinksCallback(
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
    public void deleteDataLinks(List<DataLink> dataLinks){
        List<String> dataLinkIds = Lists.newArrayList();
        for (DataLink dl : dataLinks) {
            dataLinkIds.add(dl.getId());
        }
        view.mask();
        drService.deleteDataLinks(dataLinkIds, new DeleteDataLinksCallback(view));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void createDataLinks(List<M> selectedItems) {
        final List<String> drResourceIds = Lists.newArrayList();
        for(M dr : selectedItems){
            if(!(dr instanceof DataLink)){
                drResourceIds.add(dr.getId());
            }
        }

        view.mask();
        drService.createDataLinks(drResourceIds, new CreateDataLinkCallback(dlFactory, view));
    }

    @Override
    public String getSelectedDataLinkDownloadPage() {
        M model = view.getTree().getSelectionModel().getSelectedItem();
        if(model instanceof DataLink){
            return ((DataLink)model).getDownloadPageUrl();
        }
        return null;
    }

    @Override
    public String getSelectedDataLinkDownloadUrl() {
        M model = view.getTree().getSelectionModel().getSelectedItem();
        if (model instanceof DataLink) {
            return ((DataLink)model).getDownloadUrl();
        }
        return null;
    }

    @Override
    public void openSelectedDataLinkDownloadPage() {
        M model = view.getTree().getSelectionModel().getSelectedItem();
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
