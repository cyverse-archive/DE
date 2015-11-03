package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.apps.client.views.grid.cells.AppNameCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.apps.cells.AppHyperlinkCellDefaultAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class AppNameCellDefaultAppearance extends AppHyperlinkCellDefaultAppearance implements AppNameCell.AppNameCellAppearance {
    private final BelphegorDisplayStrings displayStrings;

    @Override
    public void render(final SafeHtmlBuilder sb,
                       final App value,
                       final String searchPattern) {
        if(!value.isDisabled()){
            super.render(sb,
                         value,
                         resources.css().appName(),
                         searchPattern,
                         displayStrings.editApp(),
                         null);
        } else {
            super.render(sb,
                         value,
                         resources.css().appDisabled(),
                         searchPattern,
                         appUnavailable(),
                         null);
        }
    }

    public AppNameCellDefaultAppearance() {
        this(GWT.<BelphegorDisplayStrings> create(BelphegorDisplayStrings.class));
    }

    AppNameCellDefaultAppearance(final BelphegorDisplayStrings displayStrings) {
        this.displayStrings = displayStrings;
    }

    @Override
    public String editApp() {
        return displayStrings.editApp();
    }
}
