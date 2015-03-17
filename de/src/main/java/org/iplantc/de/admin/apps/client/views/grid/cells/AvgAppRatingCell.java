package org.iplantc.de.admin.apps.client.views.grid.cells;

import org.iplantc.de.client.models.apps.AppFeedback;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;

public class AvgAppRatingCell extends AbstractCell<AppFeedback> {

    @Override
    public void render(Cell.Context context, AppFeedback value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        sb.append(SafeHtmlUtils.fromString(NumberFormat.getFormat("0.00").format(
                value.getAverageRating())));
    }
}
