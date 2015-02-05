package org.iplantc.de.tags.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/5/15.
 *
 * @author jstroot
 */
public class TagAddedEvent extends GwtEvent<TagAddedEvent.TagAddedEventHandler> {
    public static interface TagAddedEventHandler extends EventHandler {
        void onTagAdded(TagAddedEvent event);
    }

    public static interface HasTagAddedEventHandlers {
        HandlerRegistration addTagAddedEventHandler(TagAddedEventHandler handler);
    }

    public static Type<TagAddedEventHandler> TYPE = new Type<>();

    public Type<TagAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(TagAddedEventHandler handler) {
        handler.onTagAdded(this);
    }


}
