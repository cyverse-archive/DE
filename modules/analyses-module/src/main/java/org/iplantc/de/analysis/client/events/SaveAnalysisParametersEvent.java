package org.iplantc.de.analysis.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SaveAnalysisParametersEvent extends GwtEvent<SaveAnalysisParametersEvent.SaveAnalysisParametersEventHandler> {

    public interface SaveAnalysisParametersEventHandler extends EventHandler {
        void onRequestSaveAnalysisParameters(SaveAnalysisParametersEvent event);
    }

    public static interface HasSaveAnalysisParametersEventHandlers {
        HandlerRegistration addSaveAnalysisParametersEventHandler(SaveAnalysisParametersEventHandler handler);
    }

    public static final Type<SaveAnalysisParametersEventHandler> TYPE = new Type<SaveAnalysisParametersEventHandler>();
    private final String path;
    private final String fileContents;

    public SaveAnalysisParametersEvent(String path, String fileContents) {
        this.path = path;
        this.fileContents = fileContents;
    }

    @Override
    public Type<SaveAnalysisParametersEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getFileContents() {
        return fileContents;
    }

    public String getPath() {
        return path;
    }

    @Override
    protected void dispatch(SaveAnalysisParametersEventHandler handler) {
        handler.onRequestSaveAnalysisParameters(this);
    }
}
