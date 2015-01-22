package org.iplantc.de.diskResource.client.presenters.navigation;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.gin.factory.FolderRpcProxyFactory;
import org.iplantc.de.diskResource.client.gin.factory.NavigationViewFactory;
import org.iplantc.de.diskResource.client.presenters.handlers.CachedFolderTreeStoreBinding;
import org.iplantc.de.diskResource.client.NavigationView;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * @author jstroot
 */
public class NavigationPresenterImpl implements NavigationView.Presenter {

    private static class FolderStoreDataChangeHandler implements StoreDataChangeEvent.StoreDataChangeHandler<Folder> {
        private final Tree<Folder, Folder> tree;

        public FolderStoreDataChangeHandler(Tree<Folder, Folder> tree) {
            this.tree = tree;
        }

        @Override
        public void onDataChange(StoreDataChangeEvent<Folder> event) {
            Folder folder = event.getParent();
            if (folder != null && tree.getStore().getAllChildren(folder) != null) {
                for (Folder f : tree.getStore().getAllChildren(folder)) {
                    if (f.isFilter()) {
                        Tree.TreeNode<Folder> tn = tree.findNode(f);
                        tree.getView().getTextElement(tn).setInnerHTML("<span style='color:red;font-style:italic;'>" + f.getName() + "</span>");
                    }
                }
            }
        }
    }

    private final DiskResourceView.FolderRpcProxy folderRpcProxy;
    private final TreeLoader<Folder> treeLoader;
    private final TreeStore<Folder> treeStore;
    private final NavigationView view;

    @Inject
    DiskResourceAutoBeanFactory factory;
    @Inject
    UserInfo userInfo;

    @Inject
    NavigationPresenterImpl(final NavigationViewFactory viewFactory,
                            final TreeStore<Folder> treeStore,
                            final FolderRpcProxyFactory folderRpcProxyFactory) {
        this.treeStore = treeStore;
        this.folderRpcProxy = folderRpcProxyFactory.create(null); // FIXME Need to figure out maskable
        treeLoader = new TreeLoader<Folder>(folderRpcProxy) {
            @Override
            public boolean hasChildren(Folder parent) {
                return parent.hasSubDirs();
            }
        };
        view = viewFactory.create(treeStore, treeLoader);


        this.treeStore.addStoreDataChangeHandler(new FolderStoreDataChangeHandler(view.getTree()));
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
        if (getSelectedFolder() != null) {
//            listStore.add(newChild);
//            gridView.refresh();
        } else {
            Folder request = factory.folder().as();
            request.setPath(userInfo.getHomePath());
            // Set selected folder by path
            setSelectedFolder((HasPath) request);
        }
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
    public Folder getFolderById(HasId hasId) {
        // KLUDGE Until the services are able to use GUIDs for folder IDs, first
        // check for a root folder
        // whose path matches folderId, since a root folder may now also be
        // listed under another root
        // (such as the user's home folder listed under "Shared With Me").
        if (treeStore.getRootItems() != null) {
            for (Folder root : treeStore.getRootItems()) {
                if (root.getPath().equals(hasId.getId())) {
                    return root;
                }
            }
        }

        return treeStore.findModelWithKey(hasId.getId());
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
    public void refreshFolder(Folder folder) {
        if (folder == null || treeStore.findModel(folder) == null) {
            return;
        }

        removeChildren(folder);
        treeLoader.load(folder);
    }

    @Override
    public void removeFolder(Folder folder) {
        treeStore.remove(folder);
    }

    @Override
    public void setSelectedFolder(Folder folder) {
        final Folder findModelWithKey = treeStore.findModelWithKey(folder.getId());
        if (findModelWithKey != null) {
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
        // FIXME See DiskresourcePresenterImpl.setSelectedFolderByPath
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
