package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class MoveDiskResourcesSelected extends GwtEvent<MoveDiskResourcesSelected.MoveDiskResourcesSelectedHandler> {
    public static interface MoveDiskResourcesSelectedHandler extends EventHandler {
        void onMoveDiskResourcesSelected(MoveDiskResourcesSelected event);
    }

    public static interface HasMoveDiskResourcesSelectedHandlers {
        HandlerRegistration addMoveDiskResourcesSelectedHandler(MoveDiskResourcesSelectedHandler handler);
    }

    public static final Type<MoveDiskResourcesSelectedHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;

    public MoveDiskResourcesSelected(final List<DiskResource> selectedDiskResources) {
        this.selectedDiskResources = selectedDiskResources;
    }

    public Type<MoveDiskResourcesSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelectedDiskResources() {
        return selectedDiskResources;
    }

    protected void dispatch(MoveDiskResourcesSelectedHandler handler) {
        handler.onMoveDiskResourcesSelected(this);
    }
}
