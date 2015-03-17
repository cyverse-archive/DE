package org.iplantc.de.tags.client.events;

import org.iplantc.de.client.models.tags.Tag;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/6/15.
 *
 * @author jstroot
 */
public class TagCreated extends GwtEvent<TagCreated.TagCreatedHandler> {
    public static interface TagCreatedHandler extends EventHandler {
        void onTagCreated(TagCreated event);
    }

    public static interface HasTagCreatedHandlers {
        HandlerRegistration addTagCreatedHandler(TagCreatedHandler handler);
    }

    public static Type<TagCreatedHandler> TYPE = new Type<>();
    private final Tag tag;

    public TagCreated(Tag tag) {
        this.tag = tag;
    }

    public Type<TagCreatedHandler> getAssociatedType() {
        return TYPE;
    }

    public Tag getTag() {
        return tag;
    }

    protected void dispatch(TagCreatedHandler handler) {
        handler.onTagCreated(this);
    }
}
