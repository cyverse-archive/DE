package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.DiskResourceView;

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

public class DiskResourceViewToolbarImpl extends Composite implements DiskResourceView.DiskResourceViewToolbar, DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler, FolderSelectionEvent.FolderSelectionEventHandler {

    @UiTemplate("DiskResourceViewToolbar.ui.xml")
    interface DiskResourceViewToolbarUiBinder extends UiBinder<Widget, DiskResourceViewToolbarImpl> { }

    private static DiskResourceViewToolbarUiBinder BINDER = GWT.create(DiskResourceViewToolbarUiBinder.class);

    private DiskResourceView.Presenter presenter;
    private DiskResourceView view;

    @UiField
    DiskResourceSearchField searchField;
    @UiField
    TextButton uploadMenu;
    @UiField
    MenuItem simpleUploadMi, bulkUploadMi, importFromUrlMi;

    @UiField
    TextButton fileMenu;
    @UiField
    MenuItem newWindowMi, newWindowAtLocMi, newFolderMi,
            duplicateMi, addToSideBarMi, newPlainTextFileMi,
            newTabularDataFileMi, moveToTrashMi;

    @UiField
    TextButton editMenu;
    @UiField
    MenuItem renameMi, moveMi, deleteMi, editFileMi, editCommentsMi, editInfoTypeMi, metadataMi;

    @UiField
    TextButton downloadMenu;
    @UiField
    MenuItem simpleDownloadMi, bulkDownloadMi;

    @UiField
    TextButton shareMenu;
    @UiField
    MenuItem shareWithCollaboratorsMi, createPublicLinkMi, sendToCogeMi, sendToEnsemblMi, sendToTreeViewerMi;

    @UiField
    TextButton refreshButton;

    @UiField
    TextButton trashMenu;
    @UiField
    MenuItem openTrashMi, restoreMi, emptyTrashMi;

    @Inject
    public DiskResourceViewToolbarImpl() {
        initWidget(BINDER.createAndBindUi(this));
    }

    @Override
    public void init(DiskResourceView.Presenter presenter, DiskResourceView view) {
        this.presenter = presenter;
        this.view = view;
        this.view.addDiskResourceSelectionChangedEventHandler(this);
        this.view.addFolderSelectedEventHandler(this);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
    }

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        boolean searchFieldEnabled;
        boolean uploadMenuEnabled;
        boolean simpleUploadMiEnabled, bulkUploadMiEnabled, importFromUrlMiEnabled;

        boolean fileMenuEnabled;
        boolean newWindowMiEnabled, newWindowAtLocMiEnabled, newFolderMiEnabled,
                duplicateMiEnabled, addToSideBarMiEnabled, newPlainTextFileMiEnabled,
                newTabularDataFileMiEnabled, moveToTrashMiEnabled;

        boolean editMenuEnabled;
        boolean renameMiEnabled, moveMiEnabled, deleteMiEnabled, editFileMiEnabled, editCommentsMiEnabled, editInfoTypeMiEnabled, metadataMiEnabled;

        boolean downloadMenuEnabled;
        boolean simpleDownloadMiEnabled, bulkDownloadMiEnabled;

        boolean shareMenuEnabled;
        boolean shareWithCollaboratorsMiEnabled, createPublicLinkMiEnabled, sendToCogeMiEnabled, sendToEnsemblMiEnabled, sendToTreeViewerMiEnabled;

        boolean refreshButtonEnabled;

        boolean trashMenuEnabled;
        boolean openTrashMiEnabled, restoreMiEnabled, emptyTrashMiEnabled;

        searchFieldEnabled = false;
        uploadMenuEnabled = false;
        simpleUploadMiEnabled = false;
        bulkUploadMiEnabled = false;
        importFromUrlMiEnabled = false;

        fileMenuEnabled = false;
        newWindowMiEnabled = false;
        newWindowAtLocMiEnabled = false;
        newFolderMiEnabled = false;
        duplicateMiEnabled = false;
        addToSideBarMiEnabled = false;
        newPlainTextFileMiEnabled = false;
        newTabularDataFileMiEnabled = false;
        moveToTrashMiEnabled = false;

        editMenuEnabled = false;
        renameMiEnabled = false;
        moveMiEnabled = false;
        deleteMiEnabled = false;
        editFileMiEnabled = false;
        editCommentsMiEnabled = false;
        editInfoTypeMiEnabled = false;
        metadataMiEnabled = false;

        downloadMenuEnabled = false;
        simpleDownloadMiEnabled = false;
        bulkDownloadMiEnabled = false;

        shareMenuEnabled = false;
        shareWithCollaboratorsMiEnabled = false;
        createPublicLinkMiEnabled = false;
        sendToCogeMiEnabled = false;
        sendToEnsemblMiEnabled = false;
        sendToTreeViewerMiEnabled = false;

        refreshButtonEnabled = false;

        trashMenuEnabled = false;
        openTrashMiEnabled= false;
        restoreMiEnabled = false;
        emptyTrashMiEnabled = false;


        searchField.setEnabled(searchFieldEnabled);
        uploadMenu.setEnabled(uploadMenuEnabled);
        simpleUploadMi.setEnabled(simpleUploadMiEnabled);
        bulkUploadMi.setEnabled(bulkUploadMiEnabled);
        importFromUrlMi.setEnabled(importFromUrlMiEnabled);

        fileMenu.setEnabled(fileMenuEnabled);
        newWindowMi.setEnabled(newWindowMiEnabled);
        newWindowAtLocMi.setEnabled(newWindowAtLocMiEnabled);
        newFolderMi.setEnabled(newFolderMiEnabled);
        duplicateMi.setEnabled(duplicateMiEnabled);
        addToSideBarMi.setEnabled(addToSideBarMiEnabled);
        newPlainTextFileMi.setEnabled(newPlainTextFileMiEnabled);
        newTabularDataFileMi.setEnabled(newTabularDataFileMiEnabled);
        moveToTrashMi.setEnabled(moveToTrashMiEnabled);

        editMenu.setEnabled(editMenuEnabled);
        renameMi.setEnabled(renameMiEnabled);
        moveMi.setEnabled(moveMiEnabled);
        deleteMi.setEnabled(deleteMiEnabled);
        editFileMi.setEnabled(editFileMiEnabled);
        editCommentsMi.setEnabled(editCommentsMiEnabled);
        editInfoTypeMi.setEnabled(editInfoTypeMiEnabled);
        metadataMi.setEnabled(metadataMiEnabled);

        downloadMenu.setEnabled(downloadMenuEnabled);
        simpleDownloadMi.setEnabled(simpleDownloadMiEnabled);
        bulkDownloadMi.setEnabled(bulkDownloadMiEnabled);

        shareMenu.setEnabled(shareMenuEnabled);
        shareWithCollaboratorsMi.setEnabled(shareWithCollaboratorsMiEnabled);
        createPublicLinkMi.setEnabled(createPublicLinkMiEnabled);
        sendToCogeMi.setEnabled(sendToCogeMiEnabled);
        sendToEnsemblMi.setEnabled(sendToEnsemblMiEnabled);
        sendToTreeViewerMi.setEnabled(sendToTreeViewerMiEnabled);

        refreshButton.setEnabled(refreshButtonEnabled);

        trashMenu.setEnabled(trashMenuEnabled);
        openTrashMi.setEnabled(openTrashMiEnabled);
        restoreMi.setEnabled(restoreMiEnabled);
        emptyTrashMi.setEnabled(emptyTrashMiEnabled);
    }

    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
            boolean searchFieldEnabled;
            boolean uploadMenuEnabled;
            boolean simpleUploadMiEnabled, bulkUploadMiEnabled, importFromUrlMiEnabled;

            boolean fileMenuEnabled;
            boolean newWindowMiEnabled, newWindowAtLocMiEnabled, newFolderMiEnabled,
                    duplicateMiEnabled, addToSideBarMiEnabled, newPlainTextFileMiEnabled,
                    newTabularDataFileMiEnabled, moveToTrashMiEnabled;

            boolean editMenuEnabled;
            boolean renameMiEnabled, moveMiEnabled, deleteMiEnabled, editFileMiEnabled, editCommentsMiEnabled, editInfoTypeMiEnabled, metadataMiEnabled;

            boolean downloadMenuEnabled;
            boolean simpleDownloadMiEnabled, bulkDownloadMiEnabled;

            boolean shareMenuEnabled;
            boolean shareWithCollaboratorsMiEnabled, createPublicLinkMiEnabled, sendToCogeMiEnabled, sendToEnsemblMiEnabled, sendToTreeViewerMiEnabled;

            boolean refreshButtonEnabled;

            boolean trashMenuEnabled;
            boolean openTrashMiEnabled, restoreMiEnabled, emptyTrashMiEnabled;

            searchFieldEnabled = false;
            uploadMenuEnabled = false;
            simpleUploadMiEnabled = false;
            bulkUploadMiEnabled = false;
            importFromUrlMiEnabled = false;

            fileMenuEnabled = false;
            newWindowMiEnabled = false;
            newWindowAtLocMiEnabled = false;
            newFolderMiEnabled = false;
            duplicateMiEnabled = false;
            addToSideBarMiEnabled = false;
            newPlainTextFileMiEnabled = false;
            newTabularDataFileMiEnabled = false;
            moveToTrashMiEnabled = false;

            editMenuEnabled = false;
            renameMiEnabled = false;
            moveMiEnabled = false;
            deleteMiEnabled = false;
            editFileMiEnabled = false;
            editCommentsMiEnabled = false;
            editInfoTypeMiEnabled = false;
            metadataMiEnabled = false;

            downloadMenuEnabled = false;
            simpleDownloadMiEnabled = false;
            bulkDownloadMiEnabled = false;

            shareMenuEnabled = false;
            shareWithCollaboratorsMiEnabled = false;
            createPublicLinkMiEnabled = false;
            sendToCogeMiEnabled = false;
            sendToEnsemblMiEnabled = false;
            sendToTreeViewerMiEnabled = false;

            refreshButtonEnabled = false;

            trashMenuEnabled = false;
            openTrashMiEnabled= false;
            restoreMiEnabled = false;
            emptyTrashMiEnabled = false;


            searchField.setEnabled(searchFieldEnabled);
            uploadMenu.setEnabled(uploadMenuEnabled);
            simpleUploadMi.setEnabled(simpleUploadMiEnabled);
            bulkUploadMi.setEnabled(bulkUploadMiEnabled);
            importFromUrlMi.setEnabled(importFromUrlMiEnabled);

            fileMenu.setEnabled(fileMenuEnabled);
            newWindowMi.setEnabled(newWindowMiEnabled);
            newWindowAtLocMi.setEnabled(newWindowAtLocMiEnabled);
            newFolderMi.setEnabled(newFolderMiEnabled);
            duplicateMi.setEnabled(duplicateMiEnabled);
            addToSideBarMi.setEnabled(addToSideBarMiEnabled);
            newPlainTextFileMi.setEnabled(newPlainTextFileMiEnabled);
            newTabularDataFileMi.setEnabled(newTabularDataFileMiEnabled);
            moveToTrashMi.setEnabled(moveToTrashMiEnabled);

            editMenu.setEnabled(editMenuEnabled);
            renameMi.setEnabled(renameMiEnabled);
            moveMi.setEnabled(moveMiEnabled);
            deleteMi.setEnabled(deleteMiEnabled);
            editFileMi.setEnabled(editFileMiEnabled);
            editCommentsMi.setEnabled(editCommentsMiEnabled);
            editInfoTypeMi.setEnabled(editInfoTypeMiEnabled);
            metadataMi.setEnabled(metadataMiEnabled);

            downloadMenu.setEnabled(downloadMenuEnabled);
            simpleDownloadMi.setEnabled(simpleDownloadMiEnabled);
            bulkDownloadMi.setEnabled(bulkDownloadMiEnabled);

            shareMenu.setEnabled(shareMenuEnabled);
            shareWithCollaboratorsMi.setEnabled(shareWithCollaboratorsMiEnabled);
            createPublicLinkMi.setEnabled(createPublicLinkMiEnabled);
            sendToCogeMi.setEnabled(sendToCogeMiEnabled);
            sendToEnsemblMi.setEnabled(sendToEnsemblMiEnabled);
            sendToTreeViewerMi.setEnabled(sendToTreeViewerMiEnabled);

            refreshButton.setEnabled(refreshButtonEnabled);

            trashMenu.setEnabled(trashMenuEnabled);
            openTrashMi.setEnabled(openTrashMiEnabled);
            restoreMi.setEnabled(restoreMiEnabled);
            emptyTrashMi.setEnabled(emptyTrashMiEnabled);
    }

    //-------- Upload ---------------
    @UiHandler("simpleUploadMi")
    void onSimpleUploadClicked(SelectionEvent<Item> event) {
        presenter.doSimpleUpload();
    }

    @UiHandler("bulkUploadMi")
    void onBulkUploadClicked(SelectionEvent<Item> event) {
        presenter.doBulkUpload();
    }

    @UiHandler("importFromUrlMi")
    void onImportFromUrlClicked(SelectionEvent<Item> event) {
        presenter.doImportFromUrl();
    }

    //---------- File ----------
    @UiHandler("newWindowMi")
    void onNewWindowClicked(SelectionEvent<Item> event) {
        presenter.openNewWindow(false);
    }
    @UiHandler("newWindowAtLocMi")
    void onNewWindowAtLocClicked(SelectionEvent<Item> event) {
        presenter.openNewWindow(true);
    }

    @UiHandler("newFolderMi")
    void onNewFolderClicked(SelectionEvent<Item> event) {
        presenter.createNewFolder();
    }

    @UiHandler("duplicateMi")
    void onDuplicateClicked(SelectionEvent<Item> event){
        presenter.duplicateSelectedResource();
    }

    @UiHandler("addToSideBarMi")
    void onAddToSideBarClicked(SelectionEvent<Item> event){
        presenter.addSelectedFolderToSideBar();
    }

    @UiHandler("newPlainTextFileMi")
    void onNewPlainTextFileClicked(SelectionEvent<Item> event){
        presenter.createNewPlainTextFile();
    }

    @UiHandler("newTabularDataFileMi")
    void onNewTabularDataFileClicked(SelectionEvent<Item> event){
        presenter.createNewTabularDataFile();
    }

    @UiHandler("moveToTrashMi")
    void onMoveToTrashClicked(SelectionEvent<Item> event){
        presenter.moveSelectedDiskResourcesToTrash();
    }

    //----------- Edit -----------
    @UiHandler("renameMi")
    void onRenameClicked(SelectionEvent<Item> event){
        presenter.renameSelectedResource();
    }

    @UiHandler("editFileMi")
    void onEditFileClicked(SelectionEvent<Item> event){
        presenter.editSelectedFile();
    }

    @UiHandler("editCommentsMi")
    void onEditCommentClicked(SelectionEvent<Item> event){
        presenter.editSelectedResourceComments();
    }

    @UiHandler("editInfoTypeMi")
    void onEditInfoTypeClicked(SelectionEvent<Item> event){
        presenter.editSelectedResourceInfoType();
    }

    @UiHandler("metadataMi")
    void onEditMetadataClicked(SelectionEvent<Item> event){
        presenter.manageSelectedResourceMetadata();
    }

    @UiHandler("moveMi")
    void onMoveClicked(SelectionEvent<Item> event){
        presenter.moveSelectedDiskResources();
    }

    @UiHandler("deleteMi")
    void onDeleteClicked(SelectionEvent<Item> event){
        presenter.deleteSelectedResources();
    }

    //---------- Download --------------
    @UiHandler("simpleDownloadMi")
    void onSimpleDownloadClicked(SelectionEvent<Item> event) {
        presenter.doSimpleDownload();
    }

    @UiHandler("bulkDownloadMi")
    void onBulkDownloadClicked(SelectionEvent<Item> event) {
        presenter.doBulkDownload();
    }

    //--------- Sharing -------------
    @UiHandler("shareWithCollaboratorsMi")
    void onShareWithCollaboratorsClicked(SelectionEvent<Item> event) {
        presenter.manageSelectedResourceCollaboratorSharing();
    }

    @UiHandler("createPublicLinkMi")
    void onCreatePublicLinkClicked(SelectionEvent<Item> event){
        presenter.manageSelectedResourceDataLinks();
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

    //------------ Refresh --------------
    @UiHandler("refreshButton")
    void onRefreshClicked(SelectEvent event) {
        presenter.refreshSelectedFolder();
    }


    //------------- Trash ---------------
    @UiHandler("openTrashMi")
    void onOpenTrashClicked(SelectionEvent<Item> event) {
        presenter.selectTrashFolder();
    }

    @UiHandler("restoreMi")
    void onRestoreClicked(SelectionEvent<Item> event){
        presenter.restoreSelectedResources();
    }

    @UiHandler("emptyTrashMi")
    void onEmptyTrashClicked(SelectionEvent<Item> event) {
        presenter.emptyTrash();
    }

    @Override
    public DiskResourceSearchField getSearchField() {
        // TODO CORE-5300 This class will listen for events on this field, here.
        return searchField;
    }
}
