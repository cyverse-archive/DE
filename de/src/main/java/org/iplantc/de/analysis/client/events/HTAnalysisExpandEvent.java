package org.iplantc.de.analysis.client.events;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class HTAnalysisExpandEvent extends GwtEvent<HTAnalysisExpandEvent.HTAnalysisExpandEventHandler> {

    public interface HTAnalysisExpandEventHandler extends EventHandler {
        void onHTAnalysisExpanded(HTAnalysisExpandEvent event);
    }

    public static interface HasHTAnalysisExpandEventHandlers {
        HandlerRegistration addHTAnalysisExpandEventHandler(HTAnalysisExpandEventHandler handler);
    }

    private final Analysis value;

    public HTAnalysisExpandEvent(final Analysis value) {
        this.value = value;
    }

    public static final GwtEvent.Type<HTAnalysisExpandEventHandler> TYPE = new GwtEvent.Type<>();

    @Override
    public Type<HTAnalysisExpandEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Analysis getValue() {
        return value;
    }

    @Override
    protected void dispatch(HTAnalysisExpandEventHandler handler) {
        handler.onHTAnalysisExpanded(this);
    }

}
