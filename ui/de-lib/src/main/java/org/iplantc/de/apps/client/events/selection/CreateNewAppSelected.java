package org.iplantc.de.apps.client.events.selection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class CreateNewAppSelected extends GwtEvent<CreateNewAppSelected.CreateNewAppSelectedHandler> {
    public static interface CreateNewAppSelectedHandler extends EventHandler {
        void onCreateNewAppSelected(CreateNewAppSelected event);
    }

    public static interface HasCreateNewAppSelectedHandlers {
        HandlerRegistration addCreateNewAppSelectedHandler(CreateNewAppSelectedHandler handler);
    }
    public static final Type<CreateNewAppSelectedHandler> TYPE = new Type<>();

    public Type<CreateNewAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CreateNewAppSelectedHandler handler) {
        handler.onCreateNewAppSelected(this);
    }
}
