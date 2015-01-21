package org.iplantc.de.admin.desktop.client.apps.views.cells;

import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

/**
 * @author jstroot
 */
public class AppNameCell extends AppHyperlinkCell {

    public interface AppNameCellAppearance {

        String appUnavailable();

        String editApp();
    }

    private AppNameCellAppearance appearance = GWT.create(AppNameCellAppearance.class);

    public AppNameCell(AppsView view) {
        super(view);
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }
        sb.appendHtmlConstant("&nbsp;");
        SafeHtml safeHtmlName = SafeHtmlUtils.fromString(value.getName());
        if (!value.isDisabled()) {
            // FIXME Factor out resources into theme once app module is completely themed.
            sb.append(templates.cell(resources.css().appName(), safeHtmlName, appearance.editApp(),
                                     ELEMENT_NAME));
        } else {
            sb.append(templates.cell(resources.css().appDisabled(), safeHtmlName,
                                     appearance.appUnavailable(), ELEMENT_NAME));
        }

    }
}
