package org.iplantc.de.diskResource.client.presenters.handlers;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.search.events.SubmitDiskResourceQueryEvent.SubmitDiskResourceQueryEventHandler;
import org.iplantc.de.diskResource.client.views.widgets.DiskResourceViewToolbar;

import java.util.List;

/**
 * A ToolbarButtonVisibilitySelectionHandler for Folder selections in the Data Window Navigation panel.
 * 
 * @author psarando
 * 
 */
public class ToolbarButtonVisibilityNavigationHandler extends
        ToolbarButtonVisibilitySelectionHandler<Folder> implements SubmitDiskResourceQueryEventHandler {

    public ToolbarButtonVisibilityNavigationHandler(DiskResourceViewToolbar toolbar) {
        super(toolbar);
    }

    @Override
    protected void updateToolbar(List<Folder> selection) {
        boolean oneSelected = selection.size() == 1;
        boolean canUpload = oneSelected && DiskResourceUtil.canUploadTo(selection.get(0));
        boolean newFolderEnabled = canUpload;

        toolbar.setUploadsEnabled(canUpload);
        toolbar.setBulkUploadEnabled(canUpload);
        toolbar.setSimpleUploadEnabled(canUpload);
        toolbar.setImportButtonEnabled(canUpload);

        toolbar.setNewButtonEnabled(newFolderEnabled);
        toolbar.setNewFileButtonEnabled(newFolderEnabled);
        toolbar.setNewFolderButtonEnabled(newFolderEnabled);
        toolbar.setRefreshButtonEnabled(oneSelected);
    }

    @Override
    public void doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent event) {
        toolbar.setRefreshButtonEnabled(event.getQueryTemplate() != null);
    }
}
