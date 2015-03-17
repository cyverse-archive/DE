package org.iplantc.de.theme.base.client.apps.grid;

import org.iplantc.de.apps.client.AppsGridView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AppsGridViewDefaultAppearance implements AppsGridView.AppsGridAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final AppsMessages appsMessages;

    public AppsGridViewDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<AppsMessages> create(AppsMessages.class));
    }

    AppsGridViewDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                  final AppsMessages appsMessages) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.appsMessages = appsMessages;
    }

    @Override
    public String appLaunchWithoutToolError() {
        return appsMessages.appLaunchWithoutToolError();
    }

    @Override
    public String appRemoveFailure() {
        return appsMessages.appRemoveFailure();
    }

    @Override
    public String beforeAppSearchLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String favServiceFailure() {
        return appsMessages.favServiceFailure();
    }

    @Override
    public String getAppsLoadingMask() {
        return iplantDisplayStrings.loadingMask();
    }

    @Override
    public String integratedByColumnLabel() {
        return appsMessages.integratedBy();
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public String ratingColumnLabel() {
        return appsMessages.rating();
    }

    @Override
    public String searchAppResultsHeader(String searchText, int total) {
        return appsMessages.searchAppResultsHeader(searchText, total);
    }
}
