package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.handlers.CreateNewWorkflowEventHandler;

import com.google.gwt.event.shared.GwtEvent;

public class CreateNewWorkflowEvent extends GwtEvent<CreateNewWorkflowEventHandler> {

    public static final GwtEvent.Type<CreateNewWorkflowEventHandler> TYPE = new GwtEvent.Type<CreateNewWorkflowEventHandler>();

    @Override
    public GwtEvent.Type<CreateNewWorkflowEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CreateNewWorkflowEventHandler handler) {
        handler.createNewWorkflow();
    }

}
