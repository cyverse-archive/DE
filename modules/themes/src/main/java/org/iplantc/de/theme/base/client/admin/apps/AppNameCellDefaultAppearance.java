package org.iplantc.de.theme.base.client.admin.apps;

import org.iplantc.de.admin.desktop.client.apps.views.cells.AppNameCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.theme.base.client.admin.BelphegorDisplayStrings;
import org.iplantc.de.theme.base.client.applications.cells.AppHyperlinkCellDefaultAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AppNameCellDefaultAppearance extends AppHyperlinkCellDefaultAppearance implements AppNameCell.AppNameCellAppearance {
    private final BelphegorDisplayStrings displayStrings;

    @Override
    public void render(SafeHtmlBuilder sb, App value) {
        if(!value.isDisabled()){
            super.render(sb,
                         value,
                         resources.css().appName(),
                         SafeHtmlUtils.fromString(value.getName()),
                         displayStrings.editApp(),
                         null);
        } else {
            super.render(sb,
                         value,
                         resources.css().appDisabled(),
                         SafeHtmlUtils.fromString(value.getName()),
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
