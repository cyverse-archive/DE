package org.iplantc.de.analysis.client.events;

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
        HandlerRegistration addAnalysisAppSelectedEventHandlers(AnalysisAppSelectedEventHandler handler);
    }

    private final String appId;
    public static final GwtEvent.Type<AnalysisAppSelectedEventHandler> TYPE = new GwtEvent.Type<AnalysisAppSelectedEventHandler>();
    public AnalysisAppSelectedEvent(final String appId){
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
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
