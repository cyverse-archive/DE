package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent.FolderRefreshEventHandler;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.DiskResourceRenamedEvent.DiskResourceRenamedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent.DiskResourcesMovedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderCreatedEvent.FolderCreatedEventHandler;

import java.util.Collection;
import java.util.List;

/**
 * @author jstroot
 */
public final class DiskResourcesEventHandler implements DiskResourcesDeletedEvent.DiskResourcesDeletedEventHandler,
                                                        DiskResourcesMovedEventHandler,
                                                        DiskResourceRenamedEventHandler,
                                                        FolderCreatedEventHandler,
                                                        FolderRefreshEventHandler {
    final NavigationView.Presenter navigationPresenter;
    final DiskResourceUtil diskResourceUtil;

    public DiskResourcesEventHandler(final NavigationView.Presenter navigationPresenter) {
        this.navigationPresenter = navigationPresenter;
        this.diskResourceUtil = DiskResourceUtil.getInstance();
    }

    @Override
    public void onRequestFolderRefresh(FolderRefreshEvent event) {
        navigationPresenter.refreshFolder(event.getFolder());
    }

    @Override
    public void onDiskResourcesDeleted(Collection<DiskResource> resources, Folder parentFolder) {
        navigationPresenter.refreshFolder(parentFolder);
    }

    @Override
    public void onDiskResourcesMoved(DiskResourcesMovedEvent event) {
        List<DiskResource> resourcesToMove = event.getResourcesToMove();
        Folder destinationFolder = event.getDestinationFolder();
        Folder selectedFolder = navigationPresenter.getSelectedFolder();
        // moved contents only not the folder itself
        if (event.isMoveContents()) {
            navigationPresenter.refreshFolder(destinationFolder);
            navigationPresenter.refreshFolder(selectedFolder);
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
        Folder parentFolder = navigationPresenter.getParentFolder(selectedFolder);

        if (diskResourceUtil.isDescendantOfFolder(parentFolder, destinationFolder)) {
            // The destination is under the parent, so if we prune the parent and set the destination
            // as the selected folder, the parent will lazy-load down to the destination.
            navigationPresenter.removeChildren(parentFolder);
        } else if (diskResourceUtil.isDescendantOfFolder(destinationFolder, parentFolder)) {
            // The parent is under the destination, so we only need to view the destination folder's
            // contents and refresh its children.
            navigationPresenter.refreshFolder(destinationFolder);
        } else {
            // Refresh the parent folder since it has lost a child.
            navigationPresenter.refreshFolder(parentFolder);
            // Refresh the destination folder since it has gained a child.
            navigationPresenter.refreshFolder(destinationFolder);
        }

        // View the destination folder's contents.
        navigationPresenter.setSelectedFolder((HasPath)destinationFolder);
    }

    private void diskResourcesMovedFromGrid(List<DiskResource> resourcesToMove, Folder selectedFolder,
            Folder destinationFolder) {
        if (diskResourceUtil.containsFolder(resourcesToMove)) {
            // Refresh the destination folder, since it has gained a child.
            if (diskResourceUtil.isDescendantOfFolder(destinationFolder, selectedFolder)) {
                navigationPresenter.removeChildren(destinationFolder);
            } else {
                // Refresh the selected folder since it has lost a child. This will also reload the
                // selected folder's contents in the grid.
                navigationPresenter.refreshFolder(selectedFolder);
                // Refresh the destination folder since it has gained a child.
                navigationPresenter.refreshFolder(destinationFolder);
                return;
            }
        }

        // Refresh the selected folder's contents.
        navigationPresenter.setSelectedFolder((HasPath)selectedFolder);
    }

    @Override
    public void onRename(DiskResource originalDr, DiskResource newDr) {
        Folder parent = navigationPresenter.getFolderByPath(diskResourceUtil.parseParent(newDr.getPath()));
        if (parent != null) {
            navigationPresenter.refreshFolder(parent);
        }
    }

    @Override
    public void onFolderCreated(Folder parentFolder, Folder newFolder) {
        navigationPresenter.addFolder(parentFolder, newFolder);
    }

}
