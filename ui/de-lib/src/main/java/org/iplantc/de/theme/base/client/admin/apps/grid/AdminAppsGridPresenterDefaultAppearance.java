package org.iplantc.de.theme.base.client.admin.apps.grid;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorErrorStrings;
import org.iplantc.de.theme.base.client.apps.grid.AppsGridViewDefaultAppearance;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AdminAppsGridPresenterDefaultAppearance extends AppsGridViewDefaultAppearance implements AdminAppsGridView.Presenter.Appearance {
    private final BelphegorDisplayStrings displayStrings;
    private final BelphegorErrorStrings errorStrings;
    private final IplantDisplayStrings iplantDisplayStrings;

    public AdminAppsGridPresenterDefaultAppearance() {
        this(GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<BelphegorErrorStrings> create(BelphegorErrorStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }

    AdminAppsGridPresenterDefaultAppearance(final BelphegorDisplayStrings displayStrings,
                                            final BelphegorErrorStrings errorStrings,
                                            final IplantDisplayStrings iplantDisplayStrings) {
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
    }

    @Override
    public String confirmDeleteAppTitle() {
        return displayStrings.confirmDeleteAppTitle();
    }

    @Override
    public String confirmDeleteAppWarning() {
        return iplantDisplayStrings.warning();
    }

    @Override
    public String deleteAppLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String deleteApplicationError(String name) {
        return errorStrings.deleteApplicationError(name);
    }

    @Override
    public String restoreAppFailureMsg(String name) {
        return errorStrings.restoreAppFailureMsg(name);
    }

    @Override
    public String restoreAppFailureMsgTitle() {
        return errorStrings.restoreAppFailureMsgTitle();
    }

    @Override
    public String restoreAppLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String restoreAppSuccessMsg(String name, String s) {
        return displayStrings.restoreAppSuccessMsg(name, s);
    }

    @Override
    public String restoreAppSuccessMsgTitle() {
        return displayStrings.restoreAppSuccessMsgTitle();
    }

    @Override
    public String saveAppLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String updateApplicationError() {
        return errorStrings.updateApplicationError();
    }

    @Override
    public String updateDocumentationSuccess() {
        return displayStrings.updateDocumentationSuccess();
    }

    @Override
    public String betaTagAddedSuccess() {
        return displayStrings.betaTagAddedSuccess();
    }

    @Override
    public String betaTagRemovedSuccess() {
        return displayStrings.betaTagRemovedSuccess();
    }
}
