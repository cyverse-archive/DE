package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent.FolderRefreshEventHandler;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.events.DiskResourceRenamedEvent.DiskResourceRenamedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent.DiskResourcesMovedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderCreatedEvent.FolderCreatedEventHandler;
import org.iplantc.de.diskResource.client.events.RequestAttachDiskResourceFavoritesFolderEvent;
import org.iplantc.de.diskResource.client.events.RequestAttachDiskResourceFavoritesFolderEvent.RequestAttachDiskResourceFavoritesFolderEventHandler;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent.UpdateSavedSearchesHandler;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class DiskResourcesEventHandler implements DiskResourcesDeletedEvent.DiskResourcesDeletedEventHandler,
                                                        DiskResourcesMovedEventHandler,
                                                        DiskResourceRenamedEventHandler,
                                                        FolderCreatedEventHandler,
                                                        FolderRefreshEventHandler,
 UpdateSavedSearchesHandler, RequestAttachDiskResourceFavoritesFolderEventHandler {
    private final DiskResourceView.Presenter presenter;
    private final DiskResourceView view;
    private final DiskResourceAutoBeanFactory factory;

    public DiskResourcesEventHandler(DiskResourceView.Presenter presenter, DiskResourceAutoBeanFactory factory) {
        this.presenter = presenter;
        this.view = presenter.getView();
        this.factory = factory;
    }

    @Override
    public void onRequestFolderRefresh(FolderRefreshEvent event) {
        presenter.doRefreshFolder(event.getFolder());
    }

    @Override
    public void onDiskResourcesDeleted(Collection<DiskResource> resources, Folder parentFolder) {
        presenter.doRefreshFolder(parentFolder);
    }

    @Override
    public void onDiskResourcesMoved(DiskResourcesMovedEvent event) {
        Set<DiskResource> resourcesToMove = event.getResourcesToMove();
        Folder destinationFolder = event.getDestinationFolder();
        Folder selectedFolder = presenter.getSelectedFolder();
        // moved contents only not the folder itself
        if (event.isMoveContents()) {
            presenter.doRefreshFolder(destinationFolder);
            presenter.doRefreshFolder(selectedFolder);
            presenter.refreshSelectedFolder();
        } else {

            if (resourcesToMove.contains(selectedFolder)) {
                selectedFolderMovedFromNavTree(selectedFolder, destinationFolder);
            } else {
                diskResourcesMovedFromGrid(resourcesToMove, selectedFolder, destinationFolder);
            }
        }
    }

    private void selectedFolderMovedFromNavTree(Folder selectedFolder, Folder destinationFolder) {
        // If the selected folder happens to be one of the moved items, then view the destination by
        // setting it as the selected folder.
        Folder parentFolder = view.getParentFolder(selectedFolder);

        if (DiskResourceUtil.isDescendantOfFolder(parentFolder, destinationFolder)) {
            // The destination is under the parent, so if we prune the parent and set the destination
            // as the selected folder, the parent will lazy-load down to the destination.
            view.removeChildren(parentFolder);
        } else if (DiskResourceUtil.isDescendantOfFolder(destinationFolder, parentFolder)) {
            // The parent is under the destination, so we only need to view the destination folder's
            // contents and refresh its children.
            presenter.doRefreshFolder(destinationFolder);
        } else {
            // Refresh the parent folder since it has lost a child.
            presenter.doRefreshFolder(parentFolder);
            // Refresh the destination folder since it has gained a child.
            presenter.doRefreshFolder(destinationFolder);
        }

        // View the destination folder's contents.
        presenter.setSelectedFolderByPath(destinationFolder);
    }

    private void diskResourcesMovedFromGrid(Set<DiskResource> resourcesToMove, Folder selectedFolder,
            Folder destinationFolder) {
        if (DiskResourceUtil.containsFolder(resourcesToMove)) {
            // Refresh the destination folder, since it has gained a child.
            if (DiskResourceUtil.isDescendantOfFolder(destinationFolder, selectedFolder)) {
                view.removeChildren(destinationFolder);
            } else {
                // Refresh the selected folder since it has lost a child. This will also reload the
                // selected folder's contents in the grid.
                presenter.doRefreshFolder(selectedFolder);
                // Refresh the destination folder since it has gained a child.
                presenter.doRefreshFolder(destinationFolder);
                return;
            }
        }

        // Refresh the selected folder's contents.
        presenter.setSelectedFolderByPath(selectedFolder);
    }

    @Override
    public void onRename(DiskResource originalDr, DiskResource newDr) {
        Folder parent = view.getFolderByPath(DiskResourceUtil.parseParent(newDr.getPath()));
        if (parent != null) {
            presenter.doRefreshFolder(parent);
        }
    }

    @Override
    public void onFolderCreated(Folder parentFolder, Folder newFolder) {
        view.addFolder(parentFolder, newFolder);
    }

    /**
     * Ensures that the navigation window shows the given templates. These show up in the navigation
     * window as "magic folders".
     * <p/>
     * This method ensures that the only the given list of queryTemplates will be displayed in the
     * navigation pane.
     * 
     * 
     * Only objects which are instances of {@link DiskResourceQueryTemplate} will be operated on. Items
     * which can't be found in the tree store will be added, and items which are already in the store and
     * are marked as dirty will be updated.
     * 
     * @param event
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

    @Override
    public void onRequest(RequestAttachDiskResourceFavoritesFolderEvent event) {
        TreeStore<Folder> treeStore = view.getTreeStore();
        AutoBean<DiskResourceFavorite> bean = AutoBeanCodex.decode(factory, DiskResourceFavorite.class, "{}");

        DiskResourceFavorite as = bean.as();
        String id = UserInfo.getInstance().getHomePath() + "/favorites";
        as.setId(id);
        as.setPath(id);
        as.setName("Favorites");
        treeStore.add(as);

    }

}
