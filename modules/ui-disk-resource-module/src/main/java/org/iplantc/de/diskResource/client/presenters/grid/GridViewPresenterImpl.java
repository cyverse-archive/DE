package org.iplantc.de.diskResource.client.presenters.grid;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewFactory;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectDiskResourceByIdStoreAddHandler;
import org.iplantc.de.diskResource.client.model.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.events.ManageCommentsEvent;
import org.iplantc.de.diskResource.client.events.ManageMetadataEvent;
import org.iplantc.de.diskResource.client.events.ManageSharingEvent;
import org.iplantc.de.diskResource.client.events.RequestDiskResourceFavoriteEvent;
import org.iplantc.de.diskResource.client.events.ShareByDataLinkEvent;
import org.iplantc.de.diskResource.client.views.grid.DiskResourceColumnModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

import java.util.HashMap;
import java.util.List;

/**
 * @author jstroot
 */
public class GridViewPresenterImpl implements GridView.Presenter, DiskResourcePathSelectedEvent.DiskResourcePathSelectedEventHandler {


    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject EventBus eventBus;
    @Inject DiskResourceUtil diskResourceUtil;
    private final NavigationView.Presenter navigationPresenter;
    private final ListStore<DiskResource> listStore;
    private final GridView view;
    private final HashMap<EventHandler, HandlerRegistration> registeredHandlers = Maps.newHashMap();
    private boolean filePreviewEnabled = true;
    private DiskResourceView.Presenter parentPresenter;

    @Inject
    GridViewPresenterImpl(final GridViewFactory gridViewFactory,
                          final FolderContentsRpcProxyFactory folderContentsProxyFactory,
                          @Assisted final NavigationView.Presenter navigationPresenter,
                          @Assisted final List<InfoType> infoTypeFilters,
                          @Assisted final TYPE entityType){
        this.navigationPresenter = navigationPresenter;
        this.listStore = new ListStore<>(new DiskResourceModelKeyProvider());
        DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy = folderContentsProxyFactory.createWithEntityType(infoTypeFilters, entityType);

        this.view = gridViewFactory.create(this, listStore, folderContentsRpcProxy);

        // Wire up Column Model events
        DiskResourceColumnModel cm = this.view.getColumnModel();
        cm.addDiskResourceNameSelectedEventHandler(this);
        cm.addManageSharingEventHandler(this);
        cm.addManageMetadataEventHandler(this);
        cm.addShareByDataLinkEventHandler(this);
        cm.addManageFavoritesEventHandler(this);
        cm.addManageCommentsEventHandler(this);
        cm.addDiskResourcePathSelectedEventHandler(this);

    }

    @Override
    public void deSelectDiskResources() {
        view.getSelectionModel().deselectAll();
    }

    @Override
    public void doMoveDiskResources(Folder targetFolder, List<DiskResource> resources) {
        parentPresenter.doMoveDiskResources(targetFolder, resources);
    }

    @Override
    public Element findGridRow(Element eventTargetElement) {
        return view.findGridRow(eventTargetElement);
    }

    @Override
    public int findGridRowIndex(Element targetRow) {
        return view.findGridRowIndex(targetRow);
    }

    @Override
    public List<DiskResource> getAllDiskResources() {
        return listStore.getAll();
    }

    @Override
    public List<DiskResource> getSelectedDiskResources() {
        return view.getSelectionModel().getSelectedItems();
    }

    @Override
    public Folder getSelectedUploadFolder() {
        return navigationPresenter.getSelectedUploadFolder();
    }

    @Override
    public GridView getView() {
        return view;
    }

    @Override
    public boolean isSelectAllChecked() {
        return view.getSelectionModel().isSelectAllChecked();
    }

    @Override
    public void loadFolderContents(Folder folderToSelect) {

    }

    @Override
    public void setFilePreviewEnabled(boolean filePreviewEnabled) {
        this.filePreviewEnabled = filePreviewEnabled;
    }

    @Override
    public void onDiskResourcePathSelected(DiskResourcePathSelectedEvent event) {
        final OpenFolderEvent openFolderEvent = new OpenFolderEvent(diskResourceUtil.parseParent(event.getSelectedDiskResource().getPath()));
        openFolderEvent.requestNewView(true);
        eventBus.fireEvent(openFolderEvent);
    }

    @Override
    public void setParentPresenter(DiskResourceView.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void setSelectedDiskResources(List<? extends HasId> diskResourcesToSelect) {

    }

    @Override
    public void setSelectedDiskResourcesById(List<? extends HasId> diskResourcesToSelect) {
        SelectDiskResourceByIdStoreAddHandler diskResourceByIdStoreAddHandler = new SelectDiskResourceByIdStoreAddHandler(diskResourcesToSelect, this);
        HandlerRegistration diskResHandlerReg = listStore.addStoreAddHandler(diskResourceByIdStoreAddHandler);
        addEventHandlerRegistration(diskResourceByIdStoreAddHandler, diskResHandlerReg);
    }

    @Override
    public void unRegisterHandler(EventHandler handler) {
        if(registeredHandlers.containsKey(handler)){
            registeredHandlers.remove(handler).removeHandler();
        }
    }

    public void addEventHandlerRegistration(EventHandler handler, HandlerRegistration reg) {
        registeredHandlers.put(handler, reg);
    }

    @Override
    public DiskResource updateDiskResource(DiskResource diskResource) {
        Preconditions.checkNotNull(diskResource);
        final DiskResource modelWithKey = listStore.findModelWithKey(diskResource.getId());
        if(modelWithKey == null){
            return null;
        }

        final DiskResource updated = diskResourceService.combineDiskResources(diskResource, modelWithKey);
        listStore.update(updated);
        return updated;
    }

    @Override
    public void onDiskResourceNameSelected(DiskResourceNameSelectedEvent event) {

        if(!(event.getSelectedItem() instanceof File) || !filePreviewEnabled){
            return;
        }
        eventBus.fireEvent(new ShowFilePreviewEvent((File)event.getSelectedItem(), this));
    }

    @Override
    public void onFavoriteRequest(RequestDiskResourceFavoriteEvent event) {

    }

    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        final Folder selectedFolder = event.getSelectedFolder();

        final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader = view.getGridLoader();
        gridLoader.getLastLoadConfig().setFolder(selectedFolder);
        gridLoader.getLastLoadConfig().setOffset(0);
        gridLoader.load();
    }

    @Override
    public void onManageComments(ManageCommentsEvent event) {

    }

    @Override
    public void onRequestManageMetadata(ManageMetadataEvent event) {

    }

    @Override
    public void onRequestManageSharing(ManageSharingEvent event) {

    }

    @Override
    public void onRequestShareByDataLink(ShareByDataLinkEvent event) {

    }
}
