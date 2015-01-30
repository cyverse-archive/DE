package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.diskResources.TYPE;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.search.SearchAutoBeanFactory;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.models.tags.IplantTagAutoBeanFactory;
import org.iplantc.de.client.models.tags.IplantTagList;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.services.DiskResourceServiceFacade;
import org.iplantc.de.client.services.MetadataServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.PathListWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.diskResource.client.events.*;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkPanelFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.GridViewPresenterFactory;
import org.iplantc.de.diskResource.client.presenters.callbacks.CreateFolderCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceDeleteCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMoveCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceRestoreCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.GetDiskResourceDetailsCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.RenameDiskResourceCallback;
import org.iplantc.de.diskResource.client.presenters.handlers.DiskResourcesEventHandler;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.dialogs.CreateFolderDialog;
import org.iplantc.de.diskResource.client.views.dialogs.FolderSelectDialog;
import org.iplantc.de.diskResource.client.views.dialogs.InfoTypeEditorDialog;
import org.iplantc.de.diskResource.client.views.dialogs.RenameFileDialog;
import org.iplantc.de.diskResource.client.views.dialogs.RenameFolderDialog;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.messages.IplantContextualHelpStrings;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author jstroot
 */
public class DiskResourcePresenterImpl implements DiskResourceView.Presenter,
                                                  DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler,
                                                  RootFoldersRetrievedEvent.RootFoldersRetrievedEventHandler {

    @Inject IplantErrorStrings errorStrings;
    @Inject IplantContextualHelpStrings helpStrings;
    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject MetadataServiceFacade fsmdataService;
    @Inject UserInfo userInfo;
    @Inject DiskResourceSelectorDialogFactory selectorDialogFactory;
    @Inject DataLinkPanelFactory dataLinkPanelFactory;
    @Inject DiskResourceUtil diskResourceUtil;

    final IplantAnnouncer announcer;
    final DiskResourceView view;
    final DiskResourceAutoBeanFactory drFactory;
    final List<HandlerRegistration> dreventHandlers = new ArrayList<>();

    private final NavigationView.Presenter navigationPresenter;
    private final GridView.Presenter gridViewPresenter;
    private final IplantDisplayStrings displayStrings;
    private final EventBus eventBus;
    private DataSearchPresenter dataSearchPresenter;

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final DiskResourceAutoBeanFactory drFactory,
                              final NavigationView.Presenter navigationPresenter,
                              final GridViewPresenterFactory gridViewPresenterFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final IplantAnnouncer announcer,
                              final EventBus eventBus,
                              @Assisted("hideToolbar") final boolean hideToolbar,
                              @Assisted("hideDetailsPanel") final boolean hideDetailsPanel,
                              @Assisted("singleSelect") final boolean singleSelect,
                              @Assisted("disableFilePreview") final boolean disableFilePreview,
                              @Assisted final HasPath folderToSelect,
                              @Assisted final List<InfoType> infoTypeFilters,
                              @Assisted final TYPE entityType,
                              @Assisted final IsWidget southWidget) {
        this(diskResourceViewFactory,
             drFactory, navigationPresenter, gridViewPresenterFactory, dataSearchPresenter,
             displayStrings, announcer, eventBus,
             infoTypeFilters, entityType);
        view.setNorthWidgetHidden(hideToolbar);
        view.setEastWidgetHidden(hideDetailsPanel);
        if(singleSelect) {
            gridViewPresenter.getView().setSingleSelect();
        }
        if(disableFilePreview) {
            disableFilePreview();
        }
        navigationPresenter.setSelectedFolder(folderToSelect);
        view.setSouthWidget(southWidget);
    }

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final DiskResourceAutoBeanFactory drFactory,
                              final NavigationView.Presenter navigationPresenter,
                              final GridViewPresenterFactory gridViewPresenterFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final IplantAnnouncer announcer,
                              final EventBus eventBus,
                              @Assisted("hideToolbar") final boolean hideToolbar,
                              @Assisted("hideDetailsPanel") final boolean hideDetailsPanel,
                              @Assisted("singleSelect") final boolean singleSelect,
                              @Assisted("disableFilePreview") final boolean disableFilePreview,
                              @Assisted final HasPath folderToSelect,
                              @Assisted final IsWidget southWidget,
                              @Assisted final int southWidgetHeight) {
        this(diskResourceViewFactory,
             drFactory, navigationPresenter, gridViewPresenterFactory, dataSearchPresenter,
             displayStrings, announcer, eventBus,
             Collections.<InfoType>emptyList(),
             null);
        view.setNorthWidgetHidden(hideToolbar);
        view.setEastWidgetHidden(hideDetailsPanel);
        if(singleSelect) {
            gridViewPresenter.getView().setSingleSelect();
        }
        if(disableFilePreview) {
            disableFilePreview();
        }
        navigationPresenter.setSelectedFolder(folderToSelect);
        view.setSouthWidget(southWidget, southWidgetHeight);
    }

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final DiskResourceAutoBeanFactory drFactory,
                              final NavigationView.Presenter navigationPresenter,
                              final GridViewPresenterFactory gridViewPresenterFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final IplantAnnouncer announcer,
                              final EventBus eventBus,
                              @Assisted("hideToolbar") final boolean hideToolbar,
                              @Assisted("hideDetailsPanel") final boolean hideDetailsPanel,
                              @Assisted("singleSelect") final boolean singleSelect,
                              @Assisted("disableFilePreview") final boolean disableFilePreview,
                              @Assisted final HasPath folderToSelect,
                              @Assisted final List<HasId> selectedResources) {
        this(diskResourceViewFactory, drFactory,
             navigationPresenter, gridViewPresenterFactory, dataSearchPresenter,
             displayStrings, announcer, eventBus,
             Collections.<InfoType>emptyList(),
             null);
        view.setNorthWidgetHidden(hideToolbar);
        view.setEastWidgetHidden(hideDetailsPanel);
        if(singleSelect) {
            gridViewPresenter.getView().setSingleSelect();
        }
        if(disableFilePreview) {
            disableFilePreview();
        }
        navigationPresenter.setSelectedFolder(folderToSelect);
        setSelectedDiskResourcesById(selectedResources);
    }

    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final DiskResourceAutoBeanFactory drFactory,
                              final NavigationView.Presenter navigationPresenter,
                              final GridViewPresenterFactory gridViewPresenterFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final IplantAnnouncer announcer,
                              final EventBus eventBus,
                              final List<InfoType> infoTypeFilters,
                              final TYPE entityType) {
        this.drFactory = drFactory;
        this.navigationPresenter = navigationPresenter;
        this.gridViewPresenter = gridViewPresenterFactory.create(navigationPresenter,
                                                                 infoTypeFilters,
                                                                 entityType);
        this.displayStrings = displayStrings;
        this.announcer = announcer;
        this.eventBus = eventBus;
        this.dataSearchPresenter = dataSearchPresenter;
        this.view = diskResourceViewFactory.create(this, navigationPresenter, gridViewPresenter);

        this.navigationPresenter.setParentPresenter(this);
        this.gridViewPresenter.setParentPresenter(this);
        this.navigationPresenter.setMaskable(view);


        this.view.getToolbar().addShareByDataLinkEventHandler(this.gridViewPresenter);
        this.view.addManageSharingEventHandler(this.gridViewPresenter);
        this.view.getToolbar().addManageSharingEventHandler(this.gridViewPresenter);
        this.view.getToolbar().addManageMetadataEventHandler(this.gridViewPresenter);
        this.view.getToolbar().addManageCommentsEventHandler(this.gridViewPresenter);


        DiskResourceSearchField searchField = this.view.getToolbar().getSearchField();
        searchField.addSaveDiskResourceQueryClickedEventHandler(this.dataSearchPresenter);
        searchField.addSubmitDiskResourceQueryEventHandler(this.gridViewPresenter);

        this.gridViewPresenter.getView().addBeforeLoadHandler(this.navigationPresenter);
        this.gridViewPresenter.getView().addDiskResourceNameSelectedEventHandler(this.navigationPresenter);
        this.gridViewPresenter.getView().addDiskResourcePathSelectedEventHandler(this.navigationPresenter);
        this.gridViewPresenter.getView().addDiskResourceSelectionChangedEventHandler(this);
        this.gridViewPresenter.getView().addDiskResourceSelectionChangedEventHandler(this.view);

        this.navigationPresenter.addSavedSearchedRetrievedEventHandler(this.dataSearchPresenter);
        this.navigationPresenter.addSubmitDiskResourceQueryEventHandler(this.gridViewPresenter);
        this.navigationPresenter.addRootFoldersRetrievedEventHandler(this);
        this.navigationPresenter.getView().addFolderSelectedEventHandler(this.gridViewPresenter);
        this.navigationPresenter.getView().addFolderSelectedEventHandler(this.gridViewPresenter.getView());
        this.navigationPresenter.getView().addFolderSelectedEventHandler(searchField);
        this.navigationPresenter.getView().addDeleteSavedSearchClickedEventHandler(this.dataSearchPresenter);


        this.dataSearchPresenter.addUpdateSavedSearchesEventHandler(this.navigationPresenter);
        this.dataSearchPresenter.addSavedSearchDeletedEventHandler(searchField);


        // Wire up global event handlers
        DiskResourcesEventHandler diskResourcesEventHandler = new DiskResourcesEventHandler(navigationPresenter);
        dreventHandlers.add(eventBus.addHandler(FolderRefreshEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(DiskResourcesDeletedEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(FolderCreatedEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(DiskResourceRenamedEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(DiskResourcesMovedEvent.TYPE, diskResourcesEventHandler));
    }

    @Override
    public HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler handler) {
        return gridViewPresenter.getView().addDiskResourceSelectionChangedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addFolderSelectedEventHandler(FolderSelectionEvent.FolderSelectionEventHandler handler) {
        return navigationPresenter.getView().addFolderSelectedEventHandler(handler);
    }

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        final List<DiskResource> selection = event.getSelection();
        if (selection != null && selection.size() == 1) {
            Iterator<DiskResource> it = selection.iterator();
            DiskResource next = it.next();
            if (!next.isStatLoaded()) {
                getDetails(next);
            } else {
                view.updateDetails(next);
            }
        } else {
            view.resetDetailsPanel();
        }
    }

    @Override
    public void onFavoriteRequest(RequestDiskResourceFavoriteEvent event) {
        final DiskResource diskResource = event.getDiskResource();
        checkNotNull(diskResource);
        if (!diskResource.isFavorite()) {
            fsmdataService.addToFavorites(diskResource.getId(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(errorStrings.markFavoriteError(), caught);

                }

                @Override
                public void onSuccess(String result) {
                    updateFav(diskResource, true);
                }
            });
        } else {
            fsmdataService.removeFromFavorites(diskResource.getId(), new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(errorStrings.removeFavoriteError(), caught);

                }

                @Override
                public void onSuccess(String result) {
                    updateFav(diskResource, false);

                }
            });
        }
    }

    @Override
    public Folder convertToFolder(DiskResource selectedItem) {
        return diskResourceService.convertToFolder(selectedItem);
    }

    @Override
    public void onRootFoldersRetrieved(RootFoldersRetrievedEvent event) {
        DiskResourceFavorite diskResourceFavorite = drFactory.getFavortieFolder().as();
        String id = userInfo.getHomePath() + FAVORITES_FOLDER_PATH;
        diskResourceFavorite.setId(id);
        diskResourceFavorite.setPath(id);
        diskResourceFavorite.setName(FAVORITES_FOLDER_NAME);
        navigationPresenter.addFolder(diskResourceFavorite);
    }

    @Override
    public void cleanUp() {
        for (HandlerRegistration hr : dreventHandlers) {
            eventBus.removeHandler(hr);
        }
    }

    @Override
    public DiskResourceView getView() {
        return view;
    }

    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
        // JDS Re-select currently selected folder in order to load center
        // panel.
        navigationPresenter.setSelectedFolder(navigationPresenter.getSelectedFolder());
    }

    @Override
    public void disableFilePreview() {
        gridViewPresenter.setFilePreviewEnabled(false);
    }

    @Override
    public void go(HasOneWidget container, HasPath folderToSelect, final List<? extends HasId> diskResourcesToSelect) {

        if ((folderToSelect == null) || Strings.isNullOrEmpty(folderToSelect.getPath())) {
            go(container);
        } else {
            container.setWidget(view);
            navigationPresenter.setSelectedFolder(folderToSelect);
            setSelectedDiskResourcesById(diskResourcesToSelect);
        }
    }

    @Override
    public void setSelectedDiskResourcesById(final List<? extends HasId> diskResourcesToSelect) {
        gridViewPresenter.setSelectedDiskResourcesById(diskResourcesToSelect);
    }

    @Override
    public void setSelectedFolderByPath(final HasPath folderToSelect) {
        navigationPresenter.setSelectedFolder(folderToSelect);
    }

    @Override
    public Folder getSelectedFolder() {
        return navigationPresenter.getSelectedFolder();
    }

    @Override
    public List<DiskResource> getSelectedDiskResources() {
        return gridViewPresenter.getSelectedDiskResources();
    }

    @Override
    public void getDetails(DiskResource path) {
        // FIXME Migrate to Details presenter
        
        diskResourceService.getStat(diskResourceUtil.asStringPathTypeMap(Arrays.asList(path), path instanceof File ? TYPE.FILE
                                                                                             : TYPE.FOLDER),
                                    new GetDiskResourceDetailsCallback(this, path.getPath(), drFactory));
        view.maskSendToCoGe();
        view.maskSendToEnsembl();
        view.maskSendToTreeViewer();
    }

    @Override
    public void getTagsForSelectedResource() {
        if (getSelectedDiskResources().size() > 0) {
            Iterator<DiskResource> it = getSelectedDiskResources().iterator();
            if (it.hasNext()) {
                final DiskResource next = it.next();
                fsmdataService.getTags(next.getId(), new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post("Unable to retrieve tags!", caught);
                    }

                    @Override
                    public void onSuccess(String result) {
                        IplantTagAutoBeanFactory factory = GWT.create(IplantTagAutoBeanFactory.class);
                        AutoBean<IplantTagList> tagList = AutoBeanCodex.decode(factory, IplantTagList.class, result);
                        view.updateTags(tagList.as().getTagList());

                    }
                });
            }
        }
    }

    @Override
    public void doBulkUpload() {
        eventBus.fireEvent(new RequestBulkUploadEvent(this, navigationPresenter.getSelectedUploadFolder()));
    }

    @Override
    public void doSimpleUpload() {
        eventBus.fireEvent(new RequestSimpleUploadEvent(this, navigationPresenter.getSelectedUploadFolder()));
    }

    @Override
    public void editSelectedFile() {
        checkState(getSelectedDiskResources().size() == 1, "Only one file should be selected, but there are %i", getSelectedDiskResources().size());
        final DiskResource next = getSelectedDiskResources().iterator().next();
        checkState(next instanceof File, "Selected item should be a file, but is not.");
        checkState(PermissionValue.own.equals(next.getPermission())
                          || PermissionValue.write.equals(next.getPermission()), "User should have either own or write permissions for the selected item");

        eventBus.fireEvent(new ShowFilePreviewEvent((File) next, this));
    }

    @Override
    public void editSelectedResourceInfoType() {
        checkState(getSelectedDiskResources().size() == 1, "Only one Disk Resource should be selected, but there are %i", getSelectedDiskResources().size());
        onInfoTypeClick(getSelectedDiskResources().iterator().next(), "");
    }

    @Override
    public void doImportFromUrl() {
        eventBus.fireEvent(new RequestImportFromUrlEvent(this, navigationPresenter.getSelectedUploadFolder()));
    }

    @Override
    public void openNewWindow(boolean atThisLocation) {
        // If current folder is null, or window SHOULD NOT be opened at current location, folderPath is null
        String folderPath = (navigationPresenter.getSelectedFolder() == null) || !atThisLocation ? null : navigationPresenter.getSelectedFolder().getPath();
        OpenFolderEvent openEvent = new OpenFolderEvent(folderPath);
        openEvent.requestNewView(true);
        eventBus.fireEvent(openEvent);
    }

    @Override
    public void refreshSelectedFolder() {
        checkState(navigationPresenter.getSelectedFolder() != null, "Selected folder should no be null");
        navigationPresenter.refreshFolder(navigationPresenter.getSelectedFolder());
    }

    @Override
    public void renameSelectedResource() {
        if (!getSelectedDiskResources().isEmpty() && (getSelectedDiskResources().size() == 1)) {
            DiskResource dr = getSelectedDiskResources().iterator().next();
            if (dr instanceof File) {
                RenameFileDialog dlg = new RenameFileDialog((File)dr, this);
                dlg.show();

            } else {
                RenameFolderDialog dlg = new RenameFolderDialog((Folder)dr, this);
                dlg.show();

            }
        } else if (navigationPresenter.getSelectedFolder() != null) {
            RenameFolderDialog dlg = new RenameFolderDialog(navigationPresenter.getSelectedFolder(), this);
            dlg.show();
        }
    }

    @Override
    public void restoreSelectedResources() {
        final List<DiskResource> selectedResources = getSelectedDiskResources();

        if (selectedResources == null || selectedResources.isEmpty()) {
            return;
        }

        mask(""); //$NON-NLS-1$

        DiskResourceRestoreCallback callback = new DiskResourceRestoreCallback(navigationPresenter, this, drFactory, selectedResources);
        if (gridViewPresenter.isSelectAllChecked()) {
            diskResourceService.restoreAll(callback);
        } else {
            HasPaths request = drFactory.pathsList().as();
            request.setPaths(diskResourceUtil.asStringPathList(selectedResources));
            diskResourceService.restoreDiskResource(request, callback);
        }
    }

    @Override
    public void selectTrashFolder() {
        final HasPath hasPath = CommonModelUtils.getInstance().createHasPathFromString(userInfo.getTrashPath());
        navigationPresenter.setSelectedFolder(hasPath);
    }

    @Override
    public void sendSelectedResourceToEnsembl() {
        final List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
        Iterator<DiskResource> it = selection.iterator();
        DiskResource next = it.next();
        String infoType = getInfoType(next);
        if(Strings.isNullOrEmpty(infoType)) {
            showInfoTypeError(errorStrings.unsupportedEnsemblInfoType());
            return;
        }
        if (diskResourceUtil.isEnsemblVizTab(diskResourceUtil.createInfoTypeSplittable(infoType))) {
            eventBus.fireEvent(new RequestSendToEnsemblEvent((File)next, InfoType.fromTypeString(infoType)));
        } else {
            showInfoTypeError(errorStrings.unsupportedEnsemblInfoType());
        }
    }

    @Override
    public void sendSelectedResourcesToCoge() {
        final List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
        Iterator<DiskResource> it = selection.iterator();
        DiskResource next = it.next();
        String infoType = getInfoType(next);
        if (Strings.isNullOrEmpty(infoType)) {
            showInfoTypeError(errorStrings.unsupportedCogeInfoType());
            return;
        }
        if (diskResourceUtil.isGenomeVizTab(diskResourceUtil.createInfoTypeSplittable(infoType))) {
            eventBus.fireEvent(new RequestSendToCoGeEvent((File)next));
        } else {
            showInfoTypeError(errorStrings.unsupportedCogeInfoType());
        }
    }

    @Override
    public void sendSelectedResourcesToTreeViewer() {
        final List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
        Iterator<DiskResource> it = selection.iterator();
        DiskResource next = it.next();
        String infoType = getInfoType(next);
        if (Strings.isNullOrEmpty(infoType)) {
            showInfoTypeError(errorStrings.unsupportedTreeInfoType());
            return;
        }
        if (diskResourceUtil.isTreeTab(diskResourceUtil.createInfoTypeSplittable(infoType))) {
            eventBus.fireEvent(new RequestSendToTreeViewerEvent((File)next));
        } else {
            showInfoTypeError(errorStrings.unsupportedTreeInfoType());
        }
    }

    private void showInfoTypeError(String msg) {
        SafeHtmlBuilder builder = new SafeHtmlBuilder();
        builder.appendEscaped(msg);
        announcer.schedule(new ErrorAnnouncementConfig(builder.toSafeHtml(), true, 4000));
    }


    private String getInfoType(DiskResource dr) {
        File diskResourceInfo = ((File)dr);
        if (diskResourceInfo == null) {
            ErrorHandler.post("Unable to retrieve information type");
            return null;
        }
        return diskResourceInfo.getInfoType();
    }

    @Override
    public void setViewDebugId(String baseID) {
        view.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.DISK_RESOURCE_VIEW);
    }

    @Override
    public void createNewFolder() {
        CreateFolderDialog dlg = new CreateFolderDialog(navigationPresenter.getSelectedUploadFolder(), this);
        dlg.show();
    }

    @Override
    public void createNewPlainTextFile(FileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewRFile(FileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewPerlFile(FileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewPythonFile(FileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewShellScript(FileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewMdFile(FileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void createNewTabFile(TabularFileViewerWindowConfig config) {
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewPathListFileClicked(PathListWindowConfig config){
        config.setParentFolder(navigationPresenter.getSelectedUploadFolder());
        eventBus.fireEvent(new CreateNewFileEvent(config));
    }
    @Override
    public void doCreateNewFolder(Folder parentFolder, final String newFolderName) {
        view.mask(displayStrings.loadingMask());
        diskResourceService.createFolder(parentFolder, newFolderName, new CreateFolderCallback(parentFolder, view));
    }

    @Override
    public void doSimpleDownload() {
        eventBus.fireEvent(new RequestSimpleDownloadEvent(this, getSelectedDiskResources(), navigationPresenter.getSelectedFolder()));
    }

    @Override
    public void doBulkDownload() {
        eventBus.fireEvent(new RequestBulkDownloadEvent(this, gridViewPresenter.isSelectAllChecked(), getSelectedDiskResources(), navigationPresenter.getSelectedFolder()));
    }

    @Override
    public void doRenameDiskResource(final DiskResource dr, final String newName) {
        if (dr != null && !dr.getName().equals(newName)) {
            view.mask(displayStrings.loadingMask());
            diskResourceService.renameDiskResource(dr, newName, new RenameDiskResourceCallback(dr, view));
        }
    }

    @Override
    public void deleteSelectedResources() {
        List<DiskResource> selectedResources = getSelectedDiskResources();
        if (!selectedResources.isEmpty() && diskResourceUtil.isOwner(selectedResources)) {

            if (diskResourceUtil.containsTrashedResource(selectedResources)) {
                confirmDelete(selectedResources);
            } else {
                delete(selectedResources, displayStrings.deleteMsg());
            }
        }
    }

    private void confirmDelete(final List<DiskResource> drSet) {
        final MessageBox confirm = new ConfirmMessageBox(displayStrings.warning(), displayStrings.emptyTrashWarning());

        confirm.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(PredefinedButton.YES.equals(event.getHideButton())){
                    delete(drSet, displayStrings.deleteTrash());
                }
            }
        });

        confirm.show();
    }

    private void delete(List<DiskResource> drSet, String announce) {
        view.mask(displayStrings.loadingMask());
        Folder selectedFolder = navigationPresenter.getSelectedFolder();
        final AsyncCallback<HasPaths> callback = new DiskResourceDeleteCallback(drSet, selectedFolder, view, announce);

        if (gridViewPresenter.isSelectAllChecked() && selectedFolder != null) {
            diskResourceService.deleteContents(selectedFolder.getPath(), callback);

        } else {
            diskResourceService.deleteDiskResources(drSet, callback);
        }
    }

    @Override
    public boolean canDragDataToTargetFolder(final Folder targetFolder, final Collection<DiskResource> dropData) {
        if (targetFolder instanceof DiskResourceQueryTemplate) {
            return false;
        }

        if (targetFolder.isFilter()) {
            return false;
        }

        // Assuming that ownership is of no concern.
        for (DiskResource dr : dropData) {
            // if the resource is a direct child of target folder
            if (diskResourceUtil.isChildOfFolder(targetFolder, dr)) {
                return false;
            }

            if (dr instanceof Folder) {
                if (targetFolder.getPath().equals(dr.getPath())) {
                    return false;
                }

                // cannot drag an ancestor (parent, grandparent, etc) onto a
                // child and/or descendant
                if (diskResourceUtil.isDescendantOfFolder((Folder)dr, targetFolder)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void doMoveDiskResources(Folder targetFolder, List<DiskResource> resources) {
        Folder parent = navigationPresenter.getSelectedFolder();
        view.mask(displayStrings.loadingMask());
        if (gridViewPresenter.isSelectAllChecked()) {
            diskResourceService.moveContents(parent.getPath(), targetFolder, new DiskResourceMoveCallback(view, true, parent, targetFolder, resources));
        } else {
            diskResourceService.moveDiskResources(resources, targetFolder, new DiskResourceMoveCallback(view, false, parent, targetFolder, resources));
        }
    }

    @Override
    public void deSelectDiskResources() {
        gridViewPresenter.deSelectDiskResources();
    }

    void doEmptyTrash() {
        view.mask(displayStrings.loadingMask());
        diskResourceService.emptyTrash(userInfo.getUsername(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                navigationPresenter.refreshFolder(navigationPresenter.getFolderByPath(userInfo.getTrashPath()));
                view.unmask();
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmask();
            }
        });
    }

    @Override
    public void emptyTrash() {
        // TODO REFACTOR CORE-5300 Move confirmation box to view, which will call presenter
        final ConfirmMessageBox cmb = new ConfirmMessageBox(displayStrings.emptyTrash(), displayStrings.emptyTrashWarning());
        cmb.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(PredefinedButton.YES.equals(event.getHideButton())){
                    doEmptyTrash();
                }
            }
        });
        cmb.setWidth(300);
        cmb.show();
    }

    @Override
    public void manageSelectedResourceDataLinks() {
        IPlantDialog dlg = new IPlantDialog(true);
        dlg.setPredefinedButtons(PredefinedButton.OK);
        dlg.setHeadingText(displayStrings.manageDataLinks());
        dlg.setHideOnButtonClick(true);
        dlg.setWidth(550);
        dlg.setOkButtonText(displayStrings.done());
        DataLinkPanel.Presenter dlPresenter = dataLinkPanelFactory.createDataLinkPresenter(Lists.newArrayList(getSelectedDiskResources()));
        dlPresenter.go(dlg);
        dlg.addHelp(new HTML(helpStrings.manageDataLinksHelp()));
        dlg.show();
    }

    @Override
    public void onInfoTypeClick(final DiskResource dr, final String type) {
        final InfoTypeEditorDialog dialog = new InfoTypeEditorDialog(type, diskResourceService);
        dialog.show();
        dialog.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                String newType = dialog.getSelectedValue().toString();
                setInfoType(dr, newType);
            }
        });

    }

    @Override
    public void moveSelectedDiskResources() {
        final FolderSelectDialog fsd = selectorDialogFactory.createFolderSelector(navigationPresenter.getSelectedFolder());
        fsd.show();
        fsd.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                Folder targetFolder = fsd.getValue();
                final List<DiskResource> selectedResources = getSelectedDiskResources();
                if (diskResourceUtil.isMovable(targetFolder, selectedResources)) {
                    if (canDragDataToTargetFolder(targetFolder, selectedResources)) {
                        doMoveDiskResources(targetFolder, selectedResources);
                    } else {
                        announcer.schedule(new ErrorAnnouncementConfig(errorStrings.diskResourceIncompleteMove()));
                        view.unmask();
                    }
                } else {
                    announcer.schedule(new ErrorAnnouncementConfig(errorStrings.permissionErrorMessage()));
                    view.unmask();
                }
            }
        });

    }

    @Override
    public void moveSelectedDiskResourcesToTrash() {
        checkState(!getSelectedDiskResources().isEmpty(), "Selected resources should not be empty");
        delete(getSelectedDiskResources(), displayStrings.deleteMsg());
    }

    @Override
    public void mask(String loadingMask) {
        view.mask((Strings.isNullOrEmpty(loadingMask)) ? displayStrings.loadingMask() : loadingMask);
    }

    @Override
    public void unmask() {
        view.unmask();
    }

    @Override
    public void resetInfoType() {
        if (getSelectedDiskResources().size() > 0) {
            Iterator<DiskResource> it = getSelectedDiskResources().iterator();
            if (it.hasNext()) {
                setInfoType(it.next(), "");
            }
        }
    }

    private void setInfoType(final DiskResource dr, String newType) {
        diskResourceService.setFileType(dr.getPath(), newType, new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable arg0) {
                ErrorHandler.post(arg0);
            }

            @Override
            public void onSuccess(String arg0) {
                getDetails(dr);
            }
        });
    }

    @Override
    public void displayAndCacheDiskResourceInfo(DiskResource info) {
        Preconditions.checkNotNull(info, "This object cannot be null at this point.");
        DiskResource updatedModel = gridViewPresenter.updateDiskResource(info);
        view.updateDetails(updatedModel);
    }

    @Override
    public void unmaskVizMenuOptions() {
        view.unmaskSendToCoGe();
        view.unmaskSendToEnsembl();
        view.unmaskSendToTreeViewer();
    }

    @Override
    public void attachTag(final IplantTag tag) {
        if (getSelectedDiskResources().size() > 0) {
            Iterator<DiskResource> it = getSelectedDiskResources().iterator();
            if (it.hasNext()) {
                final DiskResource next = it.next();
                fsmdataService.attachTags(Arrays.asList(tag.getId()),
                                          next.getId(),
                                          new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(errorStrings.tagAttachError(), caught);

                    }

                    @Override
                    public void onSuccess(String result) {
                        announcer.schedule(new SuccessAnnouncementConfig(displayStrings.tagAttached(next.getName(), tag.getValue())));
                    }
                });
            }
        }

    }

    @Override
    public void detachTag(final IplantTag tag) {
        if (getSelectedDiskResources().size() > 0) {
            Iterator<DiskResource> it = getSelectedDiskResources().iterator();
            if (it.hasNext()) {
                final DiskResource next = it.next();
                fsmdataService.detachTags(Arrays.asList(tag.getId()),
                                          next.getId(),
                                          new AsyncCallback<String>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        ErrorHandler.post(errorStrings.tagDetachError(), caught);
                    }

                    @Override
                    public void onSuccess(String result) {
                        announcer.schedule(new SuccessAnnouncementConfig(displayStrings.tagDetached(tag.getValue(), next.getName())));
                    }
                });
            }
        }

    }

    private void updateFav(final DiskResource diskResource, boolean fav) {
        if (getSelectedDiskResources().size() > 0) {
            Iterator<DiskResource> it = getSelectedDiskResources().iterator();
            if (it.hasNext()) {
                final DiskResource next = it.next();
                if (next.getId().equals(diskResource.getId())) {
                    next.setFavorite(fav);
                    gridViewPresenter.updateDiskResource(next);
                }
            }

        }
    }

    @Override
    public void doSearchTaggedWithResources(Set<IplantTag> tags) {
        final SearchAutoBeanFactory factory = GWT.create(SearchAutoBeanFactory.class);
        DiskResourceQueryTemplate qt = factory.dataSearchFilter().as();
        qt.setTagQuery(tags);
        navigationPresenter.setSelectedFolder(qt);
    }

}
