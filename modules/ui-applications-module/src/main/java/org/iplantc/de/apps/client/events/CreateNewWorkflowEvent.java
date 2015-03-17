package org.iplantc.de.apps.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * FIXME This is more of a message than an event
 * @author jstroot
 */
public class CreateNewWorkflowEvent extends GwtEvent<CreateNewWorkflowEvent.CreateNewWorkflowEventHandler> {

    public static final GwtEvent.Type<CreateNewWorkflowEventHandler> TYPE = new GwtEvent.Type<>();

    @Override
    public GwtEvent.Type<CreateNewWorkflowEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CreateNewWorkflowEventHandler handler) {
        handler.createNewWorkflow();
    }

    public static interface CreateNewWorkflowEventHandler extends EventHandler {

        /**
         * Fire when a user wants to create a new workflow.
         */
        void createNewWorkflow();

    }
}
