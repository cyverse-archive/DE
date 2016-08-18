package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.apps.client.views.grid.cells.AdminAppNameCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.apps.cells.AppNameCellDefaultAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class AdminAppNameCellDefaultAppearance extends AppNameCellDefaultAppearance
        implements AdminAppNameCell.AdminAppNameCellAppearance {
    private final BelphegorDisplayStrings displayStrings;

    @Override
    public void render(final SafeHtmlBuilder sb,
                       final App value,
                       final String searchPattern) {

        if (value.isDisabled()) {
            super.render(sb,
                         value,
                         resources.css().appDisabled(),
                         searchPattern,
                         appUnavailable(),
                         null);
        } else if(value.isBeta() != null && value.isBeta()) {
            super.render(sb,
                         value,
                         resources.css().appBeta(),
                         searchPattern,
                         displayStrings.editApp(),
                         null);
        } else if (!value.isPublic()) {
            super.render(sb,
                         value,
                         resources.css().appPrivate(),
                         searchPattern,
                         displayStrings.editApp(),
                         null);
        } else {
            super.render(sb,
                         value,
                         resources.css().appName(),
                         searchPattern,
                         displayStrings.editApp(),
                         null);
        }
    }

    public AdminAppNameCellDefaultAppearance() {
        this(GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class));
    }

    AdminAppNameCellDefaultAppearance(final BelphegorDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String editApp() {
        return displayStrings.editApp();
    }
}
