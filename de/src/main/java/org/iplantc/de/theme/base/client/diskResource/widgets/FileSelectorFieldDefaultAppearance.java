package org.iplantc.de.theme.base.client.diskResource.widgets;

import org.iplantc.de.diskResource.client.views.widgets.FileSelectorField;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

public class FileSelectorFieldDefaultAppearance extends AbstractDiskResourceSelectorDefaultAppearance
                                                implements FileSelectorField.FileSelectorFieldAppearance {
    private final DiskResourceMessages diskResourceMessages;

    public FileSelectorFieldDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    FileSelectorFieldDefaultAppearance(final DiskResourceMessages diskResourceMessages) {
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String selectAFile() {
        return diskResourceMessages.selectAFile();
    }
}
