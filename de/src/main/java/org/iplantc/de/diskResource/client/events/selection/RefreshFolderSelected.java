package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/2/15.
 *
 * @author jstroot
 */
public class RefreshFolderSelected extends GwtEvent<RefreshFolderSelected.RefreshFolderSelectedHandler> {
    public static interface RefreshFolderSelectedHandler extends EventHandler {
        void onRefreshFolderSelected(RefreshFolderSelected event);
    }

    public static interface HasRefreshFolderSelectedHandlers {
        HandlerRegistration addRefreshFolderSelectedHandler(RefreshFolderSelectedHandler handler);
    }

    public static final Type<RefreshFolderSelectedHandler> TYPE = new Type<>();
    private final Folder selectedFolder;

    public RefreshFolderSelected(final Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Type<RefreshFolderSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    protected void dispatch(RefreshFolderSelectedHandler handler) {
        handler.onRefreshFolderSelected(this);
    }
}
