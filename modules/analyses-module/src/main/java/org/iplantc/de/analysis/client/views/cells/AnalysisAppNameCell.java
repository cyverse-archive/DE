package org.iplantc.de.analysis.client.views.cells;

import org.iplantc.de.analysis.client.events.selection.AnalysisAppSelectedEvent;
import org.iplantc.de.client.models.analysis.Analysis;

import static com.google.gwt.dom.client.BrowserEvents.*;
import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Event;

/**
 * @author sriram, jstroot
 * 
 */
public class AnalysisAppNameCell extends AbstractCell<Analysis> {

    public interface AnalysisAppNameCellAppearance {
        String ELEMENT_NAME = "analysisAppName";

        void doOnMouseOut(Element eventTarget, Analysis value);

        void doOnMouseOver(Element eventTarget, Analysis value);

        void render(Cell.Context context, Analysis model, SafeHtmlBuilder sb);
    }

    private final AnalysisAppNameCellAppearance appearance;
    private HandlerManager hasHandlers;

    public AnalysisAppNameCell() {
        this(GWT.<AnalysisAppNameCellAppearance> create(AnalysisAppNameCellAppearance.class));
    }

    public AnalysisAppNameCell(AnalysisAppNameCellAppearance appearance){
        super(CLICK, MOUSEOVER, MOUSEOUT);
        this.appearance = appearance;
    }

    @Override
    public void render(Cell.Context context, Analysis model, SafeHtmlBuilder sb) {
        appearance.render(context, model, sb);
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, Analysis value, NativeEvent event,
            ValueUpdater<Analysis> valueUpdater) {
        if (value == null) {
            return;
        }
        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value);
                    break;
                case Event.ONMOUSEOVER:
                    appearance.doOnMouseOver(eventTarget, value);
                    break;
                case Event.ONMOUSEOUT:
                    appearance.doOnMouseOut(eventTarget, value);
                    break;
                default:
                    break;
            }
        }

    }

    public void setHasHandlers(HandlerManager hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    private void doOnClick(Element eventTarget, Analysis value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(AnalysisAppNameCellAppearance.ELEMENT_NAME)
                && !Strings.isNullOrEmpty(value.getResultFolderId()) && !value.isAppDisabled()) {
            if(hasHandlers != null){
                hasHandlers.fireEvent(new AnalysisAppSelectedEvent(value));
            }
        }
    }

}
