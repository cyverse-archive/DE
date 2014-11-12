package org.iplantc.de.diskResource.client.presenters;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.events.diskResources.OpenFolderEvent;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.HasPaths;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.dataLink.DataLinkFactory;
import org.iplantc.de.client.models.dataLink.DataLinkList;
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
import org.iplantc.de.commons.client.comments.presenter.CommentsPresenter;
import org.iplantc.de.commons.client.comments.view.CommentsView;
import org.iplantc.de.commons.client.comments.view.CommentsViewImpl;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.PathListWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.TabularFileViewerWindowConfig;
import org.iplantc.de.diskResource.client.dataLink.view.DataLinkPanel;
import org.iplantc.de.diskResource.client.events.*;
import org.iplantc.de.diskResource.client.gin.factory.DataLinkPanelFactory;
import org.iplantc.de.diskResource.client.gin.factory.DataSharingDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceSelectorDialogFactory;
import org.iplantc.de.diskResource.client.gin.factory.DiskResourceViewFactory;
import org.iplantc.de.diskResource.client.gin.factory.FolderRpcProxyFactory;
import org.iplantc.de.diskResource.client.metadata.presenter.DiskResourceMetadataUpdateCallback;
import org.iplantc.de.diskResource.client.metadata.presenter.MetadataPresenter;
import org.iplantc.de.diskResource.client.metadata.view.DiskResourceMetadataView;
import org.iplantc.de.diskResource.client.presenters.callbacks.CreateFolderCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceDeleteCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceMoveCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.DiskResourceRestoreCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.GetDiskResourceDetailsCallback;
import org.iplantc.de.diskResource.client.presenters.callbacks.RenameDiskResourceCallback;
import org.iplantc.de.diskResource.client.presenters.handlers.CachedFolderTreeStoreBinding;
import org.iplantc.de.diskResource.client.presenters.handlers.DiskResourcesEventHandler;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectDiskResourceByIdStoreAddHandler;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByPathLoadHandler;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.presenter.DataSearchPresenter;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.sharing.views.DataSharingDialog;
import org.iplantc.de.diskResource.client.views.DiskResourceView;
import org.iplantc.de.diskResource.client.views.cells.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageCommentsEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageMetadataEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ManageSharingEvent;
import org.iplantc.de.diskResource.client.views.cells.events.RequestDiskResourceFavoriteEvent;
import org.iplantc.de.diskResource.client.views.cells.events.ShareByDataLinkEvent;
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
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author jstroot
 */
public class DiskResourcePresenterImpl implements DiskResourceView.Presenter,
                                                  DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler,
                                                  UpdateSavedSearchesEvent.UpdateSavedSearchesHandler,
                                                  RootFoldersRetrievedEvent.RootFoldersRetrievedEventHandler {

    @Inject IplantErrorStrings errorStrings;
    @Inject IplantContextualHelpStrings helpStrings;
    @Inject DataLinkFactory dlFactory;
    @Inject DiskResourceServiceFacade diskResourceService;
    @Inject MetadataServiceFacade fsmdataService;
    @Inject IplantAnnouncer announcer;
    @Inject UserInfo userInfo;
    @Inject DiskResourceSelectorDialogFactory selectorDialogFactory;
    @Inject DataLinkPanelFactory dataLinkPanelFactory;
    @Inject DataSharingDialogFactory dataSharingDialogFactory;

    final DiskResourceView view;
    final DiskResourceAutoBeanFactory drFactory;
    final TreeLoader<Folder> treeLoader;
    final List<HandlerRegistration> dreventHandlers = new ArrayList<>();

    private final HashMap<EventHandler, HandlerRegistration> registeredHandlers = new HashMap<>();
    protected boolean isFilePreviewEnabled = true;
    private final IplantDisplayStrings displayStrings;
    private final EventBus eventBus;
    private DataSearchPresenter dataSearchPresenter;

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final FolderRpcProxyFactory folderRpcProxyFactory,
                              final DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy,
                              final DiskResourceAutoBeanFactory drFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final EventBus eventBus,
                              @Assisted("hideToolbar") boolean hideToolbar,
                              @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                              @Assisted("singleSelect") boolean singleSelect,
                              @Assisted("disableFilePreview") boolean disableFilePreview,
                              @Assisted HasPath folderToSelect,
                              @Assisted IsWidget southWidget) {
        this(diskResourceViewFactory, folderRpcProxyFactory, folderContentsRpcProxy,
             drFactory, dataSearchPresenter, displayStrings, eventBus);
        view.setNorthWidgetHidden(hideToolbar);
        view.setEastWidgetHidden(hideDetailsPanel);
        if(singleSelect) {
            view.setSingleSelect();
        }
        if(disableFilePreview) {
            disableFilePreview();
        }
        setSelectedFolderByPath(folderToSelect);
        view.setSouthWidget(southWidget);
    }

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final FolderRpcProxyFactory folderRpcProxyFactory,
                              final DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy,
                              final DiskResourceAutoBeanFactory drFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final EventBus eventBus,
                              @Assisted("hideToolbar") boolean hideToolbar,
                              @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                              @Assisted("singleSelect") boolean singleSelect,
                              @Assisted("disableFilePreview") boolean disableFilePreview,
                              @Assisted HasPath folderToSelect,
                              @Assisted IsWidget southWidget,
                              @Assisted int southWidgetHeight) {
        this(diskResourceViewFactory, folderRpcProxyFactory, folderContentsRpcProxy,
             drFactory, dataSearchPresenter, displayStrings, eventBus);
        view.setNorthWidgetHidden(hideToolbar);
        view.setEastWidgetHidden(hideDetailsPanel);
        if(singleSelect) {
            view.setSingleSelect();
        }
        if(disableFilePreview) {
            disableFilePreview();
        }
        setSelectedFolderByPath(folderToSelect);
        view.setSouthWidget(southWidget, southWidgetHeight);
    }

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final FolderRpcProxyFactory folderRpcProxyFactory,
                              final DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy,
                              final DiskResourceAutoBeanFactory drFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final EventBus eventBus,
                              @Assisted("hideToolbar") boolean hideToolbar,
                              @Assisted("hideDetailsPanel") boolean hideDetailsPanel,
                              @Assisted("singleSelect") boolean singleSelect,
                              @Assisted("disableFilePreview") boolean disableFilePreview,
                              @Assisted HasPath folderToSelect,
                              @Assisted List<HasId> selectedResources) {
        this(diskResourceViewFactory, folderRpcProxyFactory, folderContentsRpcProxy,
             drFactory, dataSearchPresenter, displayStrings, eventBus);
        view.setNorthWidgetHidden(hideToolbar);
        view.setEastWidgetHidden(hideDetailsPanel);
        if(singleSelect) {
            view.setSingleSelect();
        }
        if(disableFilePreview) {
            disableFilePreview();
        }
        // FIXME needs logic
        setSelectedFolderByPath(folderToSelect);
        setSelectedDiskResourcesById(selectedResources);
    }

    @AssistedInject
    DiskResourcePresenterImpl(final DiskResourceViewFactory diskResourceViewFactory,
                              final FolderRpcProxyFactory folderRpcProxyFactory,
                              final DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy,
                              final DiskResourceAutoBeanFactory drFactory,
                              final DataSearchPresenter dataSearchPresenter,
                              final IplantDisplayStrings displayStrings,
                              final EventBus eventBus) {
        this.drFactory = drFactory;
        this.displayStrings = displayStrings;
        this.eventBus = eventBus;
        this.dataSearchPresenter = dataSearchPresenter;

        // Initialize View's grid and tree loaders
        DiskResourceView.FolderRpcProxy folderRpcProxy = folderRpcProxyFactory.create(this);
        PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader = new PagingLoader<>(folderContentsRpcProxy);
        treeLoader = new TreeLoader<Folder>(folderRpcProxy) {
            @Override
            public boolean hasChildren(Folder parent) {
                return parent.hasSubDirs();
            }
        };
        gridLoader.setReuseLoadConfig(true);
        gridLoader.setRemoteSort(true);
        this.view = diskResourceViewFactory.create(this, treeLoader, gridLoader);

        folderContentsRpcProxy.setHasSafeHtml(view.getCenterHeader());

        DiskResourceSearchField searchField = this.view.getToolbar().getSearchField();

        // Wire up DiskResourceSelectionChangedEventHandlers
        this.view.addDiskResourceSelectionChangedEventHandler(this);

        // Wire up FolderSelectedEventHandlers
        this.view.addFolderSelectedEventHandler(this);
        this.view.addFolderSelectedEventHandler(searchField);
        this.dataSearchPresenter.addFolderSelectedEventHandler(this); // FIXME JDS This is a little backwards

        // Wire up SavedSearchDeletedEventHandlers
        this.dataSearchPresenter.addSavedSearchDeletedEventHandler(searchField);

        // Wire up SubmitDiskResourceQueryEventHandlers
        folderRpcProxy.addSubmitDiskResourceQueryEventHandler(dataSearchPresenter);

        // Wire up SavedSearchedRetrievedEventHandlers
        folderRpcProxy.addSavedSearchedRetrievedEventHandler(dataSearchPresenter);

        // Wire up DeleteSavedSearchClickedEventHandlers
        this.view.addDeleteSavedSearchClickedEventHandler(this.dataSearchPresenter);

        // Wire up SaveDiskResourceQueryClickedEventHandlers
        searchField.addSaveDiskResourceQueryClickedEventHandler(this.dataSearchPresenter);

        // Wire up SubmitDiskResourceQueryEventHandlers
        searchField.addSubmitDiskResourceQueryEventHandler(this.dataSearchPresenter);

        // Wire up UpdateSavedSearchesEventHandlers
        this.dataSearchPresenter.addUpdateSavedSearchesEventHandler(this);

        treeLoader.addLoadHandler(new CachedFolderTreeStoreBinding(view.getTreeStore()));

        folderRpcProxy.addRootFoldersRetrievedEventHandler(this);

        // Wire up global event handlers
        DiskResourcesEventHandler diskResourcesEventHandler = new DiskResourcesEventHandler(this);
        dreventHandlers.add(eventBus.addHandler(FolderRefreshEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(DiskResourcesDeletedEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(FolderCreatedEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(DiskResourceRenamedEvent.TYPE, diskResourcesEventHandler));
        dreventHandlers.add(eventBus.addHandler(DiskResourcesMovedEvent.TYPE, diskResourcesEventHandler));
    }

    @Override
    public HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler handler) {
        return view.addDiskResourceSelectionChangedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addFolderSelectedEventHandler(FolderSelectionEvent.FolderSelectionEventHandler handler) {
        return view.addFolderSelectedEventHandler(handler);
    }

    @Override
    public void onDiskResourceNameSelected(DiskResourceNameSelectedEvent event) {
        checkNotNull(event.getSelectedItem());

        if (event.getSelectedItem() instanceof Folder) {
            setSelectedFolderByPath(event.getSelectedItem());
        } else if ((event.getSelectedItem() instanceof File) && isFilePreviewEnabled) {
            // TODO: JDS - Possibly refactor to get stat/manifest before hand
            eventBus.fireEvent(new ShowFilePreviewEvent((File)event.getSelectedItem(), this));
        }
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
    public void onRequestShareByDataLink(ShareByDataLinkEvent event) {
        checkNotNull(event.getDiskResourceToShare());
        doShareByDataLink(event.getDiskResourceToShare());
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
    public void onManageComments(ManageCommentsEvent event) {
        doManageDiskResourceComments(event.getDiskResource());
    }

    @Override
    public Folder convertToFolder(DiskResource selectedItem) {
        AutoBean<DiskResource> autoBean = AutoBeanUtils.getAutoBean(selectedItem);
        Splittable encode = AutoBeanCodex.encode(autoBean);
        AutoBean<Folder> decode = AutoBeanCodex.decode(drFactory, Folder.class, encode);
        return decode.as();
    }

    @Override
    public void manageSelectedResourceComments() {
        checkState(getSelectedDiskResources().size() == 1, "Only one Disk Resource should be selected, but there are %i", getSelectedDiskResources().size());
        doManageDiskResourceComments(Iterables.getFirst(getSelectedDiskResources(), null));
    }

    @Override
    public void onRootFoldersRetrieved(RootFoldersRetrievedEvent event) {
        DiskResourceFavorite diskResourceFavorite = drFactory.getFavortieFolder().as();
        String id = userInfo.getHomePath() + FAVORITES_FOLDER_PATH;
        diskResourceFavorite.setId(id);
        diskResourceFavorite.setPath(id);
        diskResourceFavorite.setName(FAVORITES_FOLDER_NAME);
        if(view.getTreeStore().findModel(diskResourceFavorite) == null){
            view.getTreeStore().add(diskResourceFavorite);
        }
    }

    /**
     * Ensures that the navigation window shows the given templates. These show up in the navigation
     * window as "magic folders".
     * <p/>
     * This method ensures that the only the given list of queryTemplates will be displayed in the
     * navigation pane.
     *
     * Only objects which are instances of {@link DiskResourceQueryTemplate} will be operated on. Items
     * which can't be found in the tree store will be added, and items which are already in the store and
     * are marked as dirty will be updated.
     *
     */
    @Override
    public void onUpdateSavedSearches(UpdateSavedSearchesEvent event) {
        TreeStore<Folder> treeStore = view.getTreeStore();

        List<DiskResourceQueryTemplate> removedSearches = event.getRemovedSearches();
        if (removedSearches != null) {
            for (DiskResourceQueryTemplate qt : removedSearches) {
                treeStore.remove(qt);
            }
        }

        List<DiskResourceQueryTemplate> savedSearches = event.getSavedSearches();
        if (savedSearches != null) {
            for (DiskResourceQueryTemplate qt : savedSearches) {
                // If the item already exists in the store and the template is dirty, update it
                if (treeStore.findModelWithKey(qt.getId()) != null) {
                    if (qt.isDirty()) {
                        treeStore.update(qt);
                    }
                } else {
                    treeStore.add(qt);
                }
            }
        }
    }

    void doManageDiskResourceComments(final DiskResource dr){
        checkNotNull(dr);
        // call to retrieve comments...and show dialog
        Window d = new Window();
        d.setHeadingText(displayStrings.comments());
        d.remove(d.getButtonBar());
        d.setSize("600px", "450px");
        CommentsView cv = new CommentsViewImpl();
        CommentsPresenter cp = new CommentsPresenter(cv,
                                                     dr.getId(),
                                                     dr.getPermission() == PermissionValue.own,
                                                     fsmdataService);
        cv.setPresenter(cp);
        cp.go(d);
        d.show();

    }

    void doShareByDataLink(final DiskResource toBeShared) {
        if (toBeShared instanceof Folder) {
            showShareLink(GWT.getHostPageBaseURL() + "?type=data&folder=" + toBeShared.getPath());
        } else {
            diskResourceService.createDataLinks(Arrays.asList(toBeShared.getPath()), new AsyncCallback<String>() {

                @Override
                public void onSuccess(String result) {
                    AutoBean<DataLinkList> tickets = AutoBeanCodex.decode(dlFactory, DataLinkList.class, result);
                    List<DataLink> dlList = tickets.as().getTickets();
                    showShareLink(dlList.get(0).getDownloadUrl());
                }

                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(errorStrings.createDataLinksError(), caught);
                }
            });
        }
    }

    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        doSelectFolder(event.getSelectedFolder());
    }

    @Override
    public void onRequestManageMetadata(ManageMetadataEvent event) {
        manageSelectedResourceMetadata(event.getDiskResource());
    }

    @Override
    public void onRequestManageSharing(ManageSharingEvent event) {
        checkNotNull(event.getDiskResourceToShare());
        doShareWithCollaborators(Lists.newArrayList(event.getDiskResourceToShare()));
    }

    private void showShareLink(String linkId) {
        // Open dialog window with text selected.
        IPlantDialog dlg = new IPlantDialog();
        dlg.setHeadingText(displayStrings.copy());
        dlg.setHideOnButtonClick(true);
        dlg.setResizable(false);
        dlg.setSize("535", "130");
        TextField textBox = new TextField();
        textBox.setWidth(500);
        textBox.setReadOnly(true);
        textBox.setValue(linkId);
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        dlg.setWidget(container);
        container.add(textBox);
        container.add(new Label(displayStrings.copyPasteInstructions()));
        dlg.setFocusWidget(textBox);
        dlg.show();
        textBox.selectAll();
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
        setSelectedFolderByPath(getSelectedFolder());
    }

    @Override
    public void disableFilePreview() {
        isFilePreviewEnabled = false;
    }

    @Override
    public void go(HasOneWidget container, HasPath folderToSelect, final List<? extends HasId> diskResourcesToSelect) {

        if ((folderToSelect == null) || Strings.isNullOrEmpty(folderToSelect.getPath())) {
            go(container);
        } else {
            container.setWidget(view);
            setSelectedFolderByPath(folderToSelect);
            setSelectedDiskResourcesById(diskResourcesToSelect);
        }
    }

    @Override
    public void setSelectedDiskResourcesById(final List<? extends HasId> diskResourcesToSelect) {
        SelectDiskResourceByIdStoreAddHandler diskResourceStoreAddHandler = new SelectDiskResourceByIdStoreAddHandler(diskResourcesToSelect, this);
        HandlerRegistration diskResHandlerReg = view.getListStore().addStoreAddHandler(diskResourceStoreAddHandler);
        addEventHandlerRegistration(diskResourceStoreAddHandler, diskResHandlerReg);
    }

    @Override
    public void setSelectedFolderByPath(final HasPath folderToSelect) {
        if ((folderToSelect == null) || Strings.isNullOrEmpty(folderToSelect.getPath())) {
            return;
        }

        Folder folder = view.getFolderByPath(folderToSelect.getPath());
        if (folder != null) {
            final Folder selectedFolder = view.getSelectedFolder();
            if (folder == selectedFolder) {
                // If the folder IS the currently selected folder, then trigger
                // reload of center panel.
                doSelectFolder(folder);
            } else {
                /*
                 * If it is NOT the currently selected folder, then deselect the
                 * current folder and re-select it.
                 */
                view.deSelectNavigationFolder();
                view.setSelectedFolder(folder);
            }
        } else {
            // Create and add the SelectFolderByIdLoadHandler to the treeLoader.
            final SelectFolderByPathLoadHandler handler = new SelectFolderByPathLoadHandler(folderToSelect, this, announcer);
            /*
             * Only add handler if no root items have been loaded, or the folderToSelect has a common
             * root with the treestore.
             */
            if (view.getTreeStore().getRootCount() < 1 || handler.isRootFolderDetected()) {
                addEventHandlerRegistration(handler, treeLoader.addLoadHandler(handler));
            }
        }
    }

    @Override
    public Folder getSelectedFolder() {
        return view.getSelectedFolder();
    }

    @Override
    public Set<DiskResource> getSelectedDiskResources() {
        return view.getSelectedDiskResources();
    }

    private void getDetails(DiskResource path) {
        
        diskResourceService.getStat(DiskResourceUtil.asStringPathTypeMap(Arrays.asList(path), path instanceof File ? TYPE.FILE
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
        eventBus.fireEvent(new RequestBulkUploadEvent(this, getSelectedUploadFolder()));
    }

    @Override
    public void doSimpleUpload() {
        eventBus.fireEvent(new RequestSimpleUploadEvent(this, getSelectedUploadFolder()));
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
        eventBus.fireEvent(new RequestImportFromUrlEvent(this, getSelectedUploadFolder()));
    }

    private Folder getSelectedUploadFolder() {
        Folder selectedFolder = getSelectedFolder();

        if (selectedFolder == null) {
            return view.getFolderByPath(UserInfo.getInstance().getHomePath());
        }

        return selectedFolder;
    }

    @Override
    public void openNewWindow(boolean atThisLocation) {
        // If current folder is null, or window SHOULD NOT be opened at current location, folderPath is null
        String folderPath = (getSelectedFolder() == null) || !atThisLocation ? null : getSelectedFolder().getPath();
        OpenFolderEvent openEvent = new OpenFolderEvent(folderPath);
        openEvent.requestNewView(true);
        eventBus.fireEvent(openEvent);
    }

    @Override
    public void refreshSelectedFolder() {
        checkState(getSelectedFolder() != null, "Selected folder should no be null");
        doRefreshFolder(getSelectedFolder());
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
        } else if (getSelectedFolder() != null) {
            RenameFolderDialog dlg = new RenameFolderDialog(getSelectedFolder(), this);
            dlg.show();
        }
    }

    @Override
    public void restoreSelectedResources() {
        final Set<DiskResource> selectedResources = getSelectedDiskResources();

        if (selectedResources == null || selectedResources.isEmpty()) {
            return;
        }

        mask(""); //$NON-NLS-1$

        DiskResourceRestoreCallback callback = new DiskResourceRestoreCallback(this, drFactory, selectedResources);
        if (view.isSelectAllChecked()) {
            diskResourceService.restoreAll(callback);
        } else {
            HasPaths request = drFactory.pathsList().as();
            request.setPaths(DiskResourceUtil.asStringPathList(selectedResources));
            diskResourceService.restoreDiskResource(request, callback);
        }
    }

    @Override
    public void selectTrashFolder() {
        final HasPath hasPath = CommonModelUtils.createHasPathFromString(userInfo.getTrashPath());
        setSelectedFolderByPath(hasPath);
    }

    @Override
    public void sendSelectedResourceToEnsembl() {
        final Set<DiskResource> selection = view.getSelectedDiskResources();
        Iterator<DiskResource> it = selection.iterator();
        DiskResource next = it.next();
        String infoType = getInfoType(next);
        if(Strings.isNullOrEmpty(infoType)) {
            showInfoTypeError(errorStrings.unsupportedEnsemblInfoType());
            return;
        }
        if (DiskResourceUtil.isEnsemblVizTab(DiskResourceUtil.createInfoTypeSplittable(infoType))) {
            eventBus.fireEvent(new RequestSendToEnsemblEvent((File)next, InfoType.fromTypeString(infoType)));
        } else {
            showInfoTypeError(errorStrings.unsupportedEnsemblInfoType());
        }
    }

    @Override
    public void sendSelectedResourcesToCoge() {
        final Set<DiskResource> selection = view.getSelectedDiskResources();
        Iterator<DiskResource> it = selection.iterator();
        DiskResource next = it.next();
        String infoType = getInfoType(next);
        if (Strings.isNullOrEmpty(infoType)) {
            showInfoTypeError(errorStrings.unsupportedCogeInfoType());
            return;
        }
        if (DiskResourceUtil.isGenomeVizTab(DiskResourceUtil.createInfoTypeSplittable(infoType))) {
            eventBus.fireEvent(new RequestSendToCoGeEvent((File)next));
        } else {
            showInfoTypeError(errorStrings.unsupportedCogeInfoType());
        }
    }

    @Override
    public void sendSelectedResourcesToTreeViewer() {
        final Set<DiskResource> selection = view.getSelectedDiskResources();
        Iterator<DiskResource> it = selection.iterator();
        DiskResource next = it.next();
        String infoType = getInfoType(next);
        if (Strings.isNullOrEmpty(infoType)) {
            showInfoTypeError(errorStrings.unsupportedTreeInfoType());
            return;
        }
        if (DiskResourceUtil.isTreeTab(DiskResourceUtil.createInfoTypeSplittable(infoType))) {
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
        CreateFolderDialog dlg = new CreateFolderDialog(getSelectedUploadFolder(), this);
        dlg.show();
    }

    @Override
    public void createNewPlainTextFile(FileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewRFile(FileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewPerlFile(FileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewPythonFile(FileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewShellScript(FileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewMdFile(FileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void createNewTabFile(TabularFileViewerWindowConfig config) {
        config.setParentFolder(getSelectedUploadFolder());
        CreateNewFileEvent event = new CreateNewFileEvent(config);
        eventBus.fireEvent(event);
    }

    @Override
    public void onNewPathListFileClicked(PathListWindowConfig config){
        config.setParentFolder(getSelectedUploadFolder());
        eventBus.fireEvent(new CreateNewFileEvent(config));
    }
    @Override
    public void doCreateNewFolder(Folder parentFolder, final String newFolderName) {
        view.mask(displayStrings.loadingMask());
        diskResourceService.createFolder(parentFolder, newFolderName, new CreateFolderCallback(parentFolder, view));
    }

    @Override
    public void doRefreshFolder(Folder folder) {
        folder = view.getFolderById(folder.getId());
        if (folder == null) {
            return;
        }

        Folder selectedFolder = getSelectedFolder();
        view.refreshFolder(folder);

        if (!(folder instanceof DiskResourceQueryTemplate) && selectedFolder == folder) {
            // If the folder is currently selected, then trigger reload of
            // center panel.
            doSelectFolder(folder);
        }

    }

    @Override
    public void doSimpleDownload() {
        eventBus.fireEvent(new RequestSimpleDownloadEvent(this, getSelectedDiskResources(), getSelectedFolder()));
    }

    @Override
    public void doBulkDownload() {
        eventBus.fireEvent(new RequestBulkDownloadEvent(this, view.isSelectAllChecked(), getSelectedDiskResources(), getSelectedFolder()));
    }

    @Override
    public void doRenameDiskResource(final DiskResource dr, final String newName) {
        if (dr != null && !dr.getName().equals(newName)) {
            view.mask(displayStrings.loadingMask());
            diskResourceService.renameDiskResource(dr, newName, new RenameDiskResourceCallback(dr, view));
        }
    }

    @Override
    public void manageSelectedResourceCollaboratorSharing() {
        doShareWithCollaborators(getSelectedDiskResources());
    }

    private void doShareWithCollaborators(final Iterable<DiskResource> resourcesToBeShared){
        DataSharingDialog dlg = dataSharingDialogFactory.createDataSharingDialog(Sets.newHashSet(resourcesToBeShared));
        dlg.show();
        dlg.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                final Set<DiskResource> selection = view.getSelectedDiskResources();
                if (selection != null && selection.size() == 1) {
                    Iterator<DiskResource> it = selection.iterator();
                    DiskResource next = it.next();
                    getDetails(next);
                }
            }
        });
   }

    @Override
    public void deleteSelectedResources() {
        Set<DiskResource> selectedResources = getSelectedDiskResources();
        if (!selectedResources.isEmpty() && DiskResourceUtil.isOwner(selectedResources)) {
            HashSet<DiskResource> drSet = Sets.newHashSet(selectedResources);

            if (DiskResourceUtil.containsTrashedResource(drSet)) {
                confirmDelete(drSet);
            } else {
                delete(drSet, displayStrings.deleteMsg());
            }
        }
    }

    private void confirmDelete(final Set<DiskResource> drSet) {
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

    private void delete(Set<DiskResource> drSet, String announce) {
        view.mask(displayStrings.loadingMask());
        Folder selectedFolder = getSelectedFolder();
        final AsyncCallback<HasPaths> callback = new DiskResourceDeleteCallback(drSet, selectedFolder, view, announce);

        if (view.isSelectAllChecked() && selectedFolder != null) {
            diskResourceService.deleteContents(selectedFolder.getPath(), callback);

        } else {
            diskResourceService.deleteDiskResources(drSet, callback);
        }
    }

    @Override
    public void manageSelectedResourceMetadata() {
        manageSelectedResourceMetadata(getSelectedDiskResources().iterator().next());
    }

    private void manageSelectedResourceMetadata(DiskResource selected) {
        final DiskResourceMetadataView mdView = new DiskResourceMetadataView(selected);
        final DiskResourceMetadataView.Presenter mdPresenter = new MetadataPresenter(selected, mdView);
        final IPlantDialog ipd = new IPlantDialog(true);

        ipd.setSize("600", "400"); //$NON-NLS-1$ //$NON-NLS-2$
        ipd.setHeadingText(displayStrings.metadata() + ":" + selected.getName()); //$NON-NLS-1$
        ipd.setResizable(true);
        ipd.addHelp(new HTML(helpStrings.metadataHelp()));

        mdPresenter.go(ipd);

        if (DiskResourceUtil.isWritable(selected)) {
            ipd.setHideOnButtonClick(false);
            ipd.addOkButtonSelectHandler(new SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    if (mdView.shouldValidate() && !mdView.isValid()) {
                        ErrorAnnouncementConfig errNotice = new ErrorAnnouncementConfig(errorStrings.metadataFormInvalid());
                        IplantAnnouncer.getInstance().schedule(errNotice);
                    } else {
                        mdPresenter.setDiskResourceMetadata(new DiskResourceMetadataUpdateCallback(ipd));
                    }
                }
            });

            ipd.addCancelButtonSelectHandler(new SelectHandler() {

                @Override
                public void onSelect(SelectEvent event) {
                    ipd.hide();
                }
            });
        }

        ipd.show();
    }

    @Override
    public void unregisterHandler(EventHandler handler) {
        if (registeredHandlers.containsKey(handler)) {
            registeredHandlers.remove(handler).removeHandler();
        }
    }

    @Override
    public void addEventHandlerRegistration(EventHandler handler, HandlerRegistration reg) {
        registeredHandlers.put(handler, reg);
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
            if (DiskResourceUtil.isChildOfFolder(targetFolder, dr)) {
                return false;
            }

            if (dr instanceof Folder) {
                if (targetFolder.getPath().equals(dr.getPath())) {
                    return false;
                }

                // cannot drag an ancestor (parent, grandparent, etc) onto a
                // child and/or descendant
                if (DiskResourceUtil.isDescendantOfFolder((Folder)dr, targetFolder)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void doMoveDiskResources(Folder targetFolder, Set<DiskResource> resources) {
        Folder parent = getSelectedFolder();
        view.mask(displayStrings.loadingMask());
        if (view.isSelectAllChecked()) {
            diskResourceService.moveContents(parent.getPath(), targetFolder, new DiskResourceMoveCallback(view, true, parent, targetFolder, resources));
        } else {
            diskResourceService.moveDiskResources(resources, targetFolder, new DiskResourceMoveCallback(view, false, parent, targetFolder, resources));
        }
    }

    @Override
    public Set<? extends DiskResource> getDragSources(IsWidget source, Element dragStartEl) {
        // Verify the drag started from a valid item in the tree or grid, then
        // return the selected items.
        if (view.isViewGrid(source)) {
            Set<DiskResource> selectedResources = getSelectedDiskResources();

            if (!selectedResources.isEmpty()) {
                // Verify the dragStartEl is a row within the grid.
                Element targetRow = view.findGridRow(dragStartEl);

                if (targetRow != null) {
                    int dropIndex = view.findRowIndex(targetRow);

                    DiskResource selDiskResource = view.getListStore().get(dropIndex);
                    if (selDiskResource != null) {
                        return Sets.newHashSet(selectedResources);
                    }
                }
            }
        } else if (view.isViewTree(source) && (getSelectedFolder() != null)) {
            // Verify the dragStartEl is a folder within the tree.
            Folder srcFolder = getDropTargetFolder(source, dragStartEl);

            if (srcFolder != null) {
                return Sets.newHashSet(srcFolder);
            }
        }

        return null;
    }

    @Override
    public Folder getDropTargetFolder(IsWidget target, Element eventTargetElement) {
        Folder ret = null;
        if (view.isViewTree(target) && (view.findTreeNode(eventTargetElement) != null)) {
            TreeNode<Folder> targetTreeNode = view.findTreeNode(eventTargetElement);
            ret = targetTreeNode.getModel();
        } else if (view.isViewGrid(target)) {
            Element targetRow = view.findGridRow(eventTargetElement).cast();

            if (targetRow == null) {
                ret = getSelectedUploadFolder();
            } else {
                int dropIndex = view.findRowIndex(targetRow);

                DiskResource selDiskResource = view.getListStore().get(dropIndex);
                ret = (selDiskResource instanceof Folder) ? (Folder)selDiskResource : null;
            }
        }
        return ret;
    }

    @Override
    public void doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent event) {
        doSelectFolder(event.getQueryTemplate());
    }

    @Override
    public void deSelectDiskResources() {
        view.deSelectDiskResources();
    }

    void doSelectFolder(Folder folderToSelect){
        view.loadFolder(folderToSelect);
    }

    void doEmptyTrash() {
        view.mask(displayStrings.loadingMask());
        diskResourceService.emptyTrash(UserInfo.getInstance().getUsername(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                doRefreshFolder(view.getFolderByPath(UserInfo.getInstance().getTrashPath()));
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
        final FolderSelectDialog fsd = selectorDialogFactory.createFolderSelector();
        fsd.show();
        fsd.addOkButtonSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                Folder targetFolder = fsd.getValue();
                final Set<DiskResource> selectedResources = getSelectedDiskResources();
                if (DiskResourceUtil.isMovable(targetFolder, selectedResources)) {
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
        boolean hasLoadHandlers = false;
        for (Entry<EventHandler, HandlerRegistration> entry : registeredHandlers.entrySet()) {
            if (entry.getKey() instanceof LoadHandler<?, ?>) {
                hasLoadHandlers = true;
            }

        }
        if (!hasLoadHandlers) {
            view.unmask();
        }

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
        if (info == null) {
            return;
        }

        view.displayAndCacheDiskResourceInfo(info);
    }

    @Override
    public void unmaskVizMenuOptions() {
        view.unmaskSendToCoGe();
        view.unmaskSendToEnsembl();
        view.unmaskSendToTreeViewer();
    }

    @Override
    public void shareSelectedFolderByDataLink() {
        checkState(getSelectedDiskResources().size() == 1, "Selected resources should only contain 1 item, but contains %i", getSelectedDiskResources().size());

        doShareByDataLink(Iterables.getFirst(getSelectedDiskResources(), null));
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
                    view.updateStore(next);
                }
            }

        }
    }

    @Override
    public void doSearchTaggedWithResources(Set<IplantTag> tags) {
        final SearchAutoBeanFactory factory = GWT.create(SearchAutoBeanFactory.class);
        DiskResourceQueryTemplate qt = factory.dataSearchFilter().as();
        qt.setTagQuery(tags);
        dataSearchPresenter.doSubmitDiskResourceQuery(new SubmitDiskResourceQueryEvent(qt));
    }

}
