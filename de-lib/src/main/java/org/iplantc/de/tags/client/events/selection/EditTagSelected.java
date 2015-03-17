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
public class EditTagSelected extends GwtEvent<EditTagSelected.EditTagSelectedHandler> {
    public static interface EditTagSelectedHandler extends EventHandler {
        void onEditTagSelected(EditTagSelected event);
    }

    public static interface HasEditTagSelectedHandlers {
        HandlerRegistration addEditTagSelectedHandler(EditTagSelectedHandler handler);
    }

    public static Type<EditTagSelectedHandler> TYPE = new Type<>();
    private final Tag tag;

    public EditTagSelected(final Tag tag) {
        this.tag = tag;
    }

    public Type<EditTagSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public Tag getTag() {
        return tag;
    }

    protected void dispatch(EditTagSelectedHandler handler) {
        handler.onEditTagSelected(this);
    }
}
