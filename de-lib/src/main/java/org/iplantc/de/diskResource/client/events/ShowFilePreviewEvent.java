package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent.ShowFilePreviewEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class ShowFilePreviewEvent extends GwtEvent<ShowFilePreviewEventHandler> {

    public interface ShowFilePreviewEventHandler extends EventHandler {
        void showFilePreview(ShowFilePreviewEvent event);
    }

    public static final GwtEvent.Type<ShowFilePreviewEventHandler> TYPE = new GwtEvent.Type<>();
    private final File file;
    private final FileViewerWindowConfig config;

    public ShowFilePreviewEvent(final File file,
                                final FileViewerWindowConfig config) {
        this.file = file;
        this.config = config;
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

    public FileViewerWindowConfig getConfig() {
        return config;
    }
}
