package org.iplantc.de.diskResource.client.views.cells.events;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class ManageCommentsEvent extends GwtEvent<ManageCommentsEvent.ManageCommentsEventHandler> {

    public static final Type<ManageCommentsEventHandler> TYPE = new Type<ManageCommentsEventHandler>();

    private final DiskResource dr;

    public DiskResource getDiskResource() {
        return dr;
    }

    public ManageCommentsEvent(DiskResource dr) {
        this.dr = dr;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ManageCommentsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ManageCommentsEventHandler handler) {
        handler.onManageComments(this);

    }

    public interface ManageCommentsEventHandler extends EventHandler {
        void onManageComments(ManageCommentsEvent event);

    }
    
    public static interface HasManageCommentsEventHandlers {
        HandlerRegistration addManageCommentsEventHandler(ManageCommentsEventHandler handler);
    }

}
