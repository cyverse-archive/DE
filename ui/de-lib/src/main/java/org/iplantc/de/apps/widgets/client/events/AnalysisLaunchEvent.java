package org.iplantc.de.apps.widgets.client.events;

import org.iplantc.de.apps.widgets.client.events.AnalysisLaunchEvent.AnalysisLaunchEventHandler;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class AnalysisLaunchEvent extends GwtEvent<AnalysisLaunchEventHandler> {

    public interface AnalysisLaunchEventHandler extends EventHandler {
        void onAnalysisLaunch(AnalysisLaunchEvent analysisLaunchEvent);
    }

    public static GwtEvent.Type<AnalysisLaunchEventHandler> TYPE = new GwtEvent.Type<AnalysisLaunchEvent.AnalysisLaunchEventHandler>();
    private final HasId at;

    public AnalysisLaunchEvent(HasId at) {
        this.at = at;
    }

    public HasId getAppTemplateId() {
        return at;
    }

    @Override
    public GwtEvent.Type<AnalysisLaunchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AnalysisLaunchEventHandler handler) {
        handler.onAnalysisLaunch(this);
    }
}
