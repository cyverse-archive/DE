package org.iplantc.de.tags.client.events.selection;

import org.iplantc.de.client.models.tags.Tag;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/5/15.
 * @author jstroot
 */
public class RemoveTagSelected extends GwtEvent<RemoveTagSelected.RemoveTagSelectedHandler> {
    public static interface RemoveTagSelectedHandler extends EventHandler {
        void onRemoveTagSelected(RemoveTagSelected event);
    }

    public static interface HasRemoveTagSelectedHandlers {
        HandlerRegistration addRemoveTagSelectedHandler(RemoveTagSelectedHandler handler);
    }

    private final Tag tag;

    public RemoveTagSelected(final Tag tag) {
        this.tag = tag;
    }

    public static Type<RemoveTagSelectedHandler> TYPE = new Type<RemoveTagSelectedHandler>();

    public Type<RemoveTagSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public Tag getTag() {
        return tag;
    }

    protected void dispatch(RemoveTagSelectedHandler handler) {
        handler.onRemoveTagSelected(this);
    }
}
