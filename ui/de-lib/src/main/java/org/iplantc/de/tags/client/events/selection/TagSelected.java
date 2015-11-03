package org.iplantc.de.tags.client.events.selection;

import org.iplantc.de.client.models.tags.Tag;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/5/15.
 *
 * @author jstroot
 */
public class TagSelected extends GwtEvent<TagSelected.TagSelectedHandler> {
    public static interface HasTagSelectedHandlers {
        HandlerRegistration addTagSelectedHandler(TagSelectedHandler handler);
    }

    public static interface TagSelectedHandler extends EventHandler {
        void onTagSelected(TagSelected event);
    }
    public static Type<TagSelectedHandler> TYPE = new Type<>();
    private final Tag tag;

    public TagSelected(final Tag tag) {
        this.tag = tag;
    }

    public Type<TagSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public Tag getTag() {
        return tag;
    }

    protected void dispatch(TagSelectedHandler handler) {
        handler.onTagSelected(this);
    }
}
