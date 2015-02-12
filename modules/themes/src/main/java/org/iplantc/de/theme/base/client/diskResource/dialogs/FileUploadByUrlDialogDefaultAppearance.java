package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.FileUploadByUrlDialog;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class FileUploadByUrlDialogDefaultAppearance implements FileUploadByUrlDialog.FileUploadByUrlDialogAppearance {
    private final DiskResourceMessages diskResourceMessages;
    private final IplantErrorStrings iplantErrorStrings;

    public FileUploadByUrlDialogDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class),
             GWT.<IplantErrorStrings> create(IplantErrorStrings.class));
    }

    FileUploadByUrlDialogDefaultAppearance(final DiskResourceMessages diskResourceMessages,
                                           final IplantErrorStrings iplantErrorStrings) {
        this.diskResourceMessages = diskResourceMessages;
        this.iplantErrorStrings = iplantErrorStrings;
    }

    @Override
    public String fileExist() {
        return iplantErrorStrings.fileExist();
    }

    @Override
    public String importLabel() {
        return diskResourceMessages.importLabel();
    }

    @Override
    public String uploadingToFolder(String path) {
        return diskResourceMessages.uploadingToFolder(path);
    }

    @Override
    public String urlImport() {
        return diskResourceMessages.urlImport();
    }

    @Override
    public String urlPrompt() {
        return diskResourceMessages.urlPrompt();
    }
}
