package org.iplantc.de.diskResource.client.presenters.navigation;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshedEvent;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourceRenamedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent;
import org.iplantc.de.diskResource.client.events.FolderCreatedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.events.search.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.events.selection.ImportFromUrlSelected;
import org.iplantc.de.diskResource.client.events.selection.SimpleUploadSelected;
import org.iplantc.de.diskResource.client.gin.factory.NavigationViewFactory;
import org.iplantc.de.diskResource.client.presenters.grid.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.navigation.proxy.CachedFolderTreeStoreBinding;
import org.iplantc.de.diskResource.client.presenters.navigation.proxy.SelectFolderByPathLoadHandler;
import org.iplantc.de.diskResource.client.views.navigation.NavigationViewDnDHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Collection;
import java.util.List;

/**
 * @author jstroot
 */
public class NavigationPresenterImpl implements
                                    NavigationView.Presenter,
                                    FolderSelectionEvent.FolderSelectionEventHandler,
                                    FolderRefreshedEvent.FolderRefreshedEventHandler,
                                    DiskResourcesDeletedEvent.DiskResourcesDeletedEventHandler,
                                    DiskResourceRenamedEvent.DiskResourceRenamedEventHandler,
                                    FolderCreatedEvent.FolderCreatedEventHandler,
                                    DiskResourcesMovedEvent.DiskResourcesMovedEventHandler {

    private static class FolderStoreDataChangeHandler implements
                                                     StoreDataChangeEvent.StoreDataChangeHandler<Folder> {
        private final NavigationView.Appearance appearance;
        private final Tree<Folder, Folder> tree;

        public FolderStoreDataChangeHandler(final Tree<Folder, Folder> tree,
                                            final NavigationView.Appearance appearance) {
            this.tree = tree;
            this.appearance = appearance;
        }

        @Override
        public void onDataChange(StoreDataChangeEvent<Folder> event) {
            Folder folder = event.getParent();
            if (folder != null && tree.getStore().getAllChildren(folder) != null) {
                for (Folder f : tree.getStore().getAllChildren(folder)) {
                    if (f.isFilter()) {
                        Tree.TreeNode<Folder> tn = tree.findNode(f);
                        tree.getView()
                            .getTextElement(tn)
                            .setInnerSafeHtml(appearance.treeNodeFilterText(f.getName()));
                    }
                }
            }
        }
    }

    final TreeStore<Folder> treeStore;
    @Inject
    IplantAnnouncer announcer;
    @Inject
    NavigationView.Presenter.Appearance appearance;
    @Inject
    DiskResourceUtil diskResourceUtil;
    @Inject
    DiskResourceAutoBeanFactory factory;
    @Inject
    UserInfo userInfo;
    private final EventBus eventBus;
    private final DiskResourceView.FolderRpcProxy folderRpcProxy;
    private final List<HandlerRegistration> handlerRegistrations;
    private final TreeLoader<Folder> treeLoader;
    private final NavigationView view;
    private IsMaskable maskable;
    private DiskResourceView.Presenter parentPresenter;

    @Inject
    NavigationPresenterImpl(final NavigationViewFactory viewFactory,
                            final TreeStore<Folder> treeStore,
                            final DiskResourceView.FolderRpcProxy folderRpcProxy,
                            final DiskResourceUtil diskResourceUtil,
                            final EventBus eventBus,
                            final NavigationView.Appearance appearance) {
        this.treeStore = treeStore;
        this.folderRpcProxy = folderRpcProxy;
        this.eventBus = eventBus;
        treeLoader = new TreeLoader<Folder>(folderRpcProxy) {
            @Override
            public boolean hasChildren(Folder parent) {
                return parent.hasSubDirs();
            }
        };
        view = viewFactory.create(treeStore, treeLoader, new NavigationViewDnDHandler(diskResourceUtil,
                                                                                      this,
                                                                                      appearance));
        handlerRegistrations = Lists.newArrayList();

        view.addFolderSelectedEventHandler(this);
        this.treeStore.addStoreDataChangeHandler(new FolderStoreDataChangeHandler(view.getTree(),
                                                                                  appearance));
        this.treeLoader.addLoadHandler(new CachedFolderTreeStoreBinding(treeStore));

        // Wire up global event handlers
        handlerRegistrations.add(eventBus.addHandler(FolderRefreshedEvent.TYPE, this));
        handlerRegistrations.add(eventBus.addHandler(DiskResourcesDeletedEvent.TYPE, this));
        handlerRegistrations.add(eventBus.addHandler(DiskResourceRenamedEvent.TYPE, this));
        handlerRegistrations.add(eventBus.addHandler(FolderCreatedEvent.TYPE, this));
        handlerRegistrations.add(eventBus.addHandler(DiskResourcesMovedEvent.TYPE, this));
    }

    // <editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration
            addRootFoldersRetrievedEventHandler(RootFoldersRetrievedEvent.RootFoldersRetrievedEventHandler handler) {
        return folderRpcProxy.addRootFoldersRetrievedEventHandler(handler);
    }

    @Override
    public HandlerRegistration
            addSavedSearchedRetrievedEventHandler(SavedSearchesRetrievedEvent.SavedSearchesRetrievedEventHandler handler) {
        return folderRpcProxy.addSavedSearchedRetrievedEventHandler(handler);
    }

    @Override
    public HandlerRegistration
            addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler handler) {
        return folderRpcProxy.addSubmitDiskResourceQueryEventHandler(handler);
    }

    // </editor-fold>

    // <editor-fold desc="Event Handlers">
    @Override
    public void onBeforeLoad(BeforeLoadEvent<FolderContentsLoadConfig> event) {
        if (getSelectedFolder() == null) {
            return;
        }

        final Folder folderToBeLoaded = event.getLoadConfig().getFolder();

        /*
         * If the loaded contents are not the contents of the currently selected folder, then cancel the
         * load.
         */
        if (!Strings.isNullOrEmpty(folderToBeLoaded.getId())
                && !folderToBeLoaded.getId().equals(getSelectedFolder().getId())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onDiskResourceNameSelected(DiskResourceNameSelectedEvent event) {

        if (!(event.getSelectedItem() instanceof Folder)) {
            return;
        }
        setSelectedFolder(event.getSelectedItem());
    }

    @Override
    public void onDiskResourcePathSelected(DiskResourcePathSelectedEvent event) {
        setSelectedFolder(event.getSelectedDiskResource());
    }

    @Override
    public void onDiskResourcesDeleted(Collection<DiskResource> resources, Folder parentFolder) {
        reloadTreeStoreFolderChildren(parentFolder);
    }

    @Override
    public void onDiskResourcesMoved(DiskResourcesMovedEvent event) {
        List<DiskResource> resourcesToMove = event.getResourcesToMove();
        Folder destinationFolder = event.getDestinationFolder();
        Folder selectedFolder = getSelectedFolder();
        // moved contents only not the folder itself
        if (event.isMoveContents()) {
            reloadTreeStoreFolderChildren(destinationFolder);
            reloadTreeStoreFolderChildren(selectedFolder);
        } else if (resourcesToMove.contains(selectedFolder)) {
            selectedFolderMovedFromNavTree(selectedFolder, destinationFolder);
        } else {
            diskResourcesMovedFromGrid(resourcesToMove, selectedFolder, destinationFolder);
        }
    }

    @Override
    public void onFolderCreated(Folder parentFolder, Folder newFolder) {
        reloadTreeStoreFolderChildren(parentFolder);
    }

    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        if (event.getSelectedFolder() instanceof DiskResourceQueryTemplate) {
            // If the given query has not been saved, we need to deselect everything
            DiskResourceQueryTemplate searchQuery = (DiskResourceQueryTemplate)event.getSelectedFolder();
            if (!searchQuery.isSaved()) {
                deSelectAll();
            }
        }
    }

    @Override
    public void onImportFromUrlSelected(final ImportFromUrlSelected event) {
        Folder destinationFolder = event.getSelectedFolder();
        if (destinationFolder == null) {
            destinationFolder = getSelectedUploadFolder();
        }
        eventBus.fireEvent(new RequestImportFromUrlEvent(destinationFolder));
    }

    @Override
    public void onRename(DiskResource originalDr, DiskResource newDr) {
        Folder parent = getFolderByPath(diskResourceUtil.parseParent(newDr.getPath()));
        if (parent != null) {
            reloadTreeStoreFolderChildren(parent);
        }
    }

    @Override
    public void onFolderRefreshed(FolderRefreshedEvent event) {
        reloadTreeStoreFolderChildren(event.getFolder());
    }

    @Override
    public void onSimpleUploadSelected(SimpleUploadSelected event) {
        Folder destinationFolder = event.getSelectedFolder();
        if (destinationFolder == null) {
            destinationFolder = getSelectedUploadFolder();
        }
        eventBus.fireEvent(new RequestSimpleUploadEvent(destinationFolder));
    }

    /**
     * Ensures that the navigation window shows the given templates. These show up in the navigation
     * window as "magic folders".
     * <p/>
     * This method ensures that the only the given list of queryTemplates will be displayed in the
     * navigation pane.
     * <p/>
     * Only objects which are instances of {@link DiskResourceQueryTemplate} will be operated on. Items
     * which can't be found in the tree store will be added, and items which are already in the store and
     * are marked as dirty will be updated.
     */
    @Override
    public void onUpdateSavedSearches(UpdateSavedSearchesEvent event) {
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
                updateQueryTemplate(qt);
            }
        }
    }

    // </editor-fold>

    @Override
    public void addFolder(Folder folder) {
        if (treeStore.findModel(folder) != null) {
            // Already added
            return;
        }
        treeStore.add(folder);
    }

    @Override
    public void cleanUp() {
        for (HandlerRegistration hr : handlerRegistrations) {
            eventBus.removeHandler(hr);
        }
    }

    @Override
    public void doMoveDiskResources(Folder targetFolder, List<DiskResource> dropData) {
        parentPresenter.doMoveDiskResources(targetFolder, dropData);
    }

    @Override
    public void expandFolder(Folder folder) {
        view.getTree().setExpanded(folder, true);
    }

    @Override
    public Tree.TreeNode<Folder> findTreeNode(Element el) {
        return view.getTree().findNode(el);
    }

    @Override
    public Folder getFolderByPath(String path) {
        if (treeStore.getRootItems() != null) {
            for (Folder folder : treeStore.getAll()) {
                if (folder.getPath().equals(path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    @Override
    public Iterable<Folder> getRootItems() {
        return treeStore.getRootItems();
    }

    @Override
    public Folder getSelectedFolder() {
        return view.getTree().getSelectionModel().getSelectedItem();
    }

    @Override
    public Folder getSelectedUploadFolder() {
        if (getSelectedFolder() == null) {
            return getFolderByPath(userInfo.getHomePath());
        }
        return getSelectedFolder();
    }

    @Override
    public NavigationView getView() {
        return view;
    }

    @Override
    public boolean isLoaded(Folder folder) {
        return view.getTree().findNode(folder).isLoaded();
    }

    @Override
    public void reloadTreeStoreFolderChildren(Folder folder) {
        if (folder == null || treeStore.findModel(folder) == null) {
            return;
        }

        Folder selectedFolder = getSelectedFolder();
        boolean isCurrent = false;
        boolean isDescendant = false;
        if (selectedFolder != null) {
            isCurrent = folder.getId().equals(selectedFolder.getId());
            isDescendant = diskResourceUtil.isDescendantOfFolder(folder, selectedFolder);
        }

        removeChildren(folder);
        if (!isDescendant) {
            // Only trigger a load of the refreshed folder if selectedFolder is not a descendant of
            // the refreshed folder, otherwise the lazy-loader will handle the reload.
            treeLoader.load(folder);
        }

        if (isCurrent || isDescendant) {
            if (!(selectedFolder instanceof DiskResourceQueryTemplate)) {
                // Re-select selectedFolder to cause a selection changed event
                // or to trigger lazy-loading
                setSelectedFolder((HasPath)selectedFolder);
            }
        }
    }

    @Override
    public boolean rootsLoaded() {
        return treeStore.getRootCount() > 0;
    }

    @Override
    public void setMaskable(IsMaskable maskable) {
        this.maskable = maskable;
        this.folderRpcProxy.setMaskable(maskable);
    }

    @Override
    public void setParentPresenter(DiskResourceView.Presenter parentPresenter) {
        this.parentPresenter = parentPresenter;
    }

    @Override
    public void setSelectedFolder(final Folder folder) {
        if (folder == null) {
            return;
        }
        final Folder findModelWithKey = treeStore.findModelWithKey(folder.getId());
        if (findModelWithKey != null) {
            view.getTree().getSelectionModel().deselectAll();
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    view.getTree().getSelectionModel().select(true, findModelWithKey);
                    view.getTree().scrollIntoView(findModelWithKey);
                }
            });
        }
    }

    @Override
    public void setSelectedFolder(HasPath hasPath) {
        if ((hasPath == null) || Strings.isNullOrEmpty(hasPath.getPath())) {
            return;
        }

        Folder folder = getFolderByPath(hasPath.getPath());
        if (folder != null) {
            /*
             * Trigger a selection changed event by deselecting the current folder and re-selecting it.
             */
            deSelectAll();
            setSelectedFolder(folder);
        } else {
            // Create and add the SelectFolderByIdLoadHandler to the treeLoader.
            final SelectFolderByPathLoadHandler handler = new SelectFolderByPathLoadHandler(hasPath,
                                                                                            this,
                                                                                            appearance,
                                                                                            maskable,
                                                                                            announcer);
            /*
             * Only add handler if no root items have been loaded, or the hasPath has a common root with
             * the treestore.
             */
            if (treeStore.getRootCount() < 1 || handler.isRootFolderDetected()) {
                HandlerRegistration handlerRegistration = treeLoader.addLoadHandler(handler);
                handler.setHandlerRegistration(handlerRegistration);
            }
        }
    }

    void deSelectAll() {
        view.getTree().getSelectionModel().deselectAll();
    }

    void removeChildren(Folder folder) {
        if (folder == null || treeStore.findModel(folder) == null) {
            return;
        }

        treeStore.removeChildren(folder);
        folder.setFolders(null);
        // Set folder node as not-loaded, to prevent problems in lazy-loader logic.
        view.getTree().findNode(folder).setLoaded(false);
    }

    void updateQueryTemplate(DiskResourceQueryTemplate queryTemplate) {
        Preconditions.checkNotNull(queryTemplate);

        if (treeStore.findModel(queryTemplate) == null) {
            treeStore.add(queryTemplate);
        } else if (queryTemplate.isDirty()) {
            // Only update if it is dirty
            treeStore.update(queryTemplate);
        }
    }

    private void diskResourcesMovedFromGrid(List<DiskResource> resourcesToMove,
                                            Folder selectedFolder,
                                            Folder destinationFolder) {
        if (diskResourceUtil.containsFolder(resourcesToMove)) {
            // Refresh the destination folder, since it has gained a child.
            if (diskResourceUtil.isDescendantOfFolder(destinationFolder, selectedFolder)) {
                removeChildren(destinationFolder);
            } else {
                /*
                 * Refresh the selected folder since it has lost a child. This will also reload the
                 * selected folder's contents in the grid.
                 */
                reloadTreeStoreFolderChildren(selectedFolder);
                // Refresh the destination folder since it has gained a child.
                reloadTreeStoreFolderChildren(destinationFolder);
                return;
            }
        }

        // Refresh the selected folder's contents.
        setSelectedFolder((HasPath)selectedFolder);
    }

    private void selectedFolderMovedFromNavTree(Folder selectedFolder, Folder destinationFolder) {
        /*
         * If the selected folder happens to be one of the moved items, then view the destination by
         * setting it as the selected folder.
         */
        Folder parentFolder = treeStore.getParent(selectedFolder);

        if (diskResourceUtil.isDescendantOfFolder(parentFolder, destinationFolder)) {
            /*
             * The destination is under the parent, so if we prune the parent and set the destination as
             * the selected folder, the parent will lazy-load down to the destination.
             */
            removeChildren(parentFolder);
        } else if (diskResourceUtil.isDescendantOfFolder(destinationFolder, parentFolder)) {
            /*
             * The parent is under the destination, so we only need to view the destination folder's
             * contents and refresh its children.
             */
            reloadTreeStoreFolderChildren(destinationFolder);
        } else {
            // Refresh the parent folder since it has lost a child.
            reloadTreeStoreFolderChildren(parentFolder);
            // Refresh the destination folder since it has gained a child.
            reloadTreeStoreFolderChildren(destinationFolder);
        }

        // View the destination folder's contents.
        setSelectedFolder((HasPath)destinationFolder);
    }
}
