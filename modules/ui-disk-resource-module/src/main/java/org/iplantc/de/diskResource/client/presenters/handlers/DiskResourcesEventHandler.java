package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent.FolderRefreshEventHandler;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.events.DiskResourceRenamedEvent.DiskResourceRenamedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectedEvent.DiskResourceSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent.DiskResourcesMovedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderCreatedEvent.FolderCreatedEventHandler;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent;
import org.iplantc.de.diskResource.client.search.events.UpdateSavedSearchesEvent.UpdateSavedSearchesHandler;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public final class DiskResourcesEventHandler implements
        DiskResourcesDeletedEvent.DiskResourcesDeletedEventHandler, DiskResourceSelectedEventHandler,
        DiskResourcesMovedEventHandler, DiskResourceRenamedEventHandler, FolderCreatedEventHandler,
        FolderRefreshEventHandler, UpdateSavedSearchesHandler {
    private final DiskResourceView.Presenter presenter;
    private final DiskResourceView view;

    public DiskResourcesEventHandler(DiskResourceView.Presenter presenter) {
        this.presenter = presenter;
        this.view = presenter.getView();
    }

    @Override
    public void onRefresh(FolderRefreshEvent event) {
        presenter.onFolderRefresh(event.getFolder());
    }

    @Override
    public void onDiskResourcesDeleted(Collection<DiskResource> resources, Folder parentFolder) {
        presenter.doRefresh(parentFolder);
    }

    @Override
    public void onSelect(DiskResourceSelectedEvent event) {
        if (event.getSource() != view) {
            return;
        }

        if (event.getSelectedItem() instanceof Folder) {
            presenter.setSelectedFolderByPath(event.getSelectedItem());
        } else if (event.getSelectedItem() instanceof File) {
            EventBus.getInstance().fireEvent(
                    new ShowFilePreviewEvent((File)event.getSelectedItem(), this));
        }
    }

    @Override
    public void onDiskResourcesMoved(DiskResourcesMovedEvent event) {
        Set<DiskResource> resourcesToMove = event.getResourcesToMove();
        Folder destinationFolder = event.getDestinationFolder();
        Folder selectedFolder = presenter.getSelectedFolder();
        // moved contents only not the folder itself
        if (event.isMoveContents()) {
            presenter.doRefresh(destinationFolder);
            presenter.doRefresh(selectedFolder);
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
            presenter.doRefresh(destinationFolder);
        } else {
            // Refresh the parent folder since it has lost a child.
            presenter.doRefresh(parentFolder);
            // Refresh the destination folder since it has gained a child.
            presenter.doRefresh(destinationFolder);
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
                presenter.doRefresh(selectedFolder);
                // Refresh the destination folder since it has gained a child.
                presenter.doRefresh(destinationFolder);
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
            presenter.doRefresh(parent);
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

}
