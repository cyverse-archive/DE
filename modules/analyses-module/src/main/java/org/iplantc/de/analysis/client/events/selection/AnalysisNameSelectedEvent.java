package org.iplantc.de.analysis.client.events.selection;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Event fired when the Analysis Name hyperlink is selected.
 *
 * @author jstroot
 */
public class AnalysisNameSelectedEvent extends GwtEvent<AnalysisNameSelectedEvent.AnalysisNameSelectedEventHandler> {

    public interface AnalysisNameSelectedEventHandler extends EventHandler {
        void onAnalysisNameSelected(AnalysisNameSelectedEvent event);
    }

    public static interface HasAnalysisNameSelectedEventHandlers {
        HandlerRegistration addAnalysisNameSelectedEventHandler(AnalysisNameSelectedEventHandler handler);
    }

    private final Analysis value;

    public AnalysisNameSelectedEvent(final Analysis value) {

        this.value = value;
    }

    public static final GwtEvent.Type<AnalysisNameSelectedEventHandler> TYPE = new GwtEvent.Type<>();

    @Override
    public Type<AnalysisNameSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Analysis getValue() {
        return value;
    }

    @Override
    protected void dispatch(AnalysisNameSelectedEventHandler handler) {
        handler.onAnalysisNameSelected(this);
    }
}
