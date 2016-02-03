package org.iplantc.de.client.sharing;

import org.iplantc.de.client.models.diskResources.PermissionValue;

public interface SharingPresenter extends org.iplantc.de.commons.client.presenter.Presenter {

    interface Appearance {

        String sharingCompleteMsg();
    }

    void loadResources();

    void loadPermissions();

    PermissionValue getDefaultPermissions();

    void processRequest();

}
