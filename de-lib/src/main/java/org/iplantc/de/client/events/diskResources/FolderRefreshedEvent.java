package org.iplantc.de.client.events.diskResources;

import org.iplantc.de.client.events.diskResources.FolderRefreshedEvent.FolderRefreshedEventHandler;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FolderRefreshedEvent extends GwtEvent<FolderRefreshedEventHandler> {

    public interface FolderRefreshedEventHandler extends EventHandler {
        void onFolderRefreshed(FolderRefreshedEvent event);
    }

    public static final GwtEvent.Type<FolderRefreshedEventHandler> TYPE = new GwtEvent.Type<FolderRefreshedEventHandler>();

    private final Folder folder;

    public FolderRefreshedEvent(Folder folder) {
        this.folder = folder;
    }

    @Override
    protected void dispatch(FolderRefreshedEventHandler handler) {
        handler.onFolderRefreshed(this);
    }

    @Override
    public GwtEvent.Type<FolderRefreshedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Folder getFolder() {
        return folder;
    }
}
