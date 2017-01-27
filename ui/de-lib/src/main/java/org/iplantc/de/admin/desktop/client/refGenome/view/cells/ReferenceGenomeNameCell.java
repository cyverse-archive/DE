package org.iplantc.de.admin.desktop.client.refGenome.view.cells;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;

import org.iplantc.de.admin.desktop.client.refGenome.RefGenomeView;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.apps.refGenome.ReferenceGenome;

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
public class ReferenceGenomeNameCell extends AbstractCell<ReferenceGenome> {

    public interface ReferenceGenomeNameCellAppearance {

        void renderDeletedReferenceGenomeCell(SafeHtmlBuilder sb, ReferenceGenome value, String debugId);

        void renderReferenceGenomeCell(SafeHtmlBuilder sb, ReferenceGenome value, String debugId);
    }

    private final RefGenomeView view;
    private final ReferenceGenomeNameCellAppearance appearance = GWT.create(ReferenceGenomeNameCellAppearance.class);
    private String baseID;

    public ReferenceGenomeNameCell(RefGenomeView view) {
        super(CLICK);
        this.view = view;
    }

    @Override
    public void render(Cell.Context arg0, ReferenceGenome value, SafeHtmlBuilder sb) {
        if (value == null) {
            return;
        }

        String debugId = baseID + "." + value.getId() + Belphegor.RefGenomeIds.NAME_CELL;

        if (value.isDeleted()) {
            appearance.renderDeletedReferenceGenomeCell(sb, value, debugId);
        } else {
            appearance.renderReferenceGenomeCell(sb, value, debugId);
        }
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, ReferenceGenome value, NativeEvent event, ValueUpdater<ReferenceGenome> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget) 
                && (Event.as(event).getTypeInt() == Event.ONCLICK)
                && eventTarget.getAttribute("name").equalsIgnoreCase("rgName")) {
            view.editReferenceGenome(value);
        }
    }

    public void setBaseDebugId(String baseDebugId) {
        this.baseID = baseDebugId;
    }
}
