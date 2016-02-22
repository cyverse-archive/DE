package org.iplantc.de.theme.base.client.notifications.cells;

import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.notifications.client.views.cells.NotificationMessageCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.util.Format;

/**
 * @author aramsey
 */
public class NotificationMessageCellDefaultAppearance implements NotificationMessageCell.NotificationMessageCellAppearance {

    @Override
    public void render(Cell.Context context, NotificationMessage value, SafeHtmlBuilder sb) {
        String style = "white-space:pre-wrap;text-overflow:ellipsis;overflow:hidden;"; //$NON-NLS-1$

        if (value.getContext() != null) {
            style += "cursor:pointer; text-decoration:underline;"; //$NON-NLS-1$
        }

        sb.appendHtmlConstant(Format.substitute("<div style=\"{0}\">{1}</div>", style, //$NON-NLS-1$
                                                value.getMessage()));
    }
}
