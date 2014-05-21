package org.iplantc.admin.belphegor.client.apps.views.cells;

import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class AppNameCell extends AppHyperlinkCell {

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
            sb.append(templates.cell(resources.css().appName(), safeHtmlName, I18N.DISPLAY.editApp(),
                    ELEMENT_NAME));
        } else {
            sb.append(templates.cell(resources.css().appDisabled(), safeHtmlName,
                    I18N.DISPLAY.appUnavailable(), ELEMENT_NAME));
        }

    }
}
