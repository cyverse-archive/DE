package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceViewToolbar;

import java.util.List;

/**
 * A ToolbarButtonVisibilitySelectionHandler for DiskResource selections in the Data Window main grid.
 * 
 * @author psarando
 * 
 */
public class ToolbarButtonVisibilityGridHandler extends
        ToolbarButtonVisibilitySelectionHandler<DiskResource> {

    public ToolbarButtonVisibilityGridHandler(DiskResourceViewToolbar toolbar) {
        super(toolbar);
    }

    @Override
    protected void updateToolbar(List<DiskResource> selection) {
        boolean selectionEmpty = selection.isEmpty();
        boolean oneSelected = selection.size() == 1;
        boolean selectionInTrash = selectionInTrash(selection);
        boolean owner = !selectionEmpty && DiskResourceUtil.isOwner(selection);

        boolean canDownload = !selectionEmpty;
        boolean canSimpleDownload = canDownload && !DiskResourceUtil.containsFolder(selection);
        boolean canRename = oneSelected && owner && !selectionInTrash;
        boolean canShare = !selectionInTrash && DiskResourceUtil.hasOwner(selection);
        boolean canShareDataLink = canShare && DiskResourceUtil.containsFile(selection);
        boolean canViewMetadata = oneSelected && selection.get(0).getPermissions().isReadable()
                && !selectionInTrash;
        boolean canDelete = owner;
        boolean canEdit = canRename || canDelete || canViewMetadata;
        boolean canMove = owner && !selectionInTrash;

        toolbar.setDownloadsEnabled(canDownload);
        toolbar.setBulkDownloadButtonEnabled(canDownload);
        toolbar.setSimpleDowloadButtonEnabled(canSimpleDownload);
        toolbar.setRenameButtonEnabled(canRename);
        toolbar.setDeleteButtonEnabled(canDelete);
        toolbar.setShareButtonEnabled(canShare);
        toolbar.setShareMenuItemEnabled(canShare);
        toolbar.setDataLinkMenuItemEnabled(canShareDataLink);
        toolbar.setRestoreMenuItemEnabled(selectionInTrash);
        toolbar.setMetaDatMenuItemEnabled(canViewMetadata);
        toolbar.setEditEnabled(canEdit);
        toolbar.setMoveButtonEnabled(canMove);
    }

    /**
     * Check if every selected item is under the Trash folder.
     * 
     * @param selection
     * @return
     */
    private boolean selectionInTrash(List<DiskResource> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        String trashPath = UserInfo.getInstance().getTrashPath();
        for (DiskResource dr : selection) {
            if (dr.getId().equals(trashPath)) {
                return false;
            }

            if (!dr.getId().startsWith(trashPath)) {
                return false;
            }
        }

        return true;
    }
}
