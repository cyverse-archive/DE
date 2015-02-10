package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class BulkDownloadSelected extends GwtEvent<BulkDownloadSelected.BulkDownloadSelectedEventHandler> {
    public static interface BulkDownloadSelectedEventHandler extends EventHandler {
        void onBulkDownloadSelected(BulkDownloadSelected event);
    }

    public static interface HasBulkDownloadSelectedEventHandlers {
        HandlerRegistration addBulkDownloadSelectedEventHandler(BulkDownloadSelectedEventHandler handler);
    }

    public static final Type<BulkDownloadSelectedEventHandler> TYPE = new Type<>();
    private final Folder selectedFolder;
    private final List<DiskResource> selectedDiskResources;

    public BulkDownloadSelected(final Folder selectedFolder,
                                final List<DiskResource> selectedDiskResources) {
        this.selectedFolder = selectedFolder;
        this.selectedDiskResources = selectedDiskResources;
    }

    public Type<BulkDownloadSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelectedDiskResources() {
        return selectedDiskResources;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    protected void dispatch(BulkDownloadSelectedEventHandler handler) {
        handler.onBulkDownloadSelected(this);
    }
}
