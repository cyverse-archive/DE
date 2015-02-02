package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class ManageCommentsSelected extends GwtEvent<ManageCommentsSelected.ManageCommentsSelectedEventHandler> {

    public static interface HasManageCommentsSelectedEventHandlers {
        HandlerRegistration addManageCommentsSelectedEventHandler(ManageCommentsSelectedEventHandler handler);
    }

    public interface ManageCommentsSelectedEventHandler extends EventHandler {
        void onManageCommentsSelected(ManageCommentsSelected event);

    }

    public static final Type<ManageCommentsSelectedEventHandler> TYPE = new Type<>();
    private final DiskResource dr;

    public ManageCommentsSelected(DiskResource dr) {
        this.dr = dr;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ManageCommentsSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResource getDiskResource() {
        return dr;
    }

    @Override
    protected void dispatch(ManageCommentsSelectedEventHandler handler) {
        handler.onManageCommentsSelected(this);

    }

}
