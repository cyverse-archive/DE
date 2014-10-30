package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.fileViewers.client.callbacks.FileSaveCallback;

import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

final class SaveAsDialogOkSelectHandler implements SelectEvent.SelectHandler {
    private final Component maskable;
    private final SaveAsDialog saveAsDialog;
    private final String savingMaskText;
    private final String editorContent;
    private final FileEditorServiceFacade fileEditorService;

    SaveAsDialogOkSelectHandler(final Component maskable,
                                final SaveAsDialog saveAsDialog,
                                final String savingMaskText,
                                final String editorContent,
                                final FileEditorServiceFacade fileEditorService) {
        this.maskable = maskable;
        this.saveAsDialog = saveAsDialog;
        this.savingMaskText = savingMaskText;
        this.editorContent = editorContent;
        this.fileEditorService = fileEditorService;
    }

    @Override
    public void onSelect(SelectEvent event) {
        if (!saveAsDialog.isValid()) {
            return;
        }

        maskable.mask(savingMaskText);
        String destination = saveAsDialog.getSelectedFolder().getPath() + "/"
                                 + saveAsDialog.getFileName();
        fileEditorService.uploadTextAsFile(destination,
                                           editorContent,
                                           true,
                                           new FileSaveCallback(destination,
                                                                true,
                                                                maskable));
        saveAsDialog.hide();
    }
}
