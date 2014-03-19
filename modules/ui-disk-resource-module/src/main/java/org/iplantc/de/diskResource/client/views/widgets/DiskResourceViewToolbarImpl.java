package org.iplantc.de.diskResource.client.views.widgets;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.search.views.DiskResourceSearchField;
import org.iplantc.de.diskResource.client.views.dialogs.CreateFolderDialog;
import org.iplantc.de.diskResource.client.views.dialogs.RenameFileDialog;
import org.iplantc.de.diskResource.client.views.dialogs.RenameFolderDialog;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class DiskResourceViewToolbarImpl implements DiskResourceViewToolbar {

    @UiTemplate("DiskResourceViewToolbar.ui.xml")
    interface DiskResourceViewToolbarUiBinder extends UiBinder<Widget, DiskResourceViewToolbarImpl> {
    }

    private static DiskResourceViewToolbarUiBinder BINDER = GWT
            .create(DiskResourceViewToolbarUiBinder.class);

    private DiskResourceViewToolbar.Presenter presenter;
    private final Widget widget;

    @UiField
    TextButton uploads;

    @UiField
    MenuItem bulkUploadButton;

    @UiField
    MenuItem simpleUploadButton;

    @UiField
    MenuItem importButton;

    @UiField
    MenuItem newFolderButton;

    @UiField
    TextButton refreshButton;

    @UiField
    TextButton downloads;

    @UiField
    MenuItem simpleDownloadButton;

    @UiField
    MenuItem bulkDownloadButton;

    @UiField
    MenuItem renameButton;
    
    @UiField
    MenuItem moveButton;

    @UiField
    MenuItem deleteButton;
    
    @UiField
    MenuItem newFileButton;

    @UiField
    MenuItem metadataButton;

    @UiField
    TextButton shareButton;

    @UiField
    DiskResourceSearchField searchField;

    @UiField
    MenuItem emptyTrash;

    @UiField
    MenuItem restore;

    @UiField
    MenuItem share;

    @UiField
    MenuItem dataLink;

    @UiField
    TextButton edit;
    
    @UiField
    TextButton newButton;

    public DiskResourceViewToolbarImpl() {
        widget = BINDER.createAndBindUi(this);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void setPresenter(DiskResourceViewToolbar.Presenter presenter) {
        this.presenter = presenter;
    }
    
    @UiHandler("bulkUploadButton")
    void onBulkUploadClicked(SelectionEvent<Item> event) {
        presenter.doBulkUpload();
    }

    @UiHandler("simpleUploadButton")
    void onSimpleUploadClicked(SelectionEvent<Item> event) {
        presenter.doSimpleUpload();
    }

    @UiHandler("importButton")
    void onImportClicked(SelectionEvent<Item> event) {
        presenter.doImport();
    }

    @UiHandler("newFolderButton")
    void onNewFolderClicked(SelectionEvent<Item> event) {
        CreateFolderDialog dlg = new CreateFolderDialog(presenter.getSelectedFolder(), presenter);
        dlg.show();
    }

    @UiHandler("refreshButton")
    void onRefreshClicked(SelectEvent event) {
        presenter.doRefresh(presenter.getSelectedFolder());
    }

    @UiHandler("simpleDownloadButton")
    void onSimpleDownloadClicked(SelectionEvent<Item> event) {
        presenter.doSimpleDownload();
    }

    @UiHandler("bulkDownloadButton")
    void onBulkDownloadClicked(SelectionEvent<Item> event) {
        presenter.doBulkDownload();
    }

    @UiHandler("share")
    void onShareClicked(SelectionEvent<Item> event) {
        presenter.doShare();
    }

    @UiHandler("renameButton")
    void onRenameClicked(SelectionEvent<Item> event) {
        if (!presenter.getSelectedDiskResources().isEmpty()
                && (presenter.getSelectedDiskResources().size() == 1)) {
            DiskResource dr = presenter.getSelectedDiskResources().iterator().next();
            if (dr instanceof File) {
                RenameFileDialog dlg = new RenameFileDialog((File)dr, presenter);
                dlg.show();

            } else {
                RenameFolderDialog dlg = new RenameFolderDialog((Folder)dr, presenter);
                dlg.show();

            }
        } else if (presenter.getSelectedFolder() != null) {
            RenameFolderDialog dlg = new RenameFolderDialog(presenter.getSelectedFolder(), presenter);
            dlg.show();
        }
    }
    
    @UiHandler("newFileButton")
    void onNewFileClicked(SelectionEvent<Item> event) {
        presenter.onNewFile();
    }
    
    @UiHandler("moveButton")
    void onMoveClicked(SelectionEvent<Item> event) {
        presenter.onMove();
    }

    @UiHandler("deleteButton")
    void onDeleteClicked(SelectionEvent<Item> event) {
        presenter.requestDelete();
    }

    @UiHandler("metadataButton")
    void onMetadataClicked(SelectionEvent<Item> event) {
        presenter.doMetadata();
    }



    @UiHandler("dataLink")
    void onDataLinkClicked(SelectionEvent<Item> event) {
        presenter.doDataLinks();
    }

    @UiHandler("emptyTrash")
    void onEmptyTrashClicked(SelectionEvent<Item> event) {
        final ConfirmMessageBox cmb = new ConfirmMessageBox(I18N.DISPLAY.emptyTrash(),
                I18N.DISPLAY.emptyTrashWarning());
        cmb.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if (cmb.getHideButton() == cmb.getButtonById(PredefinedButton.YES.name())) {
                    presenter.emptyTrash();
                }
            }
        });

        cmb.setWidth(300);
        cmb.show();
    }

    @UiHandler("restore")
    void onRestoreClicked(SelectionEvent<Item> event) {
        presenter.restore();
    }

    @Override
    public void setUploadsEnabled(boolean enabled) {
        uploads.setEnabled(enabled);
    }

    @Override
    public void setBulkUploadEnabled(boolean enabled) {
        bulkUploadButton.setEnabled(enabled);
    }

    @Override
    public void setSimpleUploadEnabled(boolean enabled) {
        simpleUploadButton.setEnabled(enabled);
    }

    @Override
    public void setImportButtonEnabled(boolean enabled) {
        importButton.setEnabled(enabled);
    }
    
    @Override
    public void setNewButtonEnabled(boolean enabled) {
        newButton.setEnabled(enabled);
    }
    
    @Override
    public void setNewFileButtonEnabled(boolean enabled) {
        newFileButton.setEnabled(enabled);
    }

    @Override
    public void setNewFolderButtonEnabled(boolean enabled) {
        newFolderButton.setEnabled(enabled);
    }

    @Override
    public void setRefreshButtonEnabled(boolean enabled) {
        refreshButton.setEnabled(enabled);
    }

    @Override
    public void setDownloadsEnabled(boolean enabled) {
        downloads.setEnabled(enabled);
    }

    @Override
    public void setSimpleDowloadButtonEnabled(boolean enabled) {
        simpleDownloadButton.setEnabled(enabled);
    }

    @Override
    public void setBulkDownloadButtonEnabled(boolean enabled) {
        bulkDownloadButton.setEnabled(enabled);
    }

    @Override
    public void setRenameButtonEnabled(boolean enabled) {
        renameButton.setEnabled(enabled);
    }
    
    @Override
    public void setMoveButtonEnabled(boolean enabled) {
        moveButton.setEnabled(enabled);
    }

    @Override
    public void setDeleteButtonEnabled(boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    @Override
    public void setShareButtonEnabled(boolean enabled) {
        shareButton.setEnabled(enabled);
    }

    @Override
    public void setShareMenuItemEnabled(boolean enabled) {
        share.setEnabled(enabled);
    }

    @Override
    public void setDataLinkMenuItemEnabled(boolean enabled) {
        dataLink.setEnabled(enabled);
    }

    @Override
    public void setRestoreMenuItemEnabled(boolean enabled) {
        restore.setEnabled(enabled);
    }

    @Override
    public void setMetaDatMenuItemEnabled(boolean canEditMetadata) {
        metadataButton.setEnabled(canEditMetadata);
    }

    @Override
    public void setEditEnabled(boolean canEdit) {
        edit.setEnabled(canEdit);
    }

    @Override
    public DiskResourceSearchField getSearchField() {
        return searchField;
    }
}
