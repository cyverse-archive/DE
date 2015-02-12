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
public class RestoreDiskResourcesSelected extends GwtEvent<RestoreDiskResourcesSelected.RestoreDiskResourcesSelectedHandler> {
    public static interface RestoreDiskResourcesSelectedHandler extends EventHandler {
        void onRestoreDiskResourcesSelected(RestoreDiskResourcesSelected event);
    }

    public static interface HasRestoreDiskResourceSelectedHandlers {
        HandlerRegistration addRestoreDiskResourcesSelectedHandler(RestoreDiskResourcesSelectedHandler handler);
    }

    public static final Type<RestoreDiskResourcesSelectedHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;

    public RestoreDiskResourcesSelected(final List<DiskResource> selectedDiskResources) {
        this.selectedDiskResources = selectedDiskResources;
    }

    public Type<RestoreDiskResourcesSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelectedDiskResources() {
        return selectedDiskResources;
    }

    protected void dispatch(RestoreDiskResourcesSelectedHandler handler) {
        handler.onRestoreDiskResourcesSelected(this);
    }
}
