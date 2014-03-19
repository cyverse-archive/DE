package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.FolderSelectedEvent.FolderSelectedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class FolderSelectedEvent extends GwtEvent<FolderSelectedEventHandler> {

    public interface FolderSelectedEventHandler extends EventHandler {
        void onFolderSelected(FolderSelectedEvent event);
    }

    public static interface HasFolderSelectedEventHandlers {
        HandlerRegistration addFolderSelectedEventHandler(FolderSelectedEventHandler handler);
    }

    private final Folder selectedFolder;

    public FolderSelectedEvent(Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public static final GwtEvent.Type<FolderSelectedEventHandler> TYPE = new GwtEvent.Type<FolderSelectedEventHandler>();

    @Override
    public GwtEvent.Type<FolderSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FolderSelectedEventHandler handler) {
        handler.onFolderSelected(this);
    }
}
