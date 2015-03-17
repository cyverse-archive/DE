package org.iplantc.de.analysis.client.events.selection;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class AnalysisCommentSelectedEvent extends GwtEvent<AnalysisCommentSelectedEvent.AnalysisCommentSelectedEventHandler> {
    public interface AnalysisCommentSelectedEventHandler extends EventHandler {
        void onAnalysisCommentSelected(AnalysisCommentSelectedEvent event);
    }

    public static interface HasAnalysisCommentSelectedEventHandlers {
        HandlerRegistration addAnalysisCommentSelectedEventHandler(AnalysisCommentSelectedEventHandler handler);
    }

    public static final Type<AnalysisCommentSelectedEventHandler> TYPE = new Type<>();
    private final Analysis value;

    public AnalysisCommentSelectedEvent(Analysis value) {
        this.value = value;
    }

    @Override
    public Type<AnalysisCommentSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Analysis getValue() {
        return value;
    }

    @Override
    protected void dispatch(AnalysisCommentSelectedEventHandler handler) {
        handler.onAnalysisCommentSelected(this);
    }
}
