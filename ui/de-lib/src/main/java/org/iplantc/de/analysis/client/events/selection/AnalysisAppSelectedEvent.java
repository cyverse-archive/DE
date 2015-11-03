package org.iplantc.de.analysis.client.events.selection;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Event fired when the app associated with a given analysis is selected.
 *
 * @author jstroot
 */
public class AnalysisAppSelectedEvent extends GwtEvent<AnalysisAppSelectedEvent.AnalysisAppSelectedEventHandler> {
    public interface AnalysisAppSelectedEventHandler extends EventHandler {
        void onAnalysisAppSelected(AnalysisAppSelectedEvent event);
    }

    public static interface HasAnalysisAppSelectedEventHandlers {
        HandlerRegistration addAnalysisAppSelectedEventHandler(AnalysisAppSelectedEventHandler handler);
    }

    private final Analysis analysis;
    public static final GwtEvent.Type<AnalysisAppSelectedEventHandler> TYPE = new GwtEvent.Type<>();
    public AnalysisAppSelectedEvent(final Analysis analysis){
        this.analysis = analysis;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    @Override
    public Type<AnalysisAppSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisAppSelectedEventHandler handler) {
        handler.onAnalysisAppSelected(this);
    }
}
