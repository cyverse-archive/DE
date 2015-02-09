package org.iplantc.de.tags.client.events;

import org.iplantc.de.client.models.tags.Tag;

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
    private final Tag tag;

    public TagAddedEvent(Tag tag) {
        this.tag = tag;
    }

    public Type<TagAddedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public Tag getTag() {
        return tag;
    }

    protected void dispatch(TagAddedEventHandler handler) {
        handler.onTagAdded(this);
    }


}
