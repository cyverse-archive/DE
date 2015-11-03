package org.iplantc.de.apps.client.views.grid.cells;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class AppIntegratorCell extends AbstractCell<String> {
    public interface AppIntegratorCellAppearance {

        void render(SafeHtmlBuilder sb, String value, String pattern);
    }

    private final AppIntegratorCellAppearance appearance;
    private String pattern;

    public AppIntegratorCell() {
        this(GWT.<AppIntegratorCellAppearance> create(AppIntegratorCellAppearance.class));
    }

    public AppIntegratorCell(final AppIntegratorCellAppearance appearance) {
        this.appearance = appearance;
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        appearance.render(sb, value, pattern);

    }

    public void setSearchRegexPattern(final String pattern) {
        this.pattern = pattern;
    }
}
