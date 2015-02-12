package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Event fired from the {@link org.iplantc.de.diskResource.client.views.grid.cells.DiskResourceNameCell} to indicate that
 * the corresponding DiskResource's name has been selected.
 *
 * @author jstroot
 */
public class DiskResourceNameSelectedEvent extends GwtEvent<DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler> {

    public interface DiskResourceNameSelectedEventHandler extends EventHandler {
        void onDiskResourceNameSelected(DiskResourceNameSelectedEvent event);
    }

    public static interface HasDiskResourceNameSelectedEventHandlers {
        HandlerRegistration addDiskResourceNameSelectedEventHandler(DiskResourceNameSelectedEventHandler handler);
    }

    public static final Type<DiskResourceNameSelectedEventHandler> TYPE = new Type<>();
    private final DiskResource selectedItem;

    public DiskResourceNameSelectedEvent(final DiskResource selectedItem) {
        this.selectedItem = selectedItem;
    }

    public DiskResource getSelectedItem() {
        return selectedItem;
    }

    @Override
    public Type<DiskResourceNameSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiskResourceNameSelectedEventHandler handler) {
        handler.onDiskResourceNameSelected(this);
    }
}
