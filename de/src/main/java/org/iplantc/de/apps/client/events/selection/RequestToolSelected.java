package org.iplantc.de.apps.client.events.selection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class RequestToolSelected extends GwtEvent<RequestToolSelected.RequestToolSelectedHandler> {
    public static interface HasRequestToolSelectedHandlers {
        HandlerRegistration addRequestToolSelectedHandler(RequestToolSelectedHandler handler);
    }

    public static interface RequestToolSelectedHandler extends EventHandler {
        void onRequestToolSelected(RequestToolSelected event);
    }
    public static final Type<RequestToolSelectedHandler> TYPE = new Type<>();

    public Type<RequestToolSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RequestToolSelectedHandler handler) {
        handler.onRequestToolSelected(this);
    }
}
