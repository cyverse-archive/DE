package org.iplantc.de.diskResource.client.presenters.grid;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.FileSystemMetadataServiceFacade;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.CommentsView;
import org.iplantc.de.commons.client.comments.gin.factory.CommentsPresenterFactory;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.RequestDiskResourceFavoriteEvent;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelected;
import org.iplantc.de.diskResource.client.gin.factory.DataSharingDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderContentsRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewFactory;
import org.iplantc.de.diskResource.client.metadata.presenter.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.metadata.presenter.MetadataPresenter;
import org.iplantc.de.diskResource.client.metadata.view.DiskResourceMetadataView;
import org.iplantc.de.diskResource.client.model.DiskResourceModelKeyProvider;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectDiskResourceByIdStoreAddHandler;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.sharing.views.DataSharingDialog;
import org.iplantc.de.diskResource.client.views.dialogs.InfoTypeEditorDialog;
import org.iplantc.de.diskResource.client.views.grid.DiskResourceColumnModel;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.event.StoreUpdateEvent;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * @author jstroot
 */
public class GridViewPresenterImpl implements GridView.Presenter,
                                              DiskResourcePathSelectedEvent.DiskResourcePathSelectedEventHandler, DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler {


    private class CreateDataLinksCallback implements AsyncCallback<List<DataLink>> {

        @Override
        public void onSuccess(List<DataLink> result) {
            showShareLink(result.get(0).getDownloadUrl());
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.createDataLinksError(), caught);
        }
    }

    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject EventBus eventBus;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject DataSharingDialogFactory dataSharingDialogFactory;
    @Inject IplantAnnouncer announcer;
    @Inject CommentsPresenterFactory commentsPresenterFactory;
    @Inject FileSystemMetadataServiceFacade metadataService;
    private final Appearance appearance;
    private final NavigationView.Presenter navigationPresenter;
    private final ListStore<DiskResource> listStore;
    private final GridView view;
    private final HashMap<EventHandler, HandlerRegistration> registeredHandlers = Maps.newHashMap();
    private boolean filePreviewEnabled = true;
    private DiskResourceView.Presenter parentPresenter;

    @Inject
    GridViewPresenterImpl(final GridViewFactory gridViewFactory,
                          final FolderContentsRpcProxyFactory folderContentsProxyFactory,
                          final GridView.Presenter.Appearance appearance,
                          @Assisted final NavigationView.Presenter navigationPresenter,
                          @Assisted final List<InfoType> infoTypeFilters,
                          @Assisted final TYPE entityType){
        this.appearance = appearance;
        this.navigationPresenter = navigationPresenter;
        this.listStore = new ListStore<>(new DiskResourceModelKeyProvider());
        DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy = folderContentsProxyFactory.createWithEntityType(infoTypeFilters, entityType);

        this.view = gridViewFactory.create(this, listStore, folderContentsRpcProxy);

        // Wire up Column Model events
        DiskResourceColumnModel cm = this.view.getColumnModel();
        cm.addDiskResourceNameSelectedEventHandler(this);
        cm.addManageSharingSelectedEventHandler(this);
        cm.addManageMetadataSelectedEventHandler(this);
        cm.addShareByDataLinkSelectedEventHandler(this);
        cm.addManageFavoritesEventHandler(this);
        cm.addManageCommentsSelectedEventHandler(this);
        cm.addDiskResourcePathSelectedEventHandler(this);

        // Fetch Details
        this.view.addDiskResourceSelectionChangedEventHandler(this);

    }

    @Override
    public HandlerRegistration addStoreUpdateHandler(StoreUpdateEvent.StoreUpdateHandler<DiskResource> handler) {
        return listStore.addStoreUpdateHandler(handler);
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
    public void doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent event) {
        doFolderSelected(event.getQueryTemplate());
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
    public void fireEvent(GwtEvent<?> event) {
        // This method is extended by StoreUpdateHandler, so we must implement
        throw new UnsupportedOperationException();
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
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        // Fetch details

        final List<DiskResource> selection = event.getSelection();
        if(selection.size() != 1){
            // Only call get stat for single selections
            return;
        }
       fetchDetails(selection.iterator().next());
    }

    @Override
    public void onEditInfoTypeSelected(final EditInfoTypeSelected event) {
                Preconditions.checkState(event.getSelectedDiskResources().size() == 1, "Only one Disk Resource should be selected, but there are %i", getSelectedDiskResources().size());

        final InfoTypeEditorDialog dialog = new InfoTypeEditorDialog("", diskResourceService);
        dialog.show();
        dialog.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event1) {
                String newType = dialog.getSelectedValue().toString();
                setInfoType(event.getSelectedDiskResources().iterator().next(), newType);
            }
        });
    }

    private void setInfoType(final DiskResource dr, String newType) {
        diskResourceService.setFileType(dr.getPath(), newType, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable arg0) {
                ErrorHandler.post(arg0);
            }

            @Override
            public void onSuccess(String arg0) {
                // Fetching the details will update the item in the grid
                fetchDetails(dr);
            }
        });
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

    DiskResource updateDiskResource(DiskResource diskResource) {
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
        final DiskResource diskResource = event.getDiskResource();
        Preconditions.checkNotNull(diskResource);
        if (!diskResource.isFavorite()) {
            metadataService.addToFavorites(diskResource.getId(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(appearance.markFavoriteError(), caught);

                }

                @Override
                public void onSuccess(String result) {
                    updateFav(diskResource, true);
                }
            });
        } else {
            metadataService.removeFromFavorites(diskResource.getId(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(appearance.removeFavoriteError(), caught);
                }

                @Override
                public void onSuccess(String result) {
                    updateFav(diskResource, false);
                }
            });
        }
    }

    private void updateFav(final DiskResource diskResource, boolean fav) {
        if (getSelectedDiskResources().size() > 0) {
            Iterator<DiskResource> it = getSelectedDiskResources().iterator();
            if (it.hasNext()) {
                final DiskResource next = it.next();
                if (next.getId().equals(diskResource.getId())) {
                    next.setFavorite(fav);
                    updateDiskResource(next);
                }
            }
        }
    }
    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        doFolderSelected(event.getSelectedFolder());
    }

    void doFolderSelected(final Folder selectedFolder) {
        final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader = view.getGridLoader();
        gridLoader.getLastLoadConfig().setFolder(selectedFolder);
        gridLoader.getLastLoadConfig().setOffset(0);
        gridLoader.load();
    }


    @Override
    public void onManageCommentsSelected(ManageCommentsSelected event) {
        DiskResource dr = event.getDiskResource();
        // call to retrieve comments...and show dialog
        Window d = new Window();
        d.setHeadingText(appearance.comments());
        d.remove(d.getButtonBar());
        // FIXME Convert to sovereign dialog?
        d.setSize(appearance.commentsDialogWidth(), appearance.commentsDialogHeight());
        CommentsView.Presenter cp = commentsPresenterFactory.createCommentsPresenter(dr.getId(),
                                                                                     PermissionValue.own.equals(dr.getPermission()));
        cp.go(d, metadataService);
        d.show();
    }

    @Override
    public void onRequestManageMetadataSelected(ManageMetadataSelected event) {
        DiskResource selected = event.getDiskResource();

        // FIXME Convert to sovereign dialog
        final DiskResourceMetadataView mdView = new DiskResourceMetadataView(selected);
        final DiskResourceMetadataView.Presenter mdPresenter = new MetadataPresenter(selected, mdView);
        final IPlantDialog ipd = new IPlantDialog(true);

        ipd.setSize(appearance.metadataDialogWidth(), appearance.metadataDialogHeight());
        ipd.setHeadingText(appearance.metadata() + ":" + selected.getName()); //$NON-NLS-1u$
        ipd.setResizable(true);
        ipd.addHelp(new HTML(appearance.metadataHelp()));

        mdPresenter.go(ipd);

        if (diskResourceUtil.isWritable(selected)) {
            ipd.setHideOnButtonClick(false);
            ipd.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    if (mdView.shouldValidate() && !mdView.isValid()) {
                        ErrorAnnouncementConfig errNotice = new ErrorAnnouncementConfig(appearance.metadataFormInvalid());
                        announcer.schedule(errNotice);
                    } else {
                        mdPresenter.setDiskResourceMetadata(new DiskResourceMetadataUpdateCallback(ipd));
                    }
                }
            });

            ipd.addCancelButtonSelectHandler(new SelectEvent.SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    ipd.hide();
                }
            });
        }

        ipd.show();
    }

    @Override
    public void onRequestManageSharingSelected(ManageSharingSelected event) {
        DataSharingDialog dlg = dataSharingDialogFactory.createDataSharingDialog(Sets.newHashSet(event.getDiskResourceToShare()));
        dlg.show();
        dlg.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                final List<DiskResource> selection = getSelectedDiskResources();
                if (selection != null && selection.size() == 1) {
                    Iterator<DiskResource> it = selection.iterator();
                    DiskResource next = it.next();
                    fetchDetails(next);
                }
            }
        });
    }

    private void fetchDetails(final DiskResource resource) {
        diskResourceService.getStat(diskResourceUtil.asStringPathTypeMap(Arrays.asList(resource),
                                                                         resource instanceof File ? TYPE.FILE
                                                                             : TYPE.FOLDER),
                                    new AsyncCallback<FastMap<DiskResource>>() {
                                        @Override
                                        public void onFailure(Throwable caught) {
                                            ErrorHandler.post(appearance.retrieveStatFailed(), caught);
                                            // This unmasks the sendTo.. toolbar buttons
                                            //        presenter.unmaskVizMenuOptions();
                                        }

                                        @Override
                                        public void onSuccess(FastMap<DiskResource> drMap) {
                                            /* FIXME Fire global event to update diskresource
                                             * The toolbarview will need to listen
                                             * The gridviewpresenter will need to listen
                                             *    -- Fire another event from gridview
                                             */
                                            Preconditions.checkNotNull(drMap.get(resource.getPath()), "This object cannot be null at this point.");
                                            updateDiskResource(resource);

                                            //        presenter.getView().unmaskDetailsPanel();
                                            //        presenter.unmaskVizMenuOptions();
                                        }
                                    });
        // Need to mask the SendTo... options from the toolbar
//        view.maskSendToCoGe();
//        view.maskSendToEnsembl();
//        view.maskSendToTreeViewer();

    }

    @Override
    public void onRequestShareByDataLinkSelected(ShareByDataLinkSelected event) {
        DiskResource toBeShared = event.getDiskResourceToShare();
        if (toBeShared instanceof Folder) {
            showShareLink(GWT.getHostPageBaseURL() + "?type=data&folder=" + toBeShared.getPath());
        } else {
            diskResourceService.createDataLinks(Arrays.asList(toBeShared.getPath()), new CreateDataLinksCallback());
        }
    }
     private void showShareLink(String linkId) {
         // FIXME Fold into separate view/dlg
        // Open dialog window with text selected.
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeadingText(appearance.copy());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
         dlg.setSize(appearance.shareLinkDialogWidth(), appearance.shareLinkDialogHeight());
        TextField textBox = new TextField();
         textBox.setWidth(appearance.shareLinkDialogTextBoxWidth());
        textBox.setReadOnly(true);
        textBox.setValue(linkId);
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        dlg.setWidget(container);
        container.add(textBox);
        container.add(new Label(appearance.copyPasteInstructions()));
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
    }

}
