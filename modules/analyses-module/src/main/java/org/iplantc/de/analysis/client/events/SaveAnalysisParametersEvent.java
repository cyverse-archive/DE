package org.iplantc.de.analysis.client.events;

import org.iplantc.de.client.models.IsHideable;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class SaveAnalysisParametersEvent extends GwtEvent<SaveAnalysisParametersEvent.SaveAnalysisParametersEventHandler> {

    public interface SaveAnalysisParametersEventHandler extends EventHandler {
        void onRequestSaveAnalysisParameters(SaveAnalysisParametersEvent event);
    }

    public static interface HasSaveAnalysisParametersEventHandlers {
        HandlerRegistration addSaveAnalysisParametersEventHandler(SaveAnalysisParametersEventHandler handler);
    }

    public static final Type<SaveAnalysisParametersEventHandler> TYPE = new Type<>();
    private final String path;
    private final String fileContents;
    private final IsHideable hideable;

    public SaveAnalysisParametersEvent(final String path,
                                       final String fileContents,
                                       final IsHideable hideable) {
        this.path = path;
        this.fileContents = fileContents;
        this.hideable = hideable;
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

    public IsHideable getHideable() {
        return hideable;
    }

    @Override
    protected void dispatch(SaveAnalysisParametersEventHandler handler) {
        handler.onRequestSaveAnalysisParameters(this);
    }
}
