package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.HasPath;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * This event is fired when the user enters a new folder path in the grid path field.
 * <p/>
 * Created by jstroot on 1/27/15.
 *
 * @author jstroot
 */
public class FolderPathSelectedEvent extends GwtEvent<FolderPathSelectedEvent.FolderPathSelectedEventHandler> {
    public static interface FolderPathSelectedEventHandler extends EventHandler {
        void onFolderPathSelected(FolderPathSelectedEvent event);
    }

    public static interface HasFolderPathSelectedEventHandlers {
        HandlerRegistration addFolderPathSelectedEventHandler(FolderPathSelectedEventHandler handler);
    }

    public static Type<FolderPathSelectedEventHandler> TYPE = new Type<>();
    private final HasPath selectedFolderPath;

    public FolderPathSelectedEvent(HasPath selectedFolderPath) {
        Preconditions.checkNotNull(selectedFolderPath);
        this.selectedFolderPath = selectedFolderPath;
    }

    public HasPath getSelectedFolderPath() {
        return selectedFolderPath;
    }

    public Type<FolderPathSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(FolderPathSelectedEventHandler handler) {
        handler.onFolderPathSelected(this);
    }
}
