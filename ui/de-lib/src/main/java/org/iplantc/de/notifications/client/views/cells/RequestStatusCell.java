package org.iplantc.de.notifications.client.views.cells;

import org.iplantc.de.client.models.tool.Tool;
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
public class RequestStatusCell extends AbstractCell<String> {

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        String qtip = ""; //$NON-NLS-1$

        if (value != null &&  (value.equalsIgnoreCase(ToolRequestStatus.Completion.toString()) || value.equalsIgnoreCase(ToolRequestStatus.Evaluation.toString())
                || value.equalsIgnoreCase(ToolRequestStatus.Failed.toString()) || value.equalsIgnoreCase(ToolRequestStatus.Installation.toString())
                || value.equalsIgnoreCase(ToolRequestStatus.Pending.toString()) || value.equalsIgnoreCase(ToolRequestStatus.Submitted.toString())
                || value.equalsIgnoreCase(ToolRequestStatus.Validation.toString())) ) {
            qtip = Format.substitute("qtip=\"{0}\"", ToolRequestStatus.valueOf(value).getHelpText()); //$NON-NLS-1$
            sb.appendHtmlConstant(Format.substitute("<div {0}>{1}</div>", qtip, value));
        } else if (value != null) {
            qtip = Format.substitute("qtip=\"{0}\"", ToolRequestStatus.valueOf(ToolRequestStatus.Other.toString()).getHelpText()); //$NON-NLS-1$
            sb.appendHtmlConstant(Format.substitute("<div {0}>{1}</div>", qtip, value));
        } else{
            sb.appendHtmlConstant(Format.substitute("<div>{0}</div>", value));
        }

        //$NON-NLS-1$
    }

}
