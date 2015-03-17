package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.SaveAsDialog;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

/**
 *
 */
public class SaveAsDialogDefaultAppearance implements SaveAsDialog.SaveAsDialogAppearance{
    private final IplantDisplayStrings iplantDisplayStrings;
    private final DiskResourceMessages diskResourceMessages;

    public SaveAsDialogDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    SaveAsDialogDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                  final DiskResourceMessages diskResourceMessages) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String dialogHeight() {
        return "480px";
    }

    @Override
    public String dialogWidth() {
        return "640px";
    }

    @Override
    public String fileName() {
        return diskResourceMessages.fileName();
    }

    @Override
    public String saveAsHeadingText() {
        return iplantDisplayStrings.saveAs();
    }

    @Override
    public String selectedFolder() {
        return diskResourceMessages.selectedFolder();
    }
}
