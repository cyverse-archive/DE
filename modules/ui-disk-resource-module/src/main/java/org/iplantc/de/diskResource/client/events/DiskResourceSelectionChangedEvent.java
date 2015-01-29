package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author jstroot
 */
public class DiskResourceSelectionChangedEvent extends GwtEvent<DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler> {

    public interface DiskResourceSelectionChangedEventHandler extends EventHandler {
        void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event);
    }

    public static interface HasDiskResourceSelectionChangedEventHandlers {
        HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEventHandler handler);
    }

    public static final Type<DiskResourceSelectionChangedEventHandler> TYPE = new Type<>();

    private final List<DiskResource> selection;

    public DiskResourceSelectionChangedEvent(List<DiskResource> selection){
        this.selection = selection;
    }

    @Override
    public Type<DiskResourceSelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelection() {
        return selection;
    }

    @Override
    protected void dispatch(DiskResourceSelectionChangedEventHandler handler) {
        handler.onDiskResourceSelectionChanged(this);
    }
}
