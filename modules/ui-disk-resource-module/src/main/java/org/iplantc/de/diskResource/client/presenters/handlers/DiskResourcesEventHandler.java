package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.events.diskResources.FolderRefreshEvent;
import org.iplantc.de.client.events.diskResources.FolderRefreshEvent.FolderRefreshEventHandler;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.events.DiskResourceRenamedEvent.DiskResourceRenamedEventHandler;
import org.iplantc.de.diskResource.client.events.DiskResourcesDeletedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent.DiskResourcesMovedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderCreatedEvent.FolderCreatedEventHandler;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import java.util.Collection;
import java.util.Set;

public final class DiskResourcesEventHandler implements DiskResourcesDeletedEvent.DiskResourcesDeletedEventHandler,
                                                        DiskResourcesMovedEventHandler,
                                                        DiskResourceRenamedEventHandler,
                                                        FolderCreatedEventHandler,
                                                        FolderRefreshEventHandler {
    final DiskResourceView.Presenter presenter;
    final DiskResourceView view;
    final DiskResourceUtil diskResourceUtil;

    public DiskResourcesEventHandler(final DiskResourceView.Presenter presenter) {
        this.presenter=presenter;
        this.view=presenter.getView();
        this.diskResourceUtil = DiskResourceUtil.getInstance();
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

        if (diskResourceUtil.isDescendantOfFolder(parentFolder, destinationFolder)) {
            // The destination is under the parent, so if we prune the parent and set the destination
            // as the selected folder, the parent will lazy-load down to the destination.
            view.removeChildren(parentFolder);
        } else if (diskResourceUtil.isDescendantOfFolder(destinationFolder, parentFolder)) {
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
        if (diskResourceUtil.containsFolder(resourcesToMove)) {
            // Refresh the destination folder, since it has gained a child.
            if (diskResourceUtil.isDescendantOfFolder(destinationFolder, selectedFolder)) {
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
        Folder parent = view.getFolderByPath(diskResourceUtil.parseParent(newDr.getPath()));
        if (parent != null) {
            presenter.doRefreshFolder(parent);
        }
    }

    @Override
    public void onFolderCreated(Folder parentFolder, Folder newFolder) {
        view.addFolder(parentFolder, newFolder);
    }

}
