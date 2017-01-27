package org.iplantc.de.admin.desktop.client.systemMessage.view.cells;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.de.admin.desktop.client.systemMessage.SystemMessageView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.systemMessages.SystemMessage;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * @author jstroot
 */
public class SystemMessageNameCell extends AbstractCell<SystemMessage> {

    public interface SystemMessageNameCellAppearance {
        String CLICKABLE_ELEMENT_NAME = "smName";

        void render(SafeHtmlBuilder sb, SystemMessage value, String debugID);
    }

    private final SystemMessageView view;
    private final SystemMessageNameCellAppearance appearance = GWT.create(SystemMessageNameCellAppearance.class);
    private String baseDebugId;

    public SystemMessageNameCell(final SystemMessageView view) {
        super(CLICK);
        this.view = view;
    }

    @Override
    public void render(Cell.Context arg0, SystemMessage value, SafeHtmlBuilder sb) {
        String debugID = baseDebugId + "." + value.getId() + Belphegor.SystemMessageIds.MESSAGE_CELL;
        appearance.render(sb, value, debugID);
    }

    @Override
    public void onBrowserEvent(Cell.Context context,
                               Element parent,
                               SystemMessage value,
                               NativeEvent event,
                               ValueUpdater<SystemMessage> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)
                && (Event.as(event).getTypeInt() == Event.ONCLICK)
                && eventTarget.getAttribute("name").equalsIgnoreCase(appearance.CLICKABLE_ELEMENT_NAME)) {
            view.editSystemMessage(value);
        }
    }

    public void setBaseDebugId(String baseDebugId) {
        this.baseDebugId = baseDebugId;
    }
}
