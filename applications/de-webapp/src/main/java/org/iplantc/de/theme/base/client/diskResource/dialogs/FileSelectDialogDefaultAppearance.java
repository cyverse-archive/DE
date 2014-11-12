package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.FileSelectDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.shared.GWT;

public class FileSelectDialogDefaultAppearance implements FileSelectDialog.FileSelectDialogAppearance{

    private final DiskResourceMessages messages;

    public FileSelectDialogDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    FileSelectDialogDefaultAppearance(final DiskResourceMessages messages) {
        this.messages = messages;
    }
    @Override
    public String headerText() {
        return messages.fileSelectDialogHeaderText();
    }

    @Override
    public String selectorFieldLabel() {
        return messages.selectedFile();
    }
}
