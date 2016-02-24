package org.iplantc.de.theme.base.client.admin.permIdRequest;

import org.iplantc.de.admin.desktop.client.permIdRequest.views.PermanentIdRequestView.PermanentIdRequestPresenterAppearance;

import com.google.gwt.core.client.GWT;

public class PermanentIdRequestPresenterDefaultAppearance implements
                                                         PermanentIdRequestPresenterAppearance {

    private final PermIdRequestDisplayStrings displayStrings;

    public PermanentIdRequestPresenterDefaultAppearance() {
        this(GWT.<PermIdRequestDisplayStrings> create(PermIdRequestDisplayStrings.class));
    }

    public PermanentIdRequestPresenterDefaultAppearance(PermIdRequestDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String createPermIdSucess() {
        return displayStrings.createPermIdSucess();
    }

    @Override
    public String createPermIdFailure() {
        return displayStrings.createPermIdFailure();
    }

    @Override
    public String metadataSaveError() {
        return displayStrings.metadataSaveError();
    }

    @Override
    public String requestLoadFailure() {
        return displayStrings.requestLoadFailure();
    }

    @Override
    public String statusUpdateFailure() {
        return displayStrings.statusUpdateFailure();
    }

    @Override
    public String statusUpdateSuccess() {
        return displayStrings.statusUpdateSuccess();
    }

    @Override
    public String updateStatus() {
        return displayStrings.updateStatus();
    }

    @Override
    public String update() {
        return displayStrings.update();
    }


}
