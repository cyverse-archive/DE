package org.iplantc.de.theme.base.client.diskResource.sharing.presenter;

import org.iplantc.de.client.sharing.SharingPresenter;
import org.iplantc.de.theme.base.client.diskResource.sharing.SharingMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class SharingPresenterDefaultAppearance implements SharingPresenter.Appearance {
    private final SharingMessages sharingMessages;

    public SharingPresenterDefaultAppearance() {
        this(GWT.<SharingMessages> create(SharingMessages.class));
    }

    SharingPresenterDefaultAppearance(final SharingMessages sharingMessages) {
        this.sharingMessages = sharingMessages;
    }

    @Override
    public String sharingCompleteMsg() {
        return sharingMessages.sharingCompleteMsg();
    }
}
