package org.iplantc.de.diskResource.client.views.toolbar;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.MimeType;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.ToolbarView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.selection.CopyMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.DeleteDiskResourcesSelected;
import org.iplantc.de.diskResource.client.events.selection.EditInfoTypeSelected;
import org.iplantc.de.diskResource.client.events.selection.EmptyTrashSelected;
import org.iplantc.de.diskResource.client.events.selection.ImportFromUrlSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageCommentsSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.ManageSharingSelected;
import org.iplantc.de.diskResource.client.events.selection.MoveDiskResourcesSelected;
import org.iplantc.de.diskResource.client.events.selection.RefreshFolderSelected;
import org.iplantc.de.diskResource.client.events.selection.RenameDiskResourceSelected;
import org.iplantc.de.diskResource.client.events.selection.RestoreDiskResourcesSelected;
import org.iplantc.de.diskResource.client.events.selection.SaveMetadataSelected;
import org.iplantc.de.diskResource.client.events.selection.SaveMetadataSelected.SaveMetadataSelectedEventHandler;
import org.iplantc.de.diskResource.client.events.selection.SendToCogeSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToEnsemblSelected;
import org.iplantc.de.diskResource.client.events.selection.SendToTreeViewerSelected;
import org.iplantc.de.diskResource.client.events.selection.ShareByDataLinkSelected;
import org.iplantc.de.diskResource.client.events.selection.SimpleDownloadSelected;
import org.iplantc.de.diskResource.client.events.selection.SimpleUploadSelected;
import org.iplantc.de.diskResource.client.views.dialogs.BulkMetadataDialog;
import org.iplantc.de.diskResource.client.views.dialogs.GenomeSearchDialog;
import org.iplantc.de.diskResource.client.views.search.DiskResourceSearchField;
import org.iplantc.de.diskResource.share.DiskResourceModule.Ids;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.MessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.List;

/**
 * @author jstroot
 */
public class DiskResourceViewToolbarImpl extends Composite implements ToolbarView {

    @UiTemplate("DiskResourceViewToolbar.ui.xml")
    interface DiskResourceViewToolbarUiBinder extends UiBinder<Widget, DiskResourceViewToolbarImpl> {
    }

    @UiField(provided = true)
    final ToolbarView.Appearance appearance;
    @UiField(provided = true)
    final DiskResourceSearchField searchField;
    @Inject
    DiskResourceUtil diskResourceUtil;
    @UiField
    TextButton downloadMenu;
    @UiField
    TextButton editMenu;
    @UiField
    TextButton fileMenu;
    @UiField
    MenuItem newFileMi;
    @UiField
    MenuItem newPathListMi;
    @UiField
    MenuItem newWindowMi, newWindowAtLocMi, newFolderMi, duplicateMi, newPlainTextFileMi,
            newTabularDataFileMi, moveToTrashMi, newRFileMi, newPerlFileMi, newPythonFileMi,
            newShellScriptFileMi, newMdFileMi;
    @UiField
    MenuItem openTrashMi, restoreMi, emptyTrashMi;
    @UiField
    TextButton refreshButton;
    @UiField
    MenuItem renameMi, moveMi, deleteMi, editFileMi, editCommentsMi, editInfoTypeMi, savemetadatami,
            copymetadataMi, editmetadataMi, bulkmetadataMi, selectmetadataMi, doiMi;

    @UiField
    TextButton metadataMenu;

    @UiField
    MenuItem shareFolderLocationMi;
    @UiField
    TextButton shareMenu;
    @UiField
    MenuItem shareWithCollaboratorsMi, createPublicLinkMi, sendToCogeMi, sendToEnsemblMi,
            sendToTreeViewerMi, createNcbiSraMi;
    @UiField
    MenuItem simpleDownloadMi, bulkDownloadMi;
    @UiField
    MenuItem simpleUploadMi, bulkUploadMi, importFromUrlMi, importFromCogeMi;
    @UiField
    TextButton trashMenu;
    @UiField
    TextButton uploadMenu;
    private static final DiskResourceViewToolbarUiBinder BINDER = GWT.create(DiskResourceViewToolbarUiBinder.class);
    private final ToolbarView.Presenter presenter;
    private final UserInfo userInfo;
    private List<DiskResource> selectedDiskResources;
    private Folder selectedFolder;

    @Inject
    DiskResourceViewToolbarImpl(final DiskResourceSearchField searchField,
                                final UserInfo userInfo,
                                final ToolbarView.Appearance appearance,
                                @Assisted final ToolbarView.Presenter presenter) {
        this.searchField = searchField;
        this.userInfo = userInfo;
        this.appearance = appearance;
        this.presenter = presenter;
        initWidget(BINDER.createAndBindUi(this));
    }

    // <editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration
            addDeleteSelectedDiskResourcesSelectedEventHandler(DeleteDiskResourcesSelected.DeleteDiskResourcesSelectedEventHandler handler) {
        return addHandler(handler, DeleteDiskResourcesSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addEditInfoTypeSelectedEventHandler(EditInfoTypeSelected.EditInfoTypeSelectedEventHandler handler) {
        return addHandler(handler, EditInfoTypeSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addEmptyTrashSelectedHandler(EmptyTrashSelected.EmptyTrashSelectedHandler handler) {
        return addHandler(handler, EmptyTrashSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addImportFromUrlSelectedHandler(ImportFromUrlSelected.ImportFromUrlSelectedHandler handler) {
        return addHandler(handler, ImportFromUrlSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addManageCommentsSelectedEventHandler(ManageCommentsSelected.ManageCommentsSelectedEventHandler handler) {
        return addHandler(handler, ManageCommentsSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addManageMetadataSelectedEventHandler(ManageMetadataSelected.ManageMetadataSelectedEventHandler handler) {
        return addHandler(handler, ManageMetadataSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addCopyMetadataSelectedEventHandler(CopyMetadataSelected.CopyMetadataSelectedEventHandler handler) {
        return addHandler(handler, CopyMetadataSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addManageSharingSelectedEventHandler(ManageSharingSelected.ManageSharingSelectedEventHandler handler) {
        return addHandler(handler, ManageSharingSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addMoveDiskResourcesSelectedHandler(MoveDiskResourcesSelected.MoveDiskResourcesSelectedHandler handler) {
        return addHandler(handler, MoveDiskResourcesSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addRefreshFolderSelectedHandler(RefreshFolderSelected.RefreshFolderSelectedHandler handler) {
        return addHandler(handler, RefreshFolderSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addRenameDiskResourceSelectedHandler(RenameDiskResourceSelected.RenameDiskResourceSelectedHandler handler) {
        return addHandler(handler, RenameDiskResourceSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addRestoreDiskResourcesSelectedHandler(RestoreDiskResourcesSelected.RestoreDiskResourcesSelectedHandler handler) {
        return addHandler(handler, RestoreDiskResourcesSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addSendToCogeSelectedHandler(SendToCogeSelected.SendToCogeSelectedHandler handler) {
        return addHandler(handler, SendToCogeSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addSendToEnsemblSelectedHandler(SendToEnsemblSelected.SendToEnsemblSelectedHandler handler) {
        return addHandler(handler, SendToEnsemblSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addSendToTreeViewerSelectedHandler(SendToTreeViewerSelected.SendToTreeViewerSelectedHandler handler) {
        return addHandler(handler, SendToTreeViewerSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addShareByDataLinkSelectedEventHandler(ShareByDataLinkSelected.ShareByDataLinkSelectedEventHandler handler) {
        return addHandler(handler, ShareByDataLinkSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addSimpleDownloadSelectedHandler(SimpleDownloadSelected.SimpleDownloadSelectedHandler handler) {
        return addHandler(handler, SimpleDownloadSelected.TYPE);
    }

    @Override
    public HandlerRegistration
            addSimpleUploadSelectedHandler(SimpleUploadSelected.SimpleUploadSelectedHandler handler) {
        return addHandler(handler, SimpleUploadSelected.TYPE);
    }

    // </editor-fold>

    // <editor-fold desc="Selection Handlers">
    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {

        boolean duplicateMiEnabled, addToSideBarMiEnabled, moveToTrashMiEnabled;

        boolean renameMiEnabled, moveMiEnabled, deleteMiEnabled, editFileMiEnabled, editCommentsMiEnabled
                ,editInfoTypeMiEnabled, metadataMiEnabled;

        boolean simpleDownloadMiEnabled, bulkDownloadMiEnabled;
        boolean sendToCogeMiEnabled, sendToEnsemblMiEnabled, sendToTreeViewerMiEnabled;

        boolean shareWithCollaboratorsMiEnabled, createPublicLinkMiEnabled, shareFolderLocationMiEnabled;

        boolean restoreMiEnabled;

        selectedDiskResources = event.getSelection();
        final boolean isSelectionEmpty = selectedDiskResources.isEmpty();
        final boolean isSingleSelection = selectedDiskResources.size() == 1;
        final boolean isOwner = isOwnerList(selectedDiskResources);
        final boolean isWriteable = isWritable();
        DiskResource firstItem = getFirstDiskResource();
        final boolean isReadable = !isSelectionEmpty && isReadable(firstItem);
        final boolean isSelectionInTrash = isSelectionInTrash(selectedDiskResources);
        final boolean isFolderSelect = !isSelectionEmpty
                                       && firstItem instanceof Folder;

        duplicateMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;
        moveToTrashMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;

        renameMiEnabled = !isSelectionEmpty && isSingleSelection && isOwner && !isSelectionInTrash;
        moveMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;
        deleteMiEnabled = !isSelectionEmpty && isOwner;
        editFileMiEnabled = !isSelectionEmpty && isSingleSelection
                && containsFile(selectedDiskResources) && isOwner && !isSelectionInTrash;
        editCommentsMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash
                && isReadable;
        editInfoTypeMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash
                && containsFile(selectedDiskResources) && isOwner;
        metadataMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash;

        simpleDownloadMiEnabled = !isSelectionEmpty && containsFile(selectedDiskResources);
        bulkDownloadMiEnabled = !isSelectionEmpty;
        sendToCogeMiEnabled = !isSelectionEmpty
                && isSingleSelection
                && containsFile(selectedDiskResources)
                && !isSelectionInTrash
                && diskResourceUtil.isGenomeVizInfoType(getInfoTypeFromSingletonCollection(selectedDiskResources));
        sendToEnsemblMiEnabled = !isSelectionEmpty
                && isSingleSelection
                && containsFile(selectedDiskResources)
                && !isSelectionInTrash
                && diskResourceUtil.isEnsemblInfoType(getInfoTypeFromSingletonCollection(selectedDiskResources));
        sendToTreeViewerMiEnabled = !isSelectionEmpty
                && isSingleSelection
                && containsFile(selectedDiskResources)
                && !isSelectionInTrash
                && diskResourceUtil.isTreeInfoType(getInfoTypeFromSingletonCollection(selectedDiskResources));

        shareWithCollaboratorsMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;
        createPublicLinkMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash
                && containsFile(selectedDiskResources);
        shareFolderLocationMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash
                && containsOnlyFolders(selectedDiskResources);

        restoreMiEnabled = !isSelectionEmpty && isSelectionInTrash && isOwner;

        duplicateMi.setEnabled(duplicateMiEnabled);
        moveToTrashMi.setEnabled(moveToTrashMiEnabled);

        renameMi.setEnabled(renameMiEnabled);
        moveMi.setEnabled(moveMiEnabled);
        deleteMi.setEnabled(deleteMiEnabled);
        editFileMi.setEnabled(editFileMiEnabled);
        editCommentsMi.setEnabled(editCommentsMiEnabled);
        editInfoTypeMi.setEnabled(editInfoTypeMiEnabled);

        copymetadataMi.setEnabled(metadataMiEnabled && isReadable);
        savemetadatami.setEnabled(metadataMiEnabled && isReadable);
        bulkmetadataMi.setEnabled(
                metadataMiEnabled && isFolderSelect && (isOwner || isWriteable));
        selectmetadataMi.setEnabled(
                metadataMiEnabled && isFolderSelect && (isOwner || isWriteable));
        doiMi.setEnabled(metadataMiEnabled && isFolderSelect && isOwner);
        editmetadataMi.setEnabled(metadataMiEnabled && isReadable);

        simpleDownloadMi.setEnabled(simpleDownloadMiEnabled);
        bulkDownloadMi.setEnabled(bulkDownloadMiEnabled);

        shareWithCollaboratorsMi.setEnabled(shareWithCollaboratorsMiEnabled);
        createPublicLinkMi.setEnabled(createPublicLinkMiEnabled);
        shareFolderLocationMi.setEnabled(shareFolderLocationMiEnabled);
        sendToCogeMi.setEnabled(sendToCogeMiEnabled);
        sendToEnsemblMi.setEnabled(sendToEnsemblMiEnabled);
        sendToTreeViewerMi.setEnabled(sendToTreeViewerMiEnabled);

        restoreMi.setEnabled(restoreMiEnabled);
    }

    private DiskResource getFirstDiskResource() {
        if(selectedDiskResources!=null &&  selectedDiskResources.size() >0) {
            return selectedDiskResources.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        boolean simpleUploadMiEnabled, bulkUploadMiEnabled, importFromUrlMiEnabled;

        boolean newFolderMiEnabled, newPlainTextFileMiEnabled, newTabularDataFileMiEnabled;
        boolean refreshButtonEnabled;

        selectedFolder = event.getSelectedFolder();
        final boolean isFolderInTrash = isSelectionInTrash(Lists.<DiskResource> newArrayList(selectedFolder));
        final boolean isNull = selectedFolder == null;
        final boolean canUploadTo = canUploadTo(selectedFolder);

        simpleUploadMiEnabled = !isFolderInTrash && (isNull || canUploadTo);
        bulkUploadMiEnabled = !isFolderInTrash && (isNull || canUploadTo);
        importFromUrlMiEnabled = !isFolderInTrash && (isNull || canUploadTo);

        newFolderMiEnabled = isNull || canUploadTo;
        newPlainTextFileMiEnabled = isNull || canUploadTo;
        newTabularDataFileMiEnabled = isNull || canUploadTo;

        refreshButtonEnabled = !isNull;

        simpleUploadMi.setEnabled(simpleUploadMiEnabled);
        bulkUploadMi.setEnabled(bulkUploadMiEnabled);
        importFromUrlMi.setEnabled(importFromUrlMiEnabled);
        importFromCogeMi.setEnabled(simpleUploadMiEnabled);

        newFolderMi.setEnabled(newFolderMiEnabled);
        createNcbiSraMi.setEnabled(newFolderMiEnabled);
        newFileMi.setEnabled(newPlainTextFileMiEnabled);
        // newPlainTextFileMi.setEnabled(newPlainTextFileMiEnabled);
        // newTabularDataFileMi.setEnabled(newTabularDataFileMiEnabled);

        refreshButton.setEnabled(refreshButtonEnabled);
    }

    // </editor-fold>

    // <editor-fold desc="UI Handlers">
    @UiHandler("bulkDownloadMi")
    void onBulkDownloadClicked(SelectionEvent<Item> event) {
        MessageBox bulkDownloadInfoBox = new MessageBox(appearance.bulkDownloadInfoBoxHeading(),
                                                        appearance.bulkDownloadInfoBoxMsg());
        bulkDownloadInfoBox.setIcon(MessageBox.ICONS.info());
        bulkDownloadInfoBox.show();
    }

    @UiHandler("bulkUploadMi")
    void onBulkUploadClicked(SelectionEvent<Item> event) {
        MessageBox bulkUploadInfoBox = new MessageBox(appearance.bulkUploadInfoBoxHeading(),
                                                      appearance.bulkUploadInfoBoxMsg());
        bulkUploadInfoBox.setIcon(MessageBox.ICONS.info());
        bulkUploadInfoBox.show();
    }

    @UiHandler("createPublicLinkMi")
    void onCreatePublicLinkClicked(SelectionEvent<Item> event) {
        presenter.onCreatePublicLinkSelected(selectedDiskResources);
    }

    @UiHandler("deleteMi")
    void onDeleteClicked(SelectionEvent<Item> event) {
        fireEvent(new DeleteDiskResourcesSelected(selectedDiskResources));
    }

    @UiHandler("duplicateMi")
    void onDuplicateClicked(SelectionEvent<Item> event) {/* Do Nothing */
    }

    @UiHandler("editCommentsMi")
    void onEditCommentClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(selectedDiskResources != null && selectedDiskResources.size() == 1);
        fireEvent(new ManageCommentsSelected(selectedDiskResources.iterator().next()));
    }

    @UiHandler("editFileMi")
    void onEditFileClicked(SelectionEvent<Item> event) {
        presenter.onEditFileSelected(selectedDiskResources);
    }

    @UiHandler("editInfoTypeMi")
    void onEditInfoTypeClicked(SelectionEvent<Item> event) {
        fireEvent(new EditInfoTypeSelected(selectedDiskResources));
    }

    @UiHandler("editmetadataMi")
    void onEditMetadataClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(selectedDiskResources != null && selectedDiskResources.size() == 1);
        fireEvent(new ManageMetadataSelected(selectedDiskResources.iterator().next()));
    }

    @UiHandler("selectmetadataMi")
    void onSelectMetadataClicked(SelectionEvent<Item> event) {
        presenter.onBulkMetadataSelected(BulkMetadataDialog.BULK_MODE.SELECT);
    }

    @UiHandler("copymetadataMi")
    void onCopyMetadataClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(selectedDiskResources != null && selectedDiskResources.size() == 1);
        fireEvent(new CopyMetadataSelected(selectedDiskResources.iterator().next()));
    }

    @UiHandler("savemetadatami")
    void onSaveMetadataClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(selectedDiskResources != null && selectedDiskResources.size() == 1);
        fireEvent(new SaveMetadataSelected(selectedDiskResources.iterator().next()));
    }

    @UiHandler("emptyTrashMi")
    void onEmptyTrashClicked(SelectionEvent<Item> event) {
        fireEvent(new EmptyTrashSelected());
    }

    @UiHandler("importFromUrlMi")
    void onImportFromUrlClicked(SelectionEvent<Item> event) {
        fireEvent(new ImportFromUrlSelected(selectedFolder));
    }

    @UiHandler("moveMi")
    void onMoveClicked(SelectionEvent<Item> event) {
        fireEvent(new MoveDiskResourcesSelected(selectedDiskResources));
    }

    @UiHandler("moveToTrashMi")
    void onMoveToTrashClicked(SelectionEvent<Item> event) {
        fireEvent(new DeleteDiskResourcesSelected(selectedDiskResources, false));
    }

    @UiHandler("newFolderMi")
    void onNewFolderClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewFolderSelected(selectedFolder);
    }

    @UiHandler("createNcbiSraMi")
    void onCreateNcbiSraClicked(SelectionEvent<Item> event) {
        presenter.onCreateNcbiSraFolderStructure(selectedFolder);
    }

    @UiHandler("newMdFileMi")
    void onNewMdFile(SelectionEvent<Item> event) {
        presenter.onCreateNewFileSelected(selectedFolder, MimeType.X_WEB_MARKDOWN);
    }

    @UiHandler("newPathListMi")
    void onNewPathListFileClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewPathListSelected();
    }

    @UiHandler("newPerlFileMi")
    void onNewPerlFileClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewFileSelected(selectedFolder, MimeType.X_PERL);
    }

    @UiHandler("newPlainTextFileMi")
    void onNewPlainTextFileClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewFileSelected(selectedFolder, MimeType.PLAIN);
    }

    @UiHandler("newPythonFileMi")
    void onNewPythonFileClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewFileSelected(selectedFolder, MimeType.X_PYTHON);
    }

    @UiHandler("newRFileMi")
    void onNewRFileClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewFileSelected(selectedFolder, MimeType.X_RSRC);
    }

    @UiHandler("newShellScriptFileMi")
    void onNewShellScript(SelectionEvent<Item> event) {
        presenter.onCreateNewFileSelected(selectedFolder, MimeType.X_SH);
    }

    @UiHandler("newTabularDataFileMi")
    void onNewTabularDataFileClicked(SelectionEvent<Item> event) {
        presenter.onCreateNewDelimitedFileSelected();
    }

    @UiHandler("newWindowAtLocMi")
    void onNewWindowAtLocClicked(SelectionEvent<Item> event) {
        presenter.onOpenNewWindowAtLocationSelected(selectedFolder);
    }

    // ---------- File ----------
    @UiHandler("newWindowMi")
    void onNewWindowClicked(SelectionEvent<Item> event) {
        presenter.onOpenNewWindowSelected();
    }

    // ------------- Trash ---------------
    @UiHandler("openTrashMi")
    void onOpenTrashClicked(SelectionEvent<Item> event) {
        presenter.onOpenTrashFolderSelected();
    }

    // ------------ Refresh --------------
    @UiHandler("refreshButton")
    void onRefreshClicked(SelectEvent event) {
        fireEvent(new RefreshFolderSelected(selectedFolder));
    }

    // ----------- Edit -----------
    @UiHandler("renameMi")
    void onRenameClicked(SelectionEvent<Item> event) {
        Preconditions.checkNotNull(selectedDiskResources);
        Preconditions.checkArgument(selectedDiskResources.size() == 1);
        /*
         * FIXME Open RenameFileDialog or RenameFolderDialog from here Handle 'ok' event from dialog,
         * gather information from dialog and fire event to request rename. See DiskResourcePresenter and
         * the Dialogs mentioned above for more information.
         */
        fireEvent(new RenameDiskResourceSelected(selectedDiskResources.iterator().next()));
    }

    @UiHandler("restoreMi")
    void onRestoreClicked(SelectionEvent<Item> event) {
        fireEvent(new RestoreDiskResourcesSelected(selectedDiskResources));
    }

    @UiHandler("sendToCogeMi")
    void onSendToCogeClicked(SelectionEvent<Item> event) {
        fireEvent(new SendToCogeSelected(selectedDiskResources));
    }

    @UiHandler("sendToEnsemblMi")
    void onSendToEnsemblClicked(SelectionEvent<Item> event) {
        fireEvent(new SendToEnsemblSelected(selectedDiskResources));
    }

    @UiHandler("sendToTreeViewerMi")
    void onSendToTreeViewerClicked(SelectionEvent<Item> event) {
        fireEvent(new SendToTreeViewerSelected(selectedDiskResources));
    }

    @UiHandler("shareFolderLocationMi")
    void onShareFolderLocationClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(selectedFolder != null);
        fireEvent(new ShareByDataLinkSelected(selectedFolder));
    }

    // --------- Sharing -------------
    @UiHandler("shareWithCollaboratorsMi")
    void onShareWithCollaboratorsClicked(SelectionEvent<Item> event) {
        Preconditions.checkState(selectedDiskResources != null && !selectedDiskResources.isEmpty());
        fireEvent(new ManageSharingSelected(selectedDiskResources));
    }

    // ---------- Download --------------
    @UiHandler("simpleDownloadMi")
    void onSimpleDownloadClicked(SelectionEvent<Item> event) {
        fireEvent(new SimpleDownloadSelected(selectedFolder, selectedDiskResources));
    }

    // -------- Upload ---------------
    @UiHandler("simpleUploadMi")
    void onSimpleUploadClicked(SelectionEvent<Item> event) {
        fireEvent(new SimpleUploadSelected(selectedFolder));
    }

    @UiHandler("importFromCogeMi")
    void onImportFormCoge(SelectionEvent<Item> event) {
        presenter.onImportFromCoge();
    }

    @UiHandler("doiMi")
    void onRequestDOI(SelectionEvent<Item> event) {
        final ConfirmMessageBox mb = new ConfirmMessageBox(appearance.requestDOI(),
                                                           appearance.doiLinkMsg());

        mb.getButton(PredefinedButton.YES).setText(appearance.needDOI());
        mb.getButton(PredefinedButton.NO).setText(I18N.DISPLAY.cancel());
        mb.addDialogHideHandler(new DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                switch (event.getHideButton()) {
                    case OK:
                        break;
                    case CANCEL:
                        break;
                    case CLOSE:
                        break;
                    case YES:
                        presenter.onDoiRequest(getFirstDiskResource().getId());
                        break;
                    case NO:
                        break;
                    default:
                        // error, button added with no specific action ready
                }
            }
        });
        mb.setWidth(300);
        mb.show();
    }
    // </editor-fold>

    @Override
    public DiskResourceSearchField getSearchField() {
        return searchField;
    }

    @Override
    public void maskSendToCoGe() {
        sendToCogeMi.mask();

    }

    @Override
    public void maskSendToEnsembl() {
        sendToEnsemblMi.mask();

    }

    @Override
    public void maskSendToTreeViewer() {
        sendToTreeViewerMi.mask();

    }

    @Override
    public void unmaskSendToCoGe() {
        sendToCogeMi.unmask();
    }

    @Override
    public void unmaskSendToEnsembl() {
        sendToEnsemblMi.unmask();
    }

    @Override
    public void unmaskSendToTreeViewer() {
        sendToTreeViewerMi.unmask();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        uploadMenu.ensureDebugId(baseID + Ids.UPLOAD_MENU);
        fileMenu.ensureDebugId(baseID + Ids.FILE_MENU);
        editMenu.ensureDebugId(baseID + Ids.EDIT_MENU);
        downloadMenu.ensureDebugId(baseID + Ids.DOWNLOAD_MENU);
        refreshButton.ensureDebugId(baseID + Ids.REFRESH_BUTTON);
        shareMenu.ensureDebugId(baseID + Ids.SHARE_MENU);
        trashMenu.ensureDebugId(baseID + Ids.TRASH_MENU);
        searchField.ensureDebugId(baseID + Ids.SEARCH_FIELD);

        // Upload menu
        simpleUploadMi.ensureDebugId(baseID + Ids.UPLOAD_MENU + Ids.MENU_ITEM_SIMPLE_UPLOAD);
        bulkUploadMi.ensureDebugId(baseID + Ids.UPLOAD_MENU + Ids.MENU_ITEM_BULK_UPLOAD);
        importFromUrlMi.ensureDebugId(baseID + Ids.UPLOAD_MENU + Ids.MENU_ITEM_IMPORT_FROM_URL);
        importFromCogeMi.ensureDebugId(baseID + Ids.UPLOAD_MENU + Ids.MENU_ITEM_IMPORT_FROM_COGE);

        // File menu
        newWindowMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_WINDOW);
        newWindowAtLocMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_WINDOW_AT_LOC);
        newFolderMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_FOLDER);
        duplicateMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_DUPLICATE);
        newPlainTextFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_PLAIN_TEXT);
        newTabularDataFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_TABULAR_DATA);
        createNcbiSraMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NCBI_SRA);

        newPerlFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_PERL_DATA);
        newPythonFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_PYTHON_DATA);
        newRFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_R_DATA);
        newShellScriptFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_SHELL_DATA);
        newMdFileMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_MD_DATA);
        newPathListMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_NEW_PATH_LIST);

        moveToTrashMi.ensureDebugId(baseID + Ids.FILE_MENU + Ids.MENU_ITEM_MOVE_TO_TRASH);

        // Edit menu
        renameMi.ensureDebugId(baseID + Ids.EDIT_MENU + Ids.MENU_ITEM_RENAME);
        moveMi.ensureDebugId(baseID + Ids.EDIT_MENU + Ids.MENU_ITEM_MOVE);
        editFileMi.ensureDebugId(baseID + Ids.EDIT_MENU + Ids.MENU_ITEM_EDIT_FILE);
        editInfoTypeMi.ensureDebugId(baseID + Ids.EDIT_MENU + Ids.MENU_ITEM_EDIT_INFO_TYPE);

        // Metadata menu
        metadataMenu.ensureDebugId(baseID + Ids.METADATA_MENU);
        copymetadataMi.ensureDebugId(baseID + Ids.METADATA_MENU + Ids.MENU_ITEM_METADATA_COPY);
        savemetadatami.ensureDebugId(baseID + Ids.METADATA_MENU + Ids.MENU_ITEM_METADATA_SAVE);
        doiMi.ensureDebugId(baseID + Ids.METADATA_MENU + Ids.MENU_ITEM_REQUEST_DOI);
        bulkmetadataMi.ensureDebugId(baseID + Ids.METADATA_MENU + Ids.MENU_ITEM_BULK_METADATA);
        selectmetadataMi.ensureDebugId(
                baseID + Ids.METADATA_MENU + Ids.MENU_ITEM_BULK_METADATA + Ids.MENU_ITEM_SELECTFILE);

        // Download menu
        simpleDownloadMi.ensureDebugId(baseID + Ids.DOWNLOAD_MENU + Ids.MENU_ITEM_SIMPLE_DOWNLOAD);
        bulkDownloadMi.ensureDebugId(baseID + Ids.DOWNLOAD_MENU + Ids.MENU_ITEM_BULK_DOWNLOAD);

        // Share menu
        shareWithCollaboratorsMi.ensureDebugId(baseID + Ids.SHARE_MENU
                + Ids.MENU_ITEM_SHARE_WITH_COLLABORATORS);
        createPublicLinkMi.ensureDebugId(baseID + Ids.SHARE_MENU + Ids.MENU_ITEM_CREATE_PUBLIC_LINK);
        shareFolderLocationMi.ensureDebugId(baseID + Ids.SHARE_MENU
                + Ids.MENU_ITEM_SHARE_FOLDER_LOCATION);
        sendToCogeMi.ensureDebugId(baseID + Ids.SHARE_MENU + Ids.MENU_ITEM_SEND_TO_COGE);
        sendToEnsemblMi.ensureDebugId(baseID + Ids.SHARE_MENU + Ids.MENU_ITEM_SEND_TO_ENSEMBL);
        sendToTreeViewerMi.ensureDebugId(baseID + Ids.SHARE_MENU + Ids.MENU_ITEM_SEND_TO_TREE_VIEWER);

        // Trash menu
        openTrashMi.ensureDebugId(baseID + Ids.TRASH_MENU + Ids.MENU_ITEM_OPEN_TRASH);
        restoreMi.ensureDebugId(baseID + Ids.TRASH_MENU + Ids.MENU_ITEM_RESTORE);
        emptyTrashMi.ensureDebugId(baseID + Ids.TRASH_MENU + Ids.MENU_ITEM_EMPTY_TRASH);
        deleteMi.ensureDebugId(baseID + Ids.TRASH_MENU + Ids.MENU_ITEM_DELETE);

    }

    boolean canUploadTo(DiskResource folder) {
        return diskResourceUtil.canUploadTo(folder);
    }

    boolean containsFile(final List<DiskResource> selection) {
        return diskResourceUtil.containsFile(selection);
    }

    boolean containsOnlyFolders(List<DiskResource> selection) {
        for (DiskResource dr : selection) {
            if (dr instanceof File)
                return false;
        }
        return true;
    }

    boolean isOwnerList(final List<DiskResource> selection) {
        return diskResourceUtil.isOwner(selection);
    }

    boolean isOwner(final DiskResource item) {
       return diskResourceUtil.isOwner(item);
    }

    boolean isReadable(final DiskResource item) {
        return diskResourceUtil.isReadable(item);
    }

    boolean isWritable() {
        return diskResourceUtil.isWritable(getFirstDiskResource());
    }

    boolean isSelectionInTrash(final List<DiskResource> selection) {
        if (selection.isEmpty()) {
            return false;
        }

        String trashPath = userInfo.getTrashPath();
        for (DiskResource dr : selection) {
            if (dr.getPath().equals(trashPath)) {
                return false;
            }

            if (!dr.getPath().startsWith(trashPath)) {
                return false;
            }
        }

        return true;
    }

    private InfoType getInfoTypeFromSingletonCollection(List<DiskResource> selectedDiskResources) {
        Preconditions.checkArgument(selectedDiskResources.size() == 1);
        return InfoType.fromTypeString(selectedDiskResources.iterator().next().getInfoType());
    }

    @Override
    public HandlerRegistration
            addSaveMetadataSelectedEventHandler(SaveMetadataSelectedEventHandler handler) {
        return addHandler(handler, SaveMetadataSelected.TYPE);
    }

    @Override
    public void openViewForGenomeSearch(GenomeSearchDialog view) {
        GenomeSearchDialog dialog = view;
        dialog.clearView();
        dialog.setSize("600px", "300px");
        dialog.show();

    }

    @Override
    public void openViewBulkMetadata(BulkMetadataDialog bmd) {
        bmd.setSize("600px", "200px");
        bmd.show();

    }

}
