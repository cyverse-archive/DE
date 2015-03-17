package org.iplantc.de.diskResource.client.views.dataLink.cells;

import org.iplantc.de.client.models.dataLink.DataLink;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.diskResource.client.DataLinkView;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
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
public final class DataLinkPanelCell extends AbstractCell<DiskResource> {

    public interface Appearance {
        void render(SafeHtmlBuilder sb, DiskResource value);
    }

    private final DataLinkView.Presenter presenter;
    private final Appearance appearance;

    public DataLinkPanelCell(final DataLinkView.Presenter presenter) {
        this(presenter,
             GWT.<Appearance> create(Appearance.class));
    }
    public DataLinkPanelCell(final DataLinkView.Presenter presenter,
                             final Appearance appearance) {
        super(CLICK);
        this.presenter = presenter;
        this.appearance = appearance;
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, DiskResource value,
                               NativeEvent event,
                               ValueUpdater<DiskResource> valueUpdater) {

        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void render(Cell.Context context, DiskResource value, SafeHtmlBuilder sb) {
        appearance.render(sb, value);
    }

    private void doOnClick(Element eventTarget, DiskResource value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase("del")) {
            presenter.deleteDataLink((DataLink) value);
        }
    }

}