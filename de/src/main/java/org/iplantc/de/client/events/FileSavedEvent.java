package org.iplantc.de.client.events;

import org.iplantc.de.client.events.FileSavedEvent.FileSavedEventHandler;
import org.iplantc.de.client.models.diskResources.File;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class FileSavedEvent extends GwtEvent<FileSavedEventHandler> {
    public interface FileSavedEventHandler extends EventHandler {
        void onFileSaved(FileSavedEvent event);
    }

    public static interface HasFileSavedEventHandlers {
        HandlerRegistration addFileSavedEventHandler(FileSavedEventHandler handler);
    }

    public static final GwtEvent.Type<FileSavedEventHandler> TYPE = new Type<>();
    private File file;

    public FileSavedEvent(File file) {
        this.file = file;
    }

    @Override
    public GwtEvent.Type<FileSavedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FileSavedEventHandler handler) {
        handler.onFileSaved(this);
    }

    public File getFile() {
        return file;
    }

}
