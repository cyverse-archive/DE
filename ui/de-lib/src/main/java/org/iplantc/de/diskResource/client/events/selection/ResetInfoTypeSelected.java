package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/4/15.
 *
 * @author jstroot
 */
public class ResetInfoTypeSelected extends GwtEvent<ResetInfoTypeSelected.ResetInfoTypeSelectedHandler> {
    public static interface ResetInfoTypeSelectedHandler extends EventHandler {
        void onResetInfoTypeSelected(ResetInfoTypeSelected event);
    }

    public static interface HasResetInfoTypeSelectedHandlers {
        HandlerRegistration addResetInfoTypeSelectedHandler(ResetInfoTypeSelectedHandler handler);
    }

    public static final Type<ResetInfoTypeSelectedHandler> TYPE = new Type<>();
    private final DiskResource diskResource;

    public ResetInfoTypeSelected(final DiskResource diskResource) {
        this.diskResource = diskResource;
    }

    public Type<ResetInfoTypeSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return diskResource;
    }

    protected void dispatch(ResetInfoTypeSelectedHandler handler) {
        handler.onResetInfoTypeSelected(this);
    }
}
