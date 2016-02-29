package org.iplantc.de.theme.base.client.notifications.cells;

import org.iplantc.de.notifications.client.views.cells.RequestStatusCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.util.Format;

/**
 * @author aramsey
 */
public class RequestStatusCellDefaultAppearance implements RequestStatusCell.RequestStatusCellAppearance {
    @Override
    public void render(Cell.Context context, String helpText, String value, SafeHtmlBuilder sb) {
        String qtip = "";

        if (helpText != null) {
            qtip = Format.substitute("qtip=\"{0}\"", helpText);
            sb.appendHtmlConstant(Format.substitute("<div {0}>{1}</div>", qtip, value));
        } else{
            sb.appendHtmlConstant(Format.substitute("<div>{0}</div>", value));
        }
    }
}
