package org.iplantc.de.notifications.client.views.cells;

import org.iplantc.de.client.models.toolRequest.ToolRequestStatus;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * A Cell for displaying a ToolRequestStatus in a grid with its associated help text as a QuickTip.
 * 
 * @author psarando
 * 
 */
public class RequestStatusCell extends AbstractCell<String> {

    public interface RequestStatusCellAppearance {
        void render(Context context, String helpText, String value, SafeHtmlBuilder sb);
    }

    private RequestStatusCellAppearance appearance = GWT.create(RequestStatusCellAppearance.class);

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {

        String helpText;

        if (value != null &&  (value.equalsIgnoreCase(ToolRequestStatus.Completion.toString()) || value.equalsIgnoreCase(ToolRequestStatus.Evaluation.toString())
                               || value.equalsIgnoreCase(ToolRequestStatus.Failed.toString())
                               || value.equalsIgnoreCase(ToolRequestStatus.Installation.toString())
                               || value.equalsIgnoreCase(ToolRequestStatus.Pending.toString())
                               || value.equalsIgnoreCase(ToolRequestStatus.Submitted.toString()) || value
                                       .equalsIgnoreCase(ToolRequestStatus.Validation.toString()))) {
            helpText = ToolRequestStatus.valueOf(value).getHelpText();

        } else if (value != null) {
            helpText = ToolRequestStatus.valueOf(ToolRequestStatus.Other.toString())
                                                      .getHelpText();
        } else {
            helpText = null;
        }
        appearance.render(context, helpText, value, sb);
    }

}
