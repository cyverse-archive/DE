package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ManageCommentsEvent extends GwtEvent<ManageCommentsEvent.ManageCommentsEventHandler> {

    public static interface HasManageCommentsEventHandlers {
        HandlerRegistration addManageCommentsEventHandler(ManageCommentsEventHandler handler);
    }

    public interface ManageCommentsEventHandler extends EventHandler {
        void onManageComments(ManageCommentsEvent event);

    }

    public static final Type<ManageCommentsEventHandler> TYPE = new Type<>();
    private final DiskResource dr;

    public ManageCommentsEvent(DiskResource dr) {
        this.dr = dr;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ManageCommentsEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return dr;
    }

    @Override
    protected void dispatch(ManageCommentsEventHandler handler) {
        handler.onManageComments(this);

    }

}
