package org.iplantc.de.apps.client.events.selection;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class CreateNewWorkflowSelected extends GwtEvent<CreateNewWorkflowSelected.CreateNewWorkflowSelectedHandler> {
    public static interface CreateNewWorkflowSelectedHandler extends EventHandler {
        void onCreateNewWorkflowSelected(CreateNewWorkflowSelected event);
    }

    public static interface HasCreateNewWorkflowSelectedHandlers {
        HandlerRegistration addCreateNewWorkflowSelectedHandler(CreateNewWorkflowSelectedHandler handler);
    }
    public static final Type<CreateNewWorkflowSelectedHandler> TYPE = new Type<>();

    public Type<CreateNewWorkflowSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CreateNewWorkflowSelectedHandler handler) {
        handler.onCreateNewWorkflowSelected(this);
    }
}
