package org.iplantc.de.analysis.client.events;

import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.IsMaskable;

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
    private final IsHideable hideable;
    private final IsMaskable maskable;

    public SaveAnalysisParametersEvent(String path, String fileContents, IsHideable hideable, IsMaskable maskable) {
        this.path = path;
        this.fileContents = fileContents;
        this.hideable = hideable;
        this.maskable = maskable;
    }

    @Override
    public Type<SaveAnalysisParametersEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getFileContents() {
        return fileContents;
    }

    public IsMaskable getMaskable() {
        return maskable;
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
