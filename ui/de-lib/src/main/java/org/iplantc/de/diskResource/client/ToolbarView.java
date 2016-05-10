package org.iplantc.de.diskResource.client;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent.FolderSelectionEventHandler;
import org.iplantc.de.diskResource.client.events.selection.CopyMetadataSelected.HasCopyMetadataSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.DeleteDiskResourcesSelected.HasDeleteDiskResourcesSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected.HasEditInfoTypeSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.EmptyTrashSelected.HasEmptyTrashSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.ImportFromUrlSelected.HasImportFromUrlSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelected.HasManageCommentsSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelected.HasManageMetadataSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected.HasManageSharingSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.MoveDiskResourcesSelected.HasMoveDiskResourcesSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.RefreshFolderSelected.HasRefreshFolderSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.RenameDiskResourceSelected.HasRenameDiskResourceSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.RestoreDiskResourcesSelected.HasRestoreDiskResourceSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SaveMetadataSelected.HasSaveMetadataSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.SendToCogeSelected.HasSendToCogeSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SendToEnsemblSelected.HasSendToEnsemblSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SendToTreeViewerSelected.HasSendToTreeViewerSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelected.HasShareByDataLinkSelectedEventHandlers;
import org.iplantc.de.diskResource.client.events.selection.SimpleDownloadSelected.HasSimpleDownloadSelectedHandlers;
import org.iplantc.de.diskResource.client.events.selection.SimpleUploadSelected.HasSimpleUploadSelectedHandlers;
import org.iplantc.de.diskResource.client.views.dialogs.BulkMetadataDialog;
import org.iplantc.de.diskResource.client.views.dialogs.BulkMetadataDialog.BULK_MODE;
import org.iplantc.de.diskResource.client.views.dialogs.GenomeSearchDialog;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface ToolbarView extends IsWidget,
                                     HasManageCommentsSelectedEventHandlers,
                                     HasManageMetadataSelectedEventHandlers,
                                     HasCopyMetadataSelectedEventHandlers,
                                     HasSaveMetadataSelectedEventHandlers,
                                     HasManageSharingSelectedEventHandlers,
                                     HasShareByDataLinkSelectedEventHandlers,
                                     HasSendToEnsemblSelectedHandlers,
                                     HasSendToCogeSelectedHandlers,
                                     HasSendToTreeViewerSelectedHandlers,
                                     HasDeleteDiskResourcesSelectedEventHandlers,
                                     HasEditInfoTypeSelectedEventHandlers,
                                     HasEmptyTrashSelectedHandlers,
                                     HasMoveDiskResourcesSelectedHandlers,
                                     HasRefreshFolderSelectedHandlers,
                                     HasRenameDiskResourceSelectedHandlers,
                                     HasRestoreDiskResourceSelectedHandlers,
                                     HasSimpleUploadSelectedHandlers,
                                     HasSimpleDownloadSelectedHandlers,
                                     HasImportFromUrlSelectedHandlers,
                                     FolderSelectionEventHandler,
                                     DiskResourceSelectionChangedEventHandler {
    interface Appearance {

        SafeHtml bulkDownloadInfoBoxHeading();

        SafeHtml bulkDownloadInfoBoxMsg();

        SafeHtml bulkUploadInfoBoxHeading();

        SafeHtml bulkUploadInfoBoxMsg();

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

        String editViewMetadataMenuItem();

        String copyMetadataMenuItem();

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

        ImageResource sendNcbiSraIcon();

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

        String saveMetadataMenuItem();
        
        String sendToNcbiSraItem();

        String importFromCoge();

        String applyBulkMetadata();

        String selectMetadata();

        String requestDOI();

        String doiLinkMsg();

        String needDOI();
    }

    interface Presenter {

        interface Appearance {

            String createDelimitedFileDialogHeight();

            String createDelimitedFileDialogWidth();

            String done();

            String emptyTrash();

            String emptyTrashWarning();

            String manageDataLinks();

            int manageDataLinksDialogHeight();

            int manageDataLinksDialogWidth();

            String manageDataLinksHelp();

            String cogeSearchError();

            String cogeImportGenomeError();

            String cogeImportGenomeSucess();

            String importFromCoge();

            String bulkMetadataError();

            String bulkMetadataSuccess();

            String templatesError();

            String applyBulkMetadata();

            String overWiteMetadata();

            String doiRequestFail();

            String doiRequestSuccess();
        }

        ToolbarView getView();

        void onCreateNewDelimitedFileSelected();

        void onCreateNewFileSelected(Folder selectedFolder, MimeType mimeType);

        void onCreateNewFolderSelected(Folder selectedFolder);

        void onCreateNcbiSraFolderStructure(Folder selectedFolder);

        void onCreateNewPathListSelected();

        void onCreatePublicLinkSelected(List<DiskResource> selectedDiskResources);

        void onEditFileSelected(List<DiskResource> selectedDiskResources);

        void onOpenNewWindowAtLocationSelected(Folder selectedFolder);

        void onOpenNewWindowSelected();

        void onOpenTrashFolderSelected();

        void onImportFromCoge();

        void searchGenomeInCoge(String searchTerm);

        void importGenomeFromCoge(Integer id);

        void onBulkMetadataSelected(BULK_MODE mode);

        void submitBulkMetadataFromExistingFile(String filePath,
                                                String destFolder,
                                                String templateId,
                                                boolean force);

        void onDoiRequest(String uuid);

    }

    DiskResourceSearchField getSearchField();

    void maskSendToCoGe();

    void maskSendToEnsembl();

    void maskSendToTreeViewer();

    void unmaskSendToCoGe();

    void unmaskSendToEnsembl();

    void unmaskSendToTreeViewer();

    void openViewForGenomeSearch(GenomeSearchDialog view);

    void openViewBulkMetadata(BulkMetadataDialog bmd);
}
