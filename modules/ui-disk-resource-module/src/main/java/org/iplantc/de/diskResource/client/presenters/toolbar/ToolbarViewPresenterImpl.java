package org.iplantc.de.diskResource.client.presenters.toolbar;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.de.diskResource.client.views.dialogs.CreateFolderDialog;

import com.google.inject.Inject;

import java.util.List;

/**
 * @author jstroot
 */
public class ToolbarViewPresenterImpl implements ToolbarView.Presenter {

    @Inject EventBus eventBus;

    @Inject
    ToolbarViewPresenterImpl() {

    }

    @Override
    public void onBulkUploadSelected() {

    }

    @Override
    public void onCreateNewDelimitedFileSelected() {

    }

    @Override
    public void onCreateNewFileSelected(Folder selectedFolder, MimeType mimeType) {
        FileViewerWindowConfig config = ConfigFactory.fileViewerWindowConfig(null);
        config.setEditing(true);
        config.setParentFolder(selectedFolder);
        eventBus.fireEvent(new CreateNewFileEvent(config));
    }

    @Override
    public void onCreateNewFolderSelected(Folder selectedFolder) {
        // FIXME Update dialog
        CreateFolderDialog dlg = new CreateFolderDialog(selectedFolder, this);
        dlg.show();
    }

    @Override
    public void onCreateNewPathListSelected() {

    }

    @Override
    public void onCreatePublicLinkSelected() {

    }

    @Override
    public void onDeleteResourcesSelected() {

    }

    @Override
    public void onEditFileSelected() {

    }

    @Override
    public void onEditInfoTypeSelected() {

    }

    @Override
    public void onEmptyTrashSelected() {

    }

    @Override
    public void onImportFromUrlSelected() {

    }

    @Override
    public void onMoveDiskResourcesSelected() {

    }

    @Override
    public void onMoveToTrashSelected() {

    }

    @Override
    public void onOpenNewWindowAtLocationSelected() {

    }

    @Override
    public void onOpenNewWindowSelected() {

    }

    @Override
    public void onOpenTrashFolderSelected() {

    }

    @Override
    public void onRefreshFolderSelected() {

    }

    @Override
    public void onRenameResourceSelected() {

    }

    @Override
    public void onRestoreResourcesSelected() {

    }

    @Override
    public void onSimpleDownloadSelected(Folder selectedFolder,
                                         List<DiskResource> selectedDiskResources) {
        eventBus.fireEvent(new RequestSimpleDownloadEvent(this,
                                                          selectedDiskResources,
                                                          selectedFolder));
    }

    @Override
    public void onSimpleUploadSelected() {

    }
}
