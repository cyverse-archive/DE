package org.iplantc.de.tags.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/5/15.
 *
 * @author jstroot
 */
public class RequestCreateTag extends GwtEvent<RequestCreateTag.RequestCreateTagHandler> {
    public static interface RequestCreateTagHandler extends EventHandler {
        void onRequestCreateTag(RequestCreateTag event);
    }

    public static interface HasRequestCreateTagHandlers {
        HandlerRegistration addRequestCreateTagHandler(RequestCreateTagHandler handler);
    }

    private final String newTagText;

    public RequestCreateTag(final String newTagText){
        this.newTagText = newTagText;
    }

    public static Type<RequestCreateTagHandler> TYPE = new Type<>();

    public Type<RequestCreateTagHandler> getAssociatedType() {
        return TYPE;
    }

    public String getNewTagText() {
        return newTagText;
    }

    protected void dispatch(RequestCreateTagHandler handler) {
        handler.onRequestCreateTag(this);
    }
}
