package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.FileFolderSelectDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class FileFolderSelectDialogDefaultAppearance implements FileFolderSelectDialog.FileFolderSelectDialogAppearance {

    private final DiskResourceMessages messages;

    public FileFolderSelectDialogDefaultAppearance(){
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));

    }

    FileFolderSelectDialogDefaultAppearance(final DiskResourceMessages messages){
        this.messages = messages;
    }

    @Override
    public String getHeaderText() {
        return messages.fileFolderDialogHeaderText();
    }

    @Override
    public String getHeight() {
        return "480";
    }

    @Override
    public int getMinHeight() {
        return 480;
    }

    @Override
    public int getMinWidth() {
        return 640;
    }

    @Override
    public String getWidth() {
        return "640";
    }

    @Override
    public String selectorFieldLabel() {
        return messages.selectedItem();
    }
}
