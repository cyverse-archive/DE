package org.iplantc.de.theme.base.client.diskResource.widgets;

import org.iplantc.de.diskResource.client.views.widgets.FolderSelectorField;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

public class FolderSelectorFieldDefaultAppearance extends AbstractDiskResourceSelectorDefaultAppearance
                                                  implements FolderSelectorField.FolderSelectorFieldAppearance{
    private final DiskResourceMessages diskResourceMessages;

    public FolderSelectorFieldDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    FolderSelectorFieldDefaultAppearance(final DiskResourceMessages diskResourceMessages) {
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String selectAFolder() {
        return diskResourceMessages.selectAFolder();
    }
}
