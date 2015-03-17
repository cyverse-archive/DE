package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.HasPath;

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

    public static final Type<DiskResourcePathSelectedEventHandler> TYPE = new Type<>();
    private final HasPath selectedDiskResource;

    public DiskResourcePathSelectedEvent(HasPath selectedDiskResource) {
        this.selectedDiskResource = selectedDiskResource;
    }

    public Type<DiskResourcePathSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public HasPath getSelectedDiskResource() {
        return selectedDiskResource;
    }

    protected void dispatch(DiskResourcePathSelectedEventHandler handler) {
        handler.onDiskResourcePathSelected(this);
    }
}
