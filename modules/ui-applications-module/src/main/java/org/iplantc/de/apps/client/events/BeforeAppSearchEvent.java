package org.iplantc.de.apps.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/11/15.
 *
 * @author jstroot
 */
public class BeforeAppSearchEvent extends GwtEvent<BeforeAppSearchEvent.BeforeAppSearchEventHandler> {
    public static interface BeforeAppSearchEventHandler extends EventHandler {
        void onBeforeAppSearch(BeforeAppSearchEvent event);
    }

    public static interface HasBeforeAppSearchEventHandlers {
        HandlerRegistration addBeforeAppSearchEventHandler(BeforeAppSearchEventHandler handler);
    }

    public static final Type<BeforeAppSearchEventHandler> TYPE = new Type<>();

    public Type<BeforeAppSearchEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(BeforeAppSearchEventHandler handler) {
        handler.onBeforeAppSearch(this);
    }
}
