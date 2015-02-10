package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent.FolderSelectionEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class FolderSelectionEvent extends GwtEvent<FolderSelectionEventHandler> {

    public interface FolderSelectionEventHandler extends EventHandler {
        void onFolderSelected(FolderSelectionEvent event);
    }

    public static interface HasFolderSelectionEventHandlers {
        HandlerRegistration addFolderSelectedEventHandler(FolderSelectionEventHandler handler);
    }

    private final Folder selectedFolder;

    public FolderSelectionEvent(Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    public static final GwtEvent.Type<FolderSelectionEventHandler> TYPE = new GwtEvent.Type<>();

    @Override
    public GwtEvent.Type<FolderSelectionEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FolderSelectionEventHandler handler) {
        handler.onFolderSelected(this);
    }
}
