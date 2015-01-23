package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;

import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * @author jstroot
 */
public final class SaveAsDialogCancelSelectHandler implements SelectEvent.SelectHandler {
    private final IsMaskable maskable;
    private final SaveAsDialog saveDialog;

    public SaveAsDialogCancelSelectHandler(final IsMaskable maskable,
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
