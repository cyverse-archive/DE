package org.iplantc.de.theme.base.client.diskResource.dialogs;

import org.iplantc.de.diskResource.client.views.dialogs.BulkMetadataDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

public class BulkMetadataDialogDefaultAppearance implements BulkMetadataDialog.Appearance {

    private final DiskResourceMessages diskResourceMessages = GWT.<DiskResourceMessages> create(DiskResourceMessages.class);

    @Override
    public String heading() {
        return diskResourceMessages.bulkMetadataHeading();
    }

    @Override
    public String selectMetadataFile() {
        return diskResourceMessages.selectMetadataFile();
    }

    @Override
    public String selectTemplate() {
        return diskResourceMessages.selectTemplate();
    }

    @Override
    public String applyBulkMetadata() {
        return diskResourceMessages.applyBulkMetadata();
    }

    @Override
    public String uploadMetadata() {
        return diskResourceMessages.uploadMetadata();
    }
}
