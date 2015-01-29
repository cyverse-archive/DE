package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 1/28/15.
 *
 * @author jstroot
 */
public class DiskResourcePathSelectedEvent extends GwtEvent<DiskResourcePathSelectedEvent.DiskResourcePathSelectedEventHandler> {
    public static interface DiskResourcePathSelectedEventHandler extends EventHandler {
        void onDiskResourcePathSelected(DiskResourcePathSelectedEvent event);
    }

    public static interface HasDiskResourcePathSelectedEventHandlers {
        HandlerRegistration addDiskResourcePathSelectedEventHandler(DiskResourcePathSelectedEventHandler handler);
    }

    public static Type<DiskResourcePathSelectedEventHandler> TYPE = new Type<>();
    private final DiskResource selectedDiskResource;

    public DiskResourcePathSelectedEvent(DiskResource selectedDiskResource) {
        this.selectedDiskResource = selectedDiskResource;
    }

    public Type<DiskResourcePathSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getSelectedDiskResource() {
        return selectedDiskResource;
    }

    protected void dispatch(DiskResourcePathSelectedEventHandler handler) {
        handler.onDiskResourcePathSelected(this);
    }
}
