package org.iplantc.de.diskResource.client.presenters.navigation;

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
import org.iplantc.de.diskResource.client.events.RootFoldersRetrievedEvent;
import org.iplantc.de.diskResource.client.events.SavedSearchesRetrievedEvent;
import org.iplantc.de.diskResource.client.gin.factory.NavigationViewFactory;
import org.iplantc.de.diskResource.client.presenters.handlers.CachedFolderTreeStoreBinding;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.SelectFolderByPathLoadHandler;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
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

import java.util.Set;

/**
 * @author jstroot
 */
public class NavigationPresenterImpl implements NavigationView.Presenter {

    private static class FolderStoreDataChangeHandler implements StoreDataChangeEvent.StoreDataChangeHandler<Folder> {
        private final Tree<Folder, Folder> tree;
        private final NavigationView.Appearance appearance;

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
                        tree.getView().getTextElement(tn).setInnerSafeHtml(appearance.treeNodeFilterText(f.getName()));
                    }
                }
            }
        }
    }

    private final DiskResourceView.FolderRpcProxy folderRpcProxy;
    private final TreeLoader<Folder> treeLoader;
    final TreeStore<Folder> treeStore;
    private final NavigationView view;

    @Inject DiskResourceAutoBeanFactory factory;
    @Inject UserInfo userInfo;
    @Inject IplantAnnouncer announcer;
    private IsMaskable maskable;
    private DiskResourceView.Presenter parentPresenter;

    @Inject
    NavigationPresenterImpl(final NavigationViewFactory viewFactory,
                            final TreeStore<Folder> treeStore,
                            final DiskResourceView.FolderRpcProxy folderRpcProxy,
                            final DiskResourceUtil diskResourceUtil,
                            final NavigationView.Appearance appearance) {
        this.treeStore = treeStore;
        this.folderRpcProxy = folderRpcProxy;
        treeLoader = new TreeLoader<Folder>(folderRpcProxy) {
            @Override
            public boolean hasChildren(Folder parent) {
                return parent.hasSubDirs();
            }
        };
        view = viewFactory.create(treeStore, treeLoader, new NavigationViewDnDHandler(diskResourceUtil, this, appearance));

        this.treeStore.addStoreDataChangeHandler(new FolderStoreDataChangeHandler(view.getTree(), appearance));
        this.treeLoader.addLoadHandler(new CachedFolderTreeStoreBinding(treeStore));
    }

    @Override
    public void addFolder(Folder folder) {
        if (treeStore.findModel(folder) != null) {
            // Already added
            return;
        }
        treeStore.add(folder);
    }

    @Override
    public void addFolder(Folder parent, Folder newChild) {
        treeStore.add(parent, newChild);
        if (getSelectedFolder() == null) {
            Folder request = factory.folder().as();
            request.setPath(userInfo.getHomePath());
            // Set selected folder by path
            setSelectedFolder((HasPath) request);
        }
    }

    @Override
    public void doMoveDiskResources(Folder targetFolder, Set<DiskResource> dropData) {
        parentPresenter.doMoveDiskResources(targetFolder, dropData);
    }

    @Override
    public Iterable<Folder> getRootItems() {
        return treeStore.getRootItems();
    }

    @Override
    public HandlerRegistration addRootFoldersRetrievedEventHandler(RootFoldersRetrievedEvent.RootFoldersRetrievedEventHandler handler) {
        return folderRpcProxy.addRootFoldersRetrievedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addSavedSearchedRetrievedEventHandler(SavedSearchesRetrievedEvent.SavedSearchesRetrievedEventHandler handler) {
        return folderRpcProxy.addSavedSearchedRetrievedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addSubmitDiskResourceQueryEventHandler(SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler handler) {
        return folderRpcProxy.addSubmitDiskResourceQueryEventHandler(handler);
    }

    @Override
    public NavigationView getView() {
        return view;
    }

    @Override
    public Folder getSelectedFolder() {
        return view.getTree().getSelectionModel().getSelectedItem();
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
    public Folder getParentFolder(Folder child) {
        return treeStore.getParent(child);
    }

    @Override
    public void onBeforeLoad(BeforeLoadEvent<FolderContentsLoadConfig> event) {
        if(getSelectedFolder() == null){
            return;
        }

        final Folder folderToBeLoaded = event.getLoadConfig().getFolder();

        // If the loaded contents are not the contents of the currently selected folder, then cancel the load.
        if(!folderToBeLoaded.getId().equals(getSelectedFolder().getId())){
            event.setCancelled(true);
        }
    }

    @Override
    public void refreshFolder(Folder folder) {
        if (folder == null
                || treeStore.findModel(folder) == null) {
            return;
        }

        // De-select and Refresh the given folder
        deSelectFolder(folder);
        removeChildren(folder);
        treeLoader.load(folder);

        if(!(folder instanceof DiskResourceQueryTemplate)) {
            // Re-select the folder to cause a selection changed event
            setSelectedFolder(folder);
        }
    }

    @Override
    public void removeFolder(Folder folder) {
        treeStore.remove(folder);
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
    public void setSelectedFolder(Folder folder) {
        if(folder == null){
            return;
        }
        final Folder findModelWithKey = treeStore.findModelWithKey(folder.getId());
        if (findModelWithKey != null) {
            view.getTree().getSelectionModel().deselectAll();
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    view.getTree().getSelectionModel().setSelection(Lists.newArrayList(findModelWithKey));
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
             * Trigger a selection changed event by deselecting the
             * current folder and re-selecting it.
             */
            deSelectAll();
            setSelectedFolder(folder);
        } else {
            // Create and add the SelectFolderByIdLoadHandler to the treeLoader.
            final SelectFolderByPathLoadHandler handler = new SelectFolderByPathLoadHandler(hasPath, this, maskable, announcer);
            /*
             * Only add handler if no root items have been loaded, or the hasPath has a common
             * root with the treestore.
             */
            if (treeStore.getRootCount() < 1 || handler.isRootFolderDetected()) {
                HandlerRegistration handlerRegistration = treeLoader.addLoadHandler(handler);
                handler.setHandlerRegistration(handlerRegistration);
            }
        }
    }

    @Override
    public void deSelectFolder(Folder folder) {
        view.getTree().getSelectionModel().deselect(folder);
    }

    @Override
    public boolean isLoaded(Folder folder) {
        return view.getTree().findNode(folder).isLoaded();
    }

    @Override
    public void expandFolder(Folder folder) {
        view.getTree().setExpanded(folder, true);
    }

    @Override
    public void deSelectAll() {
        view.getTree().getSelectionModel().deselectAll();
    }

    @Override
    public Tree.TreeNode<Folder> findTreeNode(Element el) {
        return view.getTree().findNode(el);
    }


    @Override
    public void removeChildren(Folder folder) {
        if (folder == null || treeStore.findModel(folder) == null) {
            return;
        }

        treeStore.removeChildren(folder);
        folder.setFolders(null);
    }

    @Override
    public void updateQueryTemplate(DiskResourceQueryTemplate queryTemplate) {
        Preconditions.checkNotNull(queryTemplate);

        if(treeStore.findModel(queryTemplate) == null){
            treeStore.add(queryTemplate);
        } else if(queryTemplate.isDirty()) {
            // Only update if it is dirty
            treeStore.update(queryTemplate);
        }
    }
}
