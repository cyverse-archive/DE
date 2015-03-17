package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class RenameDiskResourceSelected extends GwtEvent<RenameDiskResourceSelected.RenameDiskResourceSelectedHandler> {
    public static interface RenameDiskResourceSelectedHandler extends EventHandler {
        void onRenameDiskResourceSelected(RenameDiskResourceSelected event);
    }

    public static interface HasRenameDiskResourceSelectedHandlers {
        HandlerRegistration addRenameDiskResourceSelectedHandler(RenameDiskResourceSelectedHandler handler);
    }

    public static final Type<RenameDiskResourceSelectedHandler> TYPE = new Type<>();
    private final DiskResource diskResource;

    public RenameDiskResourceSelected(final DiskResource diskResource) {
        this.diskResource = diskResource;
    }

    public Type<RenameDiskResourceSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    protected void dispatch(RenameDiskResourceSelectedHandler handler) {
        handler.onRenameDiskResourceSelected(this);
    }
}
