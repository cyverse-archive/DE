package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.desktop.client.apps.views.cells.AppNameCell;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;

import com.google.gwt.core.client.GWT;

/**
 * @author jstroot
 */
public class AppNameCellDefaultAppearance implements AppNameCell.AppNameCellAppearance {
    private final IplantDisplayStrings iplantDisplayStrings;
    private final BelphegorDisplayStrings displayStrings;

    public AppNameCellDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class));
    }

    AppNameCellDefaultAppearance(final IplantDisplayStrings iplantDisplayStrings,
                                 final BelphegorDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String appUnavailable() {
        return iplantDisplayStrings.appUnavailable();
    }

    @Override
    public String editApp() {
        return displayStrings.editApp();
    }
}
