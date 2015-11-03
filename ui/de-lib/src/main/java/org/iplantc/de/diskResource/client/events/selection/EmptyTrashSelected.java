package org.iplantc.de.diskResource.client.events.selection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/2/15.
 * @author jstroot
 */
public class EmptyTrashSelected extends GwtEvent<EmptyTrashSelected.EmptyTrashSelectedHandler> {
    public static interface EmptyTrashSelectedHandler extends EventHandler {
        void onEmptyTrashSelected(EmptyTrashSelected event);
    }

    public static interface HasEmptyTrashSelectedHandlers {
        HandlerRegistration addEmptyTrashSelectedHandler(EmptyTrashSelectedHandler handler);
    }

    public static final Type<EmptyTrashSelectedHandler> TYPE = new Type<>();

    public Type<EmptyTrashSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EmptyTrashSelectedHandler handler) {
        handler.onEmptyTrashSelected(this);
    }
}
