package org.iplantc.de.admin.desktop.client.toolAdmin.view.cells;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.Tool;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * @author aramsey
 */
public class ToolAdminNameCell extends AbstractCell<Tool> {

    public interface ToolAdminNameCellAppearance {
        String CLICKABLE_ELEMENT_NAME = "toolName";

        void render(SafeHtmlBuilder safeHtmlBuilder, Tool tool, String debugID);
    }

    private final ToolAdminView view;
    private final ToolAdminNameCellAppearance appearance = GWT.create(ToolAdminNameCellAppearance.class);
    private String baseDebugId;

    public ToolAdminNameCell(ToolAdminView view) {
        super(CLICK);
        this.view = view;
    }

    @Override
    public void render(Context context, Tool value, SafeHtmlBuilder sb) {
        String debugID = baseDebugId + "." + value.getId() + Belphegor.ToolAdminIds.NAME_CELL;
        appearance.render(sb, value, debugID);
    }

    @Override
    public void onBrowserEvent(Context context,
                               Element parent,
                               Tool value,
                               NativeEvent event,
                               ValueUpdater<Tool> valueUpdater) {

        if (value == null) {
            return;
        }

        Element eventTargetElement = Element.as(event.getEventTarget());
        if ((Event.as(event).getTypeInt() == Event.ONCLICK)
            && eventTargetElement.getAttribute("name")
                                 .equalsIgnoreCase(ToolAdminNameCellAppearance.CLICKABLE_ELEMENT_NAME)) {
            view.toolSelected(value);
        }
    }

    public void setBaseDebugId(String baseDebugId) {
        this.baseDebugId = baseDebugId;
    }
}
