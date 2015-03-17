package org.iplantc.de.theme.base.client.apps.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppIntegratorCell;
import org.iplantc.de.theme.base.client.apps.AppSearchHighlightAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AppIntegratorCellDefaultAppearance implements AppIntegratorCell.AppIntegratorCellAppearance {

    private final AppSearchHighlightAppearance highlightAppearance;

    public AppIntegratorCellDefaultAppearance() {
        this(GWT.<AppSearchHighlightAppearance> create(AppSearchHighlightAppearance.class));
    }

    AppIntegratorCellDefaultAppearance(final AppSearchHighlightAppearance highlightAppearance) {
        this.highlightAppearance = highlightAppearance;
    }

    @Override
    public void render(SafeHtmlBuilder sb, String value, String pattern) {
        String highlightText = highlightAppearance.highlightText(value, pattern);
        sb.append(SafeHtmlUtils.fromTrustedString(highlightText));
    }
}
