package org.iplantc.de.admin.apps.client.views.grid.cells;

import org.iplantc.de.apps.client.views.grid.cells.AppNameCell;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * @author jstroot
 */
public class AdminAppNameCell extends AppNameCell {

    public interface AdminAppNameCellAppearance extends AppNameCellAppearance {

        String editApp();

        void render(SafeHtmlBuilder sb, App value, String pattern);
    }

    private AdminAppNameCellAppearance appearance = GWT.create(AdminAppNameCellAppearance.class);

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        appearance.render(sb, value, pattern);
    }
}
