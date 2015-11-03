package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.FolderSelectDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

public class FolderSelectDialogDefaultAppearance implements FolderSelectDialog.FolderSelectDialogAppearance {

    private final DiskResourceMessages messages;

    public FolderSelectDialogDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    FolderSelectDialogDefaultAppearance(final DiskResourceMessages messages) {
        this.messages = messages;
    }
    @Override
    public String headerText() {
        return messages.folderSelectDialogHeaderText();
    }

    @Override
    public String selectorFieldLabel() {
        return messages.selectedFolder();
    }
}
