package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class BulkUploadSelected extends GwtEvent<BulkUploadSelected.BulkUploadSelectedEventHandler> {
    public static interface BulkUploadSelectedEventHandler extends EventHandler {
        void onBulkUploadSelected(BulkUploadSelected event);
    }

    public static interface HasBulkUploadSelectedEventHandlers {
        HandlerRegistration addBulkUploadSelectedEventHandler(BulkUploadSelectedEventHandler handler);
    }

    public static Type<BulkUploadSelectedEventHandler> TYPE = new Type<>();
    private final Folder selectedFolder;

    public BulkUploadSelected(final Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Type<BulkUploadSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    protected void dispatch(BulkUploadSelectedEventHandler handler) {
        handler.onBulkUploadSelected(this);
    }
}
