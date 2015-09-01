package org.iplantc.de.apps.client.views.grid.cells;

import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.client.models.apps.App;

import static com.google.gwt.dom.client.BrowserEvents.*;
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
 * @author jstroot
 */
public class AppRatingCell extends AbstractCell<App> {

    public interface AppRatingCellAppearance {

        int getRatingScore(Element eventTarget);

        boolean isRatingCell(Element eventTarget);

        boolean isUnRateCell(Element eventTarget);

        void onMouseOut(Element parent, App value);

        void onMouseOver(Element parent, Element eventTarget, App app);

        void onUnRate(Element eventTarget);

        void render(SafeHtmlBuilder sb, App app);
    }

    private final AppRatingCellAppearance appearance;
    private HasHandlers hasHandlers;

    public AppRatingCell() {
        this(GWT.<AppRatingCellAppearance> create(AppRatingCellAppearance.class));
    }

    public AppRatingCell(final AppRatingCellAppearance appearance) {
        super(CLICK, MOUSEOVER, MOUSEOUT);
        this.appearance = appearance;
    }

    @Override
    public void render(Cell.Context context, App value, SafeHtmlBuilder sb) {
        appearance.render(sb, value);
    }

    @Override
    public boolean handlesSelection() {
        // FIXME JDS Is this necessary?
        return true;
    }

    @Override
    public void onBrowserEvent(Cell.Context context,
                               Element parent,
                               App value,
                               NativeEvent event,
                               ValueUpdater<App> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (eventTarget.getNodeName().equalsIgnoreCase("img") && parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    onRatingClicked(eventTarget, value);
                    break;
                case Event.ONMOUSEOVER:
                    appearance.onMouseOver(parent, eventTarget, value);
                    break;
                case Event.ONMOUSEOUT:
                    appearance.onMouseOut(parent, value);
                    break;
                default:
                    break;
            }
        }
    }

    public void setHasHandlers(final HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    private void onRatingClicked(final Element eventTarget, final App value) {
        if (!value.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
            if (appearance.isRatingCell(eventTarget)) {
                final int score = appearance.getRatingScore(eventTarget);
                if (hasHandlers != null) {
                    hasHandlers.fireEvent(new AppRatingSelected(value, score));
                }

            } else if (appearance.isUnRateCell(eventTarget)) {
                appearance.onUnRate(eventTarget);
                if (hasHandlers != null) {
                    hasHandlers.fireEvent(new AppRatingDeselected(value));
                }
            }
        }

    }
}
