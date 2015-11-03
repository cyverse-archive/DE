package org.iplantc.de.diskResource.client.views.grid.cells;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * FIXME Consolidate with DiskResourceNameCell, CORE-5959
 * @author jstroot
 */
public class DiskResourcePathCell extends AbstractCell<DiskResource> {

    public interface Appearance {

        void render(SafeHtmlBuilder sb, String path, String baseID, String debugId);
    }

    private final Appearance appearance;

    private String baseID;
    private HasHandlers hasHandlers;

    public DiskResourcePathCell() {
        this(true);
    }

    public DiskResourcePathCell(final boolean previewEnabled) {
        this(previewEnabled,
             GWT.<Appearance> create(Appearance.class));
    }

    public DiskResourcePathCell(final boolean previewEnabled,
                                final Appearance appearance) {
        super(CLICK);
        this.appearance = appearance;
    }

    @Override
    public void onBrowserEvent(Cell.Context context,
                               Element parent,
                               DiskResource value,
                               NativeEvent event,
                               ValueUpdater<DiskResource> valueUpdater) {
        if (value == null) {
            return;
        }
        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    if(hasHandlers != null){
                        hasHandlers.fireEvent(new DiskResourcePathSelectedEvent(value));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void render(Cell.Context context, DiskResource value, SafeHtmlBuilder sb) {
        final String debugId = baseID + "." + value.getPath() + DiskResourceModule.Ids.PATH_CELL;
        appearance.render(sb, value.getPath(), baseID, debugId);
    }

    public void setBaseDebugId(String baseID) {
        this.baseID = baseID;
    }

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

}
