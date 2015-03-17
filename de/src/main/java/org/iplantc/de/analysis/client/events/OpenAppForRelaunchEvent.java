package org.iplantc.de.analysis.client.events;

import org.iplantc.de.client.models.analysis.Analysis;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class OpenAppForRelaunchEvent extends GwtEvent<OpenAppForRelaunchEvent.OpenAppForRelaunchEventHandler> {
    public interface OpenAppForRelaunchEventHandler extends EventHandler {
        void onRequestOpenAppForRelaunch(OpenAppForRelaunchEvent event);
    }

    public static final Type<OpenAppForRelaunchEventHandler> TYPE = new Type<>();
    private final Analysis analysisForRelaunch;

    public OpenAppForRelaunchEvent(Analysis analysis) {
        this.analysisForRelaunch = analysis;
    }

    public Analysis getAnalysisForRelaunch() {
        return analysisForRelaunch;
    }

    @Override
    public Type<OpenAppForRelaunchEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(OpenAppForRelaunchEventHandler handler) {
        handler.onRequestOpenAppForRelaunch(this);
    }
}
