package org.iplantc.de.analysis.client.views.cells;

import org.iplantc.de.analysis.client.events.AnalysisAppSelectedEvent;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.views.windows.configs.AppWizardConfig;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;

import static com.google.gwt.dom.client.BrowserEvents.*;
import com.google.common.base.Strings;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.TextDecoration;
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
                    doOnClick(eventTarget, value, valueUpdater);
                    break;
                case Event.ONMOUSEOVER:
                    doOnMouseOver(eventTarget, value);
                    break;
                case Event.ONMOUSEOUT:
                    doOnMouseOut(eventTarget, value);
                    break;
                default:
                    break;
            }
        }

    }

    public void setHasHandlers(HandlerManager hasHandlers) {
        this.hasHandlers = hasHandlers;
    }

    private void doOnMouseOut(Element eventTarget, Analysis value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(appearance.ELEMENT_NAME)
                && !Strings.isNullOrEmpty(value.getResultFolderId())) {
            eventTarget.getStyle().setTextDecoration(TextDecoration.NONE);
        }
    }

    private void doOnMouseOver(Element eventTarget, Analysis value) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(appearance.ELEMENT_NAME)
                && !Strings.isNullOrEmpty(value.getResultFolderId())) {
            eventTarget.getStyle().setTextDecoration(TextDecoration.UNDERLINE);
        }
    }

    private void doOnClick(Element eventTarget, Analysis value, ValueUpdater<Analysis> valueUpdater) {
        if (eventTarget.getAttribute("name").equalsIgnoreCase(appearance.ELEMENT_NAME)
                && !Strings.isNullOrEmpty(value.getResultFolderId()) && !value.isAppDisabled()) {
            AppWizardConfig config = ConfigFactory.appWizardConfig(value.getAppId());
            config.setAnalysisId(value);
            config.setRelaunchAnalysis(true);
            //eventBus.fireEvent(new WindowShowRequestEvent(config));
            if(hasHandlers != null){
                hasHandlers.fireEvent(new AnalysisAppSelectedEvent(value.getAppId()));
            }
        }
    }

}
