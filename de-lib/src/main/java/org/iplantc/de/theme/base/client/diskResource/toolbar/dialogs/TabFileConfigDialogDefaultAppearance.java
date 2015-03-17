package org.iplantc.de.theme.base.client.diskResource.toolbar.dialogs;

import org.iplantc.de.diskResource.client.views.toolbar.dialogs.TabFileConfigDialog;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class TabFileConfigDialogDefaultAppearance implements TabFileConfigDialog.TabFileConfigDialogAppearance {
    private final DiskResourceMessages diskResourceMessages;

    public TabFileConfigDialogDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    TabFileConfigDialogDefaultAppearance(final DiskResourceMessages diskResourceMessages) {
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String heading() {
        return diskResourceMessages.tabFileConfigDialogHeading();
    }

    @Override
    public String commaRadioLabel() {
        return diskResourceMessages.tabFileConfigDialogCommaRadioLabel();
    }

    @Override
    public String tabRadioLabel() {
        return diskResourceMessages.tabFileConfigDialogTabRadioLabel();
    }
}
