package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 2/2/15.
 *
 * @author jstroot
 */
public class DeleteDiskResourcesSelected extends GwtEvent<DeleteDiskResourcesSelected.DeleteDiskResourcesSelectedEventHandler> {
    public static interface DeleteDiskResourcesSelectedEventHandler extends EventHandler {
        void onDeleteSelectedDiskResourcesSelected(DeleteDiskResourcesSelected event);
    }

    public static interface HasDeleteDiskResourcesSelectedEventHandlers {
        HandlerRegistration addDeleteSelectedDiskResourcesSelectedEventHandler(DeleteDiskResourcesSelectedEventHandler handler);
    }

    public static final Type<DeleteDiskResourcesSelectedEventHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;
    private final boolean confirmDelete;

    public DeleteDiskResourcesSelected(final List<DiskResource> selectedDiskResources) {
        this(selectedDiskResources, true);
    }

    public DeleteDiskResourcesSelected(final List<DiskResource> selectedDiskResources,
                                       boolean confirmDelete) {
        this.selectedDiskResources = selectedDiskResources;
        this.confirmDelete = confirmDelete;
    }

    public Type<DeleteDiskResourcesSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelectedDiskResources() {
        return selectedDiskResources;
    }

    public boolean isConfirmDelete() {
        return confirmDelete;
    }

    protected void dispatch(DeleteDiskResourcesSelectedEventHandler handler) {
        handler.onDeleteSelectedDiskResourcesSelected(this);
    }
}
