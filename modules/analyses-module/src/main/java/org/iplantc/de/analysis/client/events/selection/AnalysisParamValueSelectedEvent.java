package org.iplantc.de.analysis.client.events.selection;

import org.iplantc.de.client.models.analysis.AnalysisParameter;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class AnalysisParamValueSelectedEvent extends GwtEvent<AnalysisParamValueSelectedEvent.AnalysisParamValueSelectedEventHandler> {
    public interface AnalysisParamValueSelectedEventHandler extends EventHandler {
        void onAnalysisParamValueSelected(AnalysisParamValueSelectedEvent event);
    }

    public static interface HasAnalysisParamValueSelectedEventHandlers {
        HandlerRegistration addAnalysisParamValueSelectedEventHandler(AnalysisParamValueSelectedEventHandler handler);
    }

    public static final Type<AnalysisParamValueSelectedEventHandler> TYPE = new Type<>();
    private final AnalysisParameter value;

    public AnalysisParamValueSelectedEvent(AnalysisParameter value){
        this.value = value;
    }

    @Override
    public Type<AnalysisParamValueSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public AnalysisParameter getValue() {
        return value;
    }

    @Override
    protected void dispatch(AnalysisParamValueSelectedEventHandler handler) {
        handler.onAnalysisParamValueSelected(this);
    }
}
