package org.iplantc.de.apps.widgets.client.events;

import org.iplantc.de.apps.widgets.client.events.RequestAnalysisLaunchEvent.RequestAnalysisLaunchEventHandler;
import org.iplantc.de.client.models.apps.integration.AppTemplate;
import org.iplantc.de.client.models.apps.integration.JobExecution;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class RequestAnalysisLaunchEvent extends GwtEvent<RequestAnalysisLaunchEventHandler> {

    public interface HasRequestAnalysisLaunchHandlers {
        public HandlerRegistration addRequestAnalysisLaunchEventHandler(RequestAnalysisLaunchEventHandler handler);
    }

    public interface RequestAnalysisLaunchEventHandler extends EventHandler {
        void onAnalysisLaunchRequest(AppTemplate at, JobExecution je);
    }

    public static final GwtEvent.Type<RequestAnalysisLaunchEventHandler> TYPE = new GwtEvent.Type<RequestAnalysisLaunchEventHandler>();
    private final AppTemplate at;
    private final JobExecution je;

    public RequestAnalysisLaunchEvent(AppTemplate at, JobExecution je) {
        this.at = at;
        this.je = je;
    }

    @Override
    public GwtEvent.Type<RequestAnalysisLaunchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestAnalysisLaunchEventHandler handler) {
        handler.onAnalysisLaunchRequest(at, je);
    }

}
