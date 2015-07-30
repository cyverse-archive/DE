package org.iplantc.de.client.events.diskResources;

import org.iplantc.de.client.events.diskResources.FolderRefreshEvent.FolderRefreshEventHandler;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FolderRefreshEvent extends GwtEvent<FolderRefreshEventHandler> {

    public interface FolderRefreshEventHandler extends EventHandler {
        void onFolderRefresh(FolderRefreshEvent event);
    }

    public static final GwtEvent.Type<FolderRefreshEventHandler> TYPE = new GwtEvent.Type<FolderRefreshEventHandler>();

    private final Folder folder;

    public FolderRefreshEvent(Folder folder) {
        this.folder = folder;
    }

    @Override
    protected void dispatch(FolderRefreshEventHandler handler) {
        handler.onFolderRefresh(this);
    }

    @Override
    public GwtEvent.Type<FolderRefreshEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Folder getFolder() {
        return folder;
    }
}
