package org.iplantc.de.theme.base.client.diskResource.sharing.presenter;

import org.iplantc.de.diskResource.client.DataSharingView;
import org.iplantc.de.theme.base.client.diskResource.sharing.SharingMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class DataSharingPresenterDefaultAppearance implements DataSharingView.Presenter.Appearance {
    private final SharingMessages sharingMessages;

    public DataSharingPresenterDefaultAppearance() {
        this(GWT.<SharingMessages> create(SharingMessages.class));
    }

    DataSharingPresenterDefaultAppearance(final SharingMessages sharingMessages) {
        this.sharingMessages = sharingMessages;
    }

    @Override
    public String sharingCompleteMsg() {
        return sharingMessages.sharingCompleteMsg();
    }
}
