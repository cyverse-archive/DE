package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.apps.client.AdminAppsView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.apps.AppsMessages;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AdminAppsViewDefaultAppearance implements AdminAppsView.AdminAppsViewAppearance {
    final BelphegorDisplayStrings displayStrings;
    final IplantDisplayStrings iplantDisplayStrings;
    final AppsMessages appsMessages;

    public AdminAppsViewDefaultAppearance() {
        this(GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<AppsMessages> create(AppsMessages.class));
    }
    AdminAppsViewDefaultAppearance(final BelphegorDisplayStrings displayStrings,
                                   final IplantDisplayStrings iplantDisplayStrings,
                                   final AppsMessages appsMessages) {
        this.displayStrings = displayStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.appsMessages = appsMessages;
    }

    @Override
    public String avgUserRatingColumnLabel() {
        return displayStrings.avgUserRatingColumnLabel();
    }

    @Override
    public int avgUserRatingColumnWidth() {
        return 40;
    }

    @Override
    public int integratedByColumnWidth() {
        return 130;
    }

    @Override
    public String integratedBy() {
        return appsMessages.integratedBy();
    }

    @Override
    public String nameColumnLabel() {
        return iplantDisplayStrings.name();
    }

    @Override
    public int nameColumnWidth() {
        return 180;
    }
}
