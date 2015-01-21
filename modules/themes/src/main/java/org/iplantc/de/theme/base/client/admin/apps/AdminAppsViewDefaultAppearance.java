package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.desktop.client.apps.views.AdminAppsView;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AdminAppsViewDefaultAppearance implements AdminAppsView.AdminAppsViewAppearance {
    final BelphegorDisplayStrings displayStrings;
    final IplantDisplayStrings iplantDisplayStrings;

    public AdminAppsViewDefaultAppearance() {
        this(GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class),
             GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class));
    }
    AdminAppsViewDefaultAppearance(final BelphegorDisplayStrings displayStrings,
                                   final IplantDisplayStrings iplantDisplayStrings) {
        this.displayStrings = displayStrings;
        this.iplantDisplayStrings = iplantDisplayStrings;
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
    public String integratedby() {
        return iplantDisplayStrings.integratedby();
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
