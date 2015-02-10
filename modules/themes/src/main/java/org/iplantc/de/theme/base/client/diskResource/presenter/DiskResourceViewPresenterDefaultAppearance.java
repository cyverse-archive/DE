package org.iplantc.de.theme.base.client.diskResource.presenter;

import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.theme.base.client.diskResource.DiskResourceMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class DiskResourceViewPresenterDefaultAppearance implements DiskResourceView.Presenter.Appearance {
    private final DiskResourceMessages diskResourceMessages;

    public DiskResourceViewPresenterDefaultAppearance() {
        this(GWT.<DiskResourceMessages> create(DiskResourceMessages.class));
    }

    DiskResourceViewPresenterDefaultAppearance(DiskResourceMessages diskResourceMessages) {
        this.diskResourceMessages = diskResourceMessages;
    }

    @Override
    public String unsupportedCogeInfoType() {
        return diskResourceMessages.unsupportedCogeInfoType();
    }

    @Override
    public String unsupportedEnsemblInfoType() {
        return diskResourceMessages.unsupportedEnsemblInfoType();
    }

    @Override
    public String unsupportedTreeInfoType() {
        return diskResourceMessages.unsupportedTreeInfoType();
    }
}
