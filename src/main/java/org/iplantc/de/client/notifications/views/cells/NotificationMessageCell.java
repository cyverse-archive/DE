package org.iplantc.de.client.notifications.views.cells;

import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.notifications.util.NotificationHelper;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import com.sencha.gxt.core.client.util.Format;

/**
 * 
 * A cell to render notification messages in a Grid
 * 
 * @author sriram
 * 
 */
public class NotificationMessageCell extends AbstractCell<NotificationMessage> {

    public NotificationMessageCell() {
        super("click"); //$NON-NLS-1$
    }

    @Override
    public void render(Context context, NotificationMessage value, SafeHtmlBuilder sb) {
        String style = "white-space:pre-wrap;"; //$NON-NLS-1$

        if (value.getContext() != null) {
            style += "cursor:pointer; text-decoration:underline;"; //$NON-NLS-1$
        }

        sb.appendHtmlConstant(Format.substitute("<div style=\"{0}\">{1}</div>", style, //$NON-NLS-1$
                value.getMessage()));
    }

    @Override
    public void onBrowserEvent(Context context, Element parent, NotificationMessage value,
            NativeEvent event, ValueUpdater<NotificationMessage> valueUpdater) {
        if (value == null) {
            return;
        }

        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        if ("click".equals(event.getType())) { //$NON-NLS-1$
            if (value.getContext() != null) {
                NotificationHelper.getInstance().view(value);
            }
        }
    }

}
