package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.selection.*;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface ToolbarView extends IsWidget,
                                     ManageCommentsSelected.HasManageCommentsSelectedEventHandlers,
                                     ManageMetadataSelected.HasManageMetadataSelectedEventHandlers,
                                     ManageSharingSelected.HasManageSharingSelectedEventHandlers,
                                     ShareByDataLinkSelected.HasShareByDataLinkSelectedEventHandlers,
                                     SendToEnsemblSelected.HasSendToEnsemblSelectedHandlers,
                                     SendToCogeSelected.HasSendToCogeSelectedHandlers,
                                     SendToTreeViewerSelected.HasSendToTreeViewerSelectedHandlers,
                                     BulkDownloadSelected.HasBulkDownloadSelectedEventHandlers,
                                     BulkUploadSelected.HasBulkUploadSelectedEventHandlers,
                                     DeleteDiskResourcesSelected.HasDeleteDiskResourcesSelectedEventHandlers,
                                     EditInfoTypeSelected.HasEditInfoTypeSelectedEventHandlers,
                                     EmptyTrashSelected.HasEmptyTrashSelectedHandlers,
                                     MoveDiskResourcesSelected.HasMoveDiskResourcesSelectedHandlers,
                                     RefreshFolderSelected.HasRefreshFolderSelectedHandlers,
                                     RenameDiskResourceSelected.HasRenameDiskResourceSelectedHandlers,
                                     RestoreDiskResourcesSelected.HasRestoreDiskResourceSelectedHandlers,
                                     SimpleUploadSelected.HasSimpleUploadSelectedHandlers,
                                     SimpleDownloadSelected.HasSimpleDownloadSelectedHandlers,
                                     FolderSelectionEvent.FolderSelectionEventHandler,
                                     DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler {
    interface Appearance {

        String newPathListMenuText();

        ImageResource newPathListMenuIcon();

        ImageResource trashIcon();

        String moveToTrashMenuItem();

        ImageResource newMdFileIcon();

        ImageResource newShellFileIcon();

        ImageResource newPythonFileIcon();

        ImageResource newPerlFileIcon();

        ImageResource newRFileIcon();

        ImageResource newDelimitedFileIcon();

        String newTabularDataFileMenuItem();

        ImageResource newPlainTexFileIcon();

        String newPlainTextFileMenuItem();

        ImageResource newFileMenuIcon();

        String newFileMenu();

        String duplicateMenuItem();

        ImageResource newFolderIcon();

        String newFolderMenuItem();

        String newDataWindowAtLocMenuItem();

        ImageResource addIcon();

        String newWindow();

        ImageResource importDataIcon();

        String importFromUrlMenuItem();

        String bulkUploadFromDesktop();

        String simpleUploadFromDesktop();

        String uploadMenu();

        String editMenu();

        String renameMenuItem();

        ImageResource fileRenameIcon();

        String editFileMenuItem();

        String editCommentsMenuItem();

        ImageResource userCommentIcon();

        String editInfoTypeMenuItem();

        ImageResource infoIcon();

        String metadataMenuItem();

        ImageResource metadataIcon();

        String moveMenuItem();

        ImageResource editIcon();

        String downloadMenu();

        String simpleDownloadMenuItem();

        ImageResource downloadIcon();

        String bulkDownloadMenuItem();

        String shareMenu();

        String shareWithCollaboratorsMenuItem();

        ImageResource shareWithCollaboratorsIcon();

        String createPublicLinkMenuItem();

        ImageResource linkAddIcon();

        String shareFolderLocationMenuItem();

        ImageResource shareFolderLocationIcon();

        String sendToCogeMenuItem();

        ImageResource sendToCogeIcon();

        String sendToEnsemblMenuItem();

        ImageResource sendToEnsemblIcon();

        String sendToTreeViewerMenuItem();

        ImageResource sendToTreeViewerIcon();

        String refresh();

        String trashMenu();

        String openTrashMenuItem();

        ImageResource openTrashIcon();

        String restore();

        String emptyTrashMenuItem();

        ImageResource emptyTrashIcon();

        String deleteMenuItem();

        ImageResource deleteIcon();

        ImageResource refreshIcon();

        String newRFileMenuItem();

        String newPerlFileMenuItem();

        String newPythonFileMenuItem();

        String newShellFileMenuItem();

        String newMdFileMenuItem();
    }

    interface Presenter {

        interface Appearance {

            String createDelimitedFileDialogHeight();

            String createDelimitedFileDialogWidth();

            String done();

            String emptyTrash();

            String emptyTrashWarning();

            String manageDataLinks();

            int manageDataLinksDialogWidth();

            String manageDataLinksHelp();
        }

        ToolbarView getView();

        void onCreateNewDelimitedFileSelected();

        void onCreateNewFileSelected(Folder selectedFolder, MimeType mimeType);

        void onCreateNewFolderSelected(Folder selectedFolder);

        void onCreateNewPathListSelected();

        void onCreatePublicLinkSelected(List<DiskResource> selectedDiskResources);

        void onEditFileSelected(List<DiskResource> selectedDiskResources);

        void onImportFromUrlSelected(Folder selectedFolder);

        void onOpenNewWindowAtLocationSelected(Folder selectedFolder);

        void onOpenNewWindowSelected();

        void onOpenTrashFolderSelected();
    }

    DiskResourceSearchField getSearchField();

    void maskSendToCoGe();

    void maskSendToEnsembl();

    void maskSendToTreeViewer();

    void unmaskSendToCoGe();

    void unmaskSendToEnsembl();

    void unmaskSendToTreeViewer();
}
