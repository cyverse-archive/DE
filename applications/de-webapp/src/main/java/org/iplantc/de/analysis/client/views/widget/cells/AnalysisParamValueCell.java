/**
 *
 */
package org.iplantc.de.analysis.client.views.widget.cells;

import org.iplantc.de.analysis.client.events.AnalysisParamValueSelectedEvent;
import org.iplantc.de.client.models.analysis.AnalysisParameter;

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
 * @author sriram
 * 
 */
public class AnalysisParamValueCell extends AbstractCell<AnalysisParameter> {

    public interface AnalysisParamValueCellAppearance {

        void render(Context context, AnalysisParameter value, SafeHtmlBuilder sb);
    }

    private final AnalysisParamValueCellAppearance appearance;
    private HasHandlers hasHandlers;

    public AnalysisParamValueCell() {
        this(GWT.<AnalysisParamValueCellAppearance> create(AnalysisParamValueCellAppearance.class));
    }

    protected AnalysisParamValueCell(AnalysisParamValueCellAppearance appearance){
        super(CLICK);
        this.appearance = appearance;
    }

    @Override
    public void render(Cell.Context context, AnalysisParameter value,
            SafeHtmlBuilder sb) {
        appearance.render(context, value, sb);
    }

    @Override
    public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent,
            AnalysisParameter value, NativeEvent event, ValueUpdater<AnalysisParameter> valueUpdater) {
        if (value == null) {
            return;
        }
        // Call the super handler, which handlers the enter key.
        super.onBrowserEvent(context, parent, value, event, valueUpdater);

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {
            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    if(hasHandlers != null){
                        hasHandlers.fireEvent(new AnalysisParamValueSelectedEvent(value));
                    }
                    break;
            }
        }
    }

    public void setHasHandlers(HasHandlers hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

}
