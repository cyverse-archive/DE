package org.iplantc.de.client.notifications.views.cells;

import org.iplantc.de.client.models.toolRequest.ToolRequestStatus;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.util.Format;

/**
 * A Cell for displaying a ToolRequestStatus in a grid with its associated help text as a QuickTip.
 * 
 * @author psarando
 * 
 */
public class ToolRequestStatusCell extends AbstractCell<ToolRequestStatus> {

    @Override
    public void render(Context context, ToolRequestStatus value, SafeHtmlBuilder sb) {
        String qtip = ""; //$NON-NLS-1$

        if (value != null) {
            qtip = Format.substitute("qtip=\"{0}\"", value.getHelpText()); //$NON-NLS-1$
        }

        sb.appendHtmlConstant(Format.substitute("<div {0}>{1}</div>", qtip, value)); //$NON-NLS-1$
    }

}
