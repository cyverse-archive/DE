package org.iplantc.de.analysis.client.views.cells;

import org.iplantc.de.analysis.client.events.selection.AnalysisNameSelectedEvent;
import org.iplantc.de.analysis.client.events.HTAnalysisExpandEvent;
import org.iplantc.de.client.models.analysis.Analysis;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOUT;
import static com.google.gwt.dom.client.BrowserEvents.MOUSEOVER;

import com.google.common.base.Strings;
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
 * @author sriram, jstroot
 * 
 */
public class AnalysisNameCell extends AbstractCell<Analysis> {

    public interface AnalysisNameCellAppearance {
        String ELEMENT_NAME = "analysisName";
        String HT_ELEMENT_NAME = "htanalysis";

        void doOnMouseOut(Element eventTarget, Analysis value);

        void doOnMouseOver(Element eventTarget, Analysis value);

        void render(Cell.Context context, Analysis model, SafeHtmlBuilder sb);
    }

    private final AnalysisNameCellAppearance appearance;
    private HasHandlers hasHandlers = null;

    public AnalysisNameCell() {
        this(GWT.<AnalysisNameCellAppearance> create(AnalysisNameCellAppearance.class));
    }

    public AnalysisNameCell(AnalysisNameCellAppearance appearance){
        super(CLICK, MOUSEOVER, MOUSEOUT);
        this.appearance = appearance;
    }

    public void setHasHandlers(HasHandlers hasHandlers){
        this.hasHandlers = hasHandlers;
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

    private void doOnClick(Element eventTarget, Analysis value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(AnalysisNameCellAppearance.ELEMENT_NAME)
                && !Strings.isNullOrEmpty(value.getResultFolderId())) {
            if(hasHandlers != null){
                hasHandlers.fireEvent(new AnalysisNameSelectedEvent(value));
            }

        } else if (eventTarget.getAttribute("name")
                              .equalsIgnoreCase(AnalysisNameCellAppearance.HT_ELEMENT_NAME)) {
            if (hasHandlers != null) {
                hasHandlers.fireEvent(new HTAnalysisExpandEvent(value));
            }
        }
    }

}
