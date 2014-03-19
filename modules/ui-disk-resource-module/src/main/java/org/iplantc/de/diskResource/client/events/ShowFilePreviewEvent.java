package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ShowFilePreviewEvent extends GwtEvent<ShowFilePreviewEventHandler> {

    public interface ShowFilePreviewEventHandler extends EventHandler {

        void showFilePreview(ShowFilePreviewEvent event);

    }

    public static final GwtEvent.Type<ShowFilePreviewEventHandler> TYPE = new GwtEvent.Type<ShowFilePreviewEventHandler>();
    private final File file;

    public ShowFilePreviewEvent(final File file, final Object source) {
        setSource(source);
        this.file = file;

    }

    public File getFile() {
        return file;
    }

    @Override
    public GwtEvent.Type<ShowFilePreviewEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowFilePreviewEventHandler handler) {
        handler.showFilePreview(this);
    }
}
