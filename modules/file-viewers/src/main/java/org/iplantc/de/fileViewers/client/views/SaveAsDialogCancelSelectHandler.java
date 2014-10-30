package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;

import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

final class SaveAsDialogCancelSelectHandler implements SelectEvent.SelectHandler {
    private final Component maskable;
    private final SaveAsDialog saveDialog;

    public SaveAsDialogCancelSelectHandler(final Component maskable,
                                           final SaveAsDialog saveDialog) {
        this.maskable = maskable;
        this.saveDialog = saveDialog;
    }

    @Override
    public void onSelect(SelectEvent event) {
        saveDialog.hide();
        maskable.unmask();
    }
}
