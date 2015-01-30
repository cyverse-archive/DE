package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelectedEvent;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelectedEvent;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelectedEvent;
import org.iplantc.de.diskResource.client.events.selection.SendToCogeSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToEnsemblSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToTreeViewerSelected;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelectedEvent;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

public interface ToolbarView extends IsWidget,
                                     ManageCommentsSelectedEvent.HasManageCommentsSelectedEventHandlers,
                                     ManageMetadataSelectedEvent.HasManageMetadataSelectedEventHandlers,
                                     ManageSharingSelectedEvent.HasManageSharingSelectedEventHandlers,
                                     ShareByDataLinkSelectedEvent.HasShareByDataLinkSelectedEventHandlers,
                                     SendToEnsemblSelected.HasSendToEnsemblSelectedHandlers,
                                     SendToCogeSelected.HasSendToCogeSelectedHandlers,
                                     SendToTreeViewerSelected.HasSendToTreeViewerSelectedHandlers
{
    interface Appearance {

        String newPathListMenuText();

        ImageResource newPathListMenuIcon();
    }

    interface Presenter {

        void onBulkUploadSelected();

        void onCreateNewDelimitedFileSelected();

        void onCreateNewFileSelected(Folder selectedFolder, MimeType mimeType);

        void onCreateNewFolderSelected(Folder selectedFolder);

        void onCreateNewPathListSelected();

        void onCreatePublicLinkSelected();

        void onDeleteResourcesSelected();

        void onEditFileSelected();

        void onEditInfoTypeSelected();

        void onEmptyTrashSelected();

        void onImportFromUrlSelected();

        void onMoveDiskResourcesSelected();

        void onMoveToTrashSelected();

        void onOpenNewWindowAtLocationSelected();

        void onOpenNewWindowSelected();

        void onOpenTrashFolderSelected();

        void onRefreshFolderSelected();

        void onRenameResourceSelected();

        void onRestoreResourcesSelected();

        void onSimpleDownloadSelected(Folder selectedFolder,
                                      List<DiskResource> selectedDiskResources);

        void onSimpleUploadSelected();
    }

    DiskResourceSearchField getSearchField();

//    void init(DiskResourceView.Presenter presenter, DiskResourceView view);

    void maskSendToCoGe();

    void maskSendToEnsembl();

    void maskSendToTreeViewer();

    void unmaskSendToCoGe();

    void unmaskSendToEnsembl();

    void unmaskSendToTreeViewer();
}
