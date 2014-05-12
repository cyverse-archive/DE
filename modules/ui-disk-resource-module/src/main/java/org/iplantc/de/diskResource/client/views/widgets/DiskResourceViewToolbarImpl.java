package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

import java.util.List;

public class DiskResourceViewToolbarImpl extends Composite implements DiskResourceView.DiskResourceViewToolbar, DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler, FolderSelectionEvent.FolderSelectionEventHandler {

    @UiTemplate("DiskResourceViewToolbar.ui.xml")
    interface DiskResourceViewToolbarUiBinder extends UiBinder<Widget, DiskResourceViewToolbarImpl> { }
    @UiField
    TextButton downloadMenu;
    @UiField
    TextButton editMenu;
    @UiField
    TextButton fileMenu;
    @UiField
    MenuItem newWindowMi, newWindowAtLocMi, newFolderMi,
            duplicateMi, addToSideBarMi, newPlainTextFileMi,
            newTabularDataFileMi, moveToTrashMi;
    @UiField
    MenuItem openTrashMi, restoreMi, emptyTrashMi;
    @UiField
    TextButton refreshButton;
    @UiField
    MenuItem renameMi, moveMi, deleteMi,
            editFileMi, editCommentsMi, editInfoTypeMi, metadataMi;
    @UiField
    DiskResourceSearchField searchField;
    @UiField
    TextButton shareMenu;
    @UiField
    MenuItem shareWithCollaboratorsMi, createPublicLinkMi, sendToCogeMi,
            sendToEnsemblMi, sendToTreeViewerMi;
    @UiField
    MenuItem simpleDownloadMi, bulkDownloadMi;
    @UiField
    MenuItem simpleUploadMi, bulkUploadMi, importFromUrlMi;
    @UiField
    TextButton trashMenu;
    @UiField
    TextButton uploadMenu;
    @UiField
    MenuItem shareFolderLocationMi;
    private static DiskResourceViewToolbarUiBinder BINDER = GWT.create(DiskResourceViewToolbarUiBinder.class);
    private final UserInfo userInfo;
    private DiskResourceView.Presenter presenter;
    private DiskResourceView view;

    @Inject
    public DiskResourceViewToolbarImpl(final UserInfo userInfo) {
        this.userInfo = userInfo;
        initWidget(BINDER.createAndBindUi(this));
    }

    @Override
    public DiskResourceSearchField getSearchField() {
        // TODO CORE-5300 This class will listen for events on this field, here.
        return searchField;
    }

    @Override
    public void init(DiskResourceView.Presenter presenter, DiskResourceView view) {
        this.presenter = presenter;
        this.view = view;
        this.view.addDiskResourceSelectionChangedEventHandler(this);
        this.view.addFolderSelectedEventHandler(this);
    }

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {

        boolean duplicateMiEnabled, addToSideBarMiEnabled, moveToTrashMiEnabled;

        boolean renameMiEnabled, moveMiEnabled, deleteMiEnabled, editFileMiEnabled, editCommentsMiEnabled, editInfoTypeMiEnabled, metadataMiEnabled;

        boolean simpleDownloadMiEnabled, bulkDownloadMiEnabled;
        boolean sendToCogeMiEnabled, sendToEnsemblMiEnabled, sendToTreeViewerMiEnabled;

        boolean shareWithCollaboratorsMiEnabled, createPublicLinkMiEnabled, shareFolderLocationMiEnabled;

        boolean restoreMiEnabled;

        final List<DiskResource> selection = event.getSelection();
        final boolean isSelectionEmpty = selection.isEmpty();
        final boolean isSingleSelection = selection.size() == 1;
        final boolean isOwner = isOwner(selection);
        final boolean isSelectionInTrash = isSelectionInTrash(selection);

        duplicateMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;
        addToSideBarMiEnabled = !isSelectionEmpty && !isSelectionInTrash && containsOnlyFolders(selection);
        moveToTrashMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;

        renameMiEnabled = !isSelectionEmpty && isSingleSelection && isOwner && !isSelectionInTrash;
        moveMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;
        deleteMiEnabled = !isSelectionEmpty && isOwner;
        editFileMiEnabled = !isSelectionEmpty && isSingleSelection && containsFile(selection) && isOwner && !isSelectionInTrash;
        editCommentsMiEnabled = false;
        editInfoTypeMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash && containsFile(selection) && isOwner;
        metadataMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash && isReadable(selection.get(0));

        simpleDownloadMiEnabled = !isSelectionEmpty && containsFile(selection);
        bulkDownloadMiEnabled = !isSelectionEmpty;
        sendToCogeMiEnabled = !isSelectionEmpty && isSingleSelection && containsFile(selection) && !isSelectionInTrash;
        sendToEnsemblMiEnabled = !isSelectionEmpty && isSingleSelection && containsFile(selection) && !isSelectionInTrash;
        sendToTreeViewerMiEnabled = !isSelectionEmpty && isSingleSelection && containsFile(selection) && !isSelectionInTrash;

        shareWithCollaboratorsMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash;
        createPublicLinkMiEnabled = !isSelectionEmpty && isOwner && !isSelectionInTrash && containsFile(selection);
        shareFolderLocationMiEnabled = !isSelectionEmpty && isSingleSelection && !isSelectionInTrash && containsOnlyFolders(selection);


        restoreMiEnabled = !isSelectionEmpty && isSelectionInTrash && isOwner;

        duplicateMi.setEnabled(duplicateMiEnabled);
        addToSideBarMi.setEnabled(addToSideBarMiEnabled);
        moveToTrashMi.setEnabled(moveToTrashMiEnabled);

        renameMi.setEnabled(renameMiEnabled);
        moveMi.setEnabled(moveMiEnabled);
        deleteMi.setEnabled(deleteMiEnabled);
        editFileMi.setEnabled(editFileMiEnabled);
        editCommentsMi.setEnabled(editCommentsMiEnabled);
        editInfoTypeMi.setEnabled(editInfoTypeMiEnabled);
        metadataMi.setEnabled(metadataMiEnabled);

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

    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        boolean simpleUploadMiEnabled, bulkUploadMiEnabled, importFromUrlMiEnabled;

        boolean newFolderMiEnabled, newPlainTextFileMiEnabled, newTabularDataFileMiEnabled;
        boolean refreshButtonEnabled;

        final Folder selectedFolder = event.getSelectedFolder();
        final boolean isFolderInTrash = isSelectionInTrash(Lists.<DiskResource>newArrayList(selectedFolder));
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

        newFolderMi.setEnabled(newFolderMiEnabled);
        newPlainTextFileMi.setEnabled(newPlainTextFileMiEnabled);
        newTabularDataFileMi.setEnabled(newTabularDataFileMiEnabled);

        refreshButton.setEnabled(refreshButtonEnabled);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
    }

    boolean canUploadTo(DiskResource folder){
        return DiskResourceUtil.canUploadTo(folder);
    }

    boolean containsFile(final List<DiskResource> selection) {
        return DiskResourceUtil.containsFile(selection);
    }

    boolean containsOnlyFolders(List<DiskResource> selection) {
        for(DiskResource dr : selection ) {
            if(dr instanceof File)
                return false;
        }
       return true;
    }

    boolean isOwner(final List<DiskResource> selection){
        return DiskResourceUtil.isOwner(selection);
    }

    boolean isReadable(final DiskResource item){
        return DiskResourceUtil.isReadable(item);
    }

    boolean isSelectionInTrash(final List<DiskResource> selection){
        if (selection.isEmpty()) {
            return false;
        }

        String trashPath = userInfo.getTrashPath();
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

    @UiHandler("addToSideBarMi")
    void onAddToSideBarClicked(SelectionEvent<Item> event){
    }

    @UiHandler("bulkDownloadMi")
    void onBulkDownloadClicked(SelectionEvent<Item> event) {
        presenter.doBulkDownload();
    }

    @UiHandler("bulkUploadMi")
    void onBulkUploadClicked(SelectionEvent<Item> event) {
        presenter.doBulkUpload();
    }

    @UiHandler("createPublicLinkMi")
    void onCreatePublicLinkClicked(SelectionEvent<Item> event){
        presenter.manageSelectedResourceDataLinks();
    }

    @UiHandler("deleteMi")
    void onDeleteClicked(SelectionEvent<Item> event){
        presenter.deleteSelectedResources();
    }

    @UiHandler("duplicateMi")
    void onDuplicateClicked(SelectionEvent<Item> event){
    }

    @UiHandler("editCommentsMi")
    void onEditCommentClicked(SelectionEvent<Item> event){
    }

    @UiHandler("editFileMi")
    void onEditFileClicked(SelectionEvent<Item> event){
        presenter.editSelectedFile();
    }

    @UiHandler("editInfoTypeMi")
    void onEditInfoTypeClicked(SelectionEvent<Item> event){
        presenter.editSelectedResourceInfoType();
    }

    @UiHandler("metadataMi")
    void onEditMetadataClicked(SelectionEvent<Item> event){
        presenter.manageSelectedResourceMetadata();
    }

    @UiHandler("emptyTrashMi")
    void onEmptyTrashClicked(SelectionEvent<Item> event) {
        presenter.emptyTrash();
    }

    @UiHandler("importFromUrlMi")
    void onImportFromUrlClicked(SelectionEvent<Item> event) {
        presenter.doImportFromUrl();
    }

    @UiHandler("moveMi")
    void onMoveClicked(SelectionEvent<Item> event){
        presenter.moveSelectedDiskResources();
    }

    @UiHandler("moveToTrashMi")
    void onMoveToTrashClicked(SelectionEvent<Item> event){
        presenter.moveSelectedDiskResourcesToTrash();
    }

    @UiHandler("newFolderMi")
    void onNewFolderClicked(SelectionEvent<Item> event) {
        presenter.createNewFolder();
    }

    @UiHandler("newPlainTextFileMi")
    void onNewPlainTextFileClicked(SelectionEvent<Item> event){
        presenter.createNewPlainTextFile();
    }

    @UiHandler("newTabularDataFileMi")
    void onNewTabularDataFileClicked(SelectionEvent<Item> event){
    }

    @UiHandler("newWindowAtLocMi")
    void onNewWindowAtLocClicked(SelectionEvent<Item> event) {
        presenter.openNewWindow(true);
    }

    //---------- File ----------
    @UiHandler("newWindowMi")
    void onNewWindowClicked(SelectionEvent<Item> event) {
        presenter.openNewWindow(false);
    }

    //------------- Trash ---------------
    @UiHandler("openTrashMi")
    void onOpenTrashClicked(SelectionEvent<Item> event) {
        presenter.selectTrashFolder();
    }

    //------------ Refresh --------------
    @UiHandler("refreshButton")
    void onRefreshClicked(SelectEvent event) {
        presenter.refreshSelectedFolder();
    }

    //----------- Edit -----------
    @UiHandler("renameMi")
    void onRenameClicked(SelectionEvent<Item> event){
        presenter.renameSelectedResource();
    }

    @UiHandler("restoreMi")
    void onRestoreClicked(SelectionEvent<Item> event){
        presenter.restoreSelectedResources();
    }

    @UiHandler("sendToCogeMi")
    void onSendToCogeClicked(SelectionEvent<Item> event) {
        presenter.sendSelectedResourcesToCoge();
    }

    @UiHandler("sendToEnsemblMi")
    void onSendToEnsemblClicked(SelectionEvent<Item> event){
        presenter.sendSelectedResourceToEnsembl();
    }

    @UiHandler("sendToTreeViewerMi")
    void onSendToTreeViewerClicked(SelectionEvent<Item> event){
        presenter.sendSelectedResourcesToTreeViewer();
    }

    //--------- Sharing -------------
    @UiHandler("shareWithCollaboratorsMi")
    void onShareWithCollaboratorsClicked(SelectionEvent<Item> event) {
        presenter.manageSelectedResourceCollaboratorSharing();
    }

    @UiHandler("shareFolderLocationMi")
    void onShareFolderLocationClicked(SelectionEvent<Item> event){
        presenter.shareSelectedFolderByDataLink();
    }

    //---------- Download --------------
    @UiHandler("simpleDownloadMi")
    void onSimpleDownloadClicked(SelectionEvent<Item> event) {
        presenter.doSimpleDownload();
    }

    //-------- Upload ---------------
    @UiHandler("simpleUploadMi")
    void onSimpleUploadClicked(SelectionEvent<Item> event) {
        presenter.doSimpleUpload();
    }

    @Override
    public void maskSendToCoGe() {
        sendToCogeMi.mask();

    }

    @Override
    public void unmaskSendToCoGe() {
        sendToCogeMi.unmask();

    }

    @Override
    public void maskSendToEnsembl() {
        sendToEnsemblMi.mask();

    }

    @Override
    public void unmaskSendToEnsembl() {
        sendToEnsemblMi.unmask();

    }

    @Override
    public void maskSendToTreeViewer() {
        sendToTreeViewerMi.mask();

    }

    @Override
    public void unmaskSendToTreeViewer() {
        sendToTreeViewerMi.unmask();

    }
}
