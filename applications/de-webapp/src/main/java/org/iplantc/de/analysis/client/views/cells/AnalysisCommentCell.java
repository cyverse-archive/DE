package org.iplantc.de.analysis.client.views.cells;

import org.iplantc.de.analysis.client.events.AnalysisCommentSelectedEvent;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IPlantDialog;
import org.iplantc.de.resources.client.messages.I18N;

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

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.form.TextArea;

public class AnalysisCommentCell extends AbstractCell<Analysis> {

    public interface AnalysisCommentCellAppearance {
        void render(Context context, Analysis value, SafeHtmlBuilder sb);
    }

    private final AnalysisCommentCellAppearance appearance;
    private HasHandlers hasHandlers;

    public AnalysisCommentCell() {
        this(GWT.<AnalysisCommentCellAppearance>create(AnalysisCommentCellAppearance.class));
    }

    public AnalysisCommentCell(AnalysisCommentCellAppearance appearance){
        super(CLICK, MOUSEOVER, MOUSEOUT);
        this.appearance = appearance;
    }

    @Override
    public void render(com.google.gwt.cell.client.Cell.Context context, Analysis value, SafeHtmlBuilder sb) {
        appearance.render(context, value, sb);
    }

    @Override
    public void onBrowserEvent(Cell.Context context, Element parent, Analysis value, NativeEvent event, ValueUpdater<Analysis> valueUpdater) {
        if (value == null) {
            return;
        }

        Element eventTarget = Element.as(event.getEventTarget());
        if (parent.isOrHasChild(eventTarget)) {

            switch (Event.as(event).getTypeInt()) {
                case Event.ONCLICK:
                    doOnClick(eventTarget, value, valueUpdater);
                    break;
                default:
                    break;
            }
        }
    }

    public void setHasHandlers(HasHandlers handlerManager) {
        hasHandlers = handlerManager;
    }

    private void doOnClick(Element eventTarget, Analysis value, ValueUpdater<Analysis> valueUpdater) {
        Dialog d = new IPlantDialog();
        d.setHeadingText(I18N.DISPLAY.comments());
        d.setSize("350px", "300px");
        TextArea ta = new TextArea();
        ta.setSize("300px", "200px");
        ta.setValue(value.getDescription());
        d.add(ta);
        d.show();
        if(hasHandlers != null){
            hasHandlers.fireEvent(new AnalysisCommentSelectedEvent(value));
        }
    }


}
