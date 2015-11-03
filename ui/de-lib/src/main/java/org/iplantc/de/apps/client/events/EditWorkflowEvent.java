package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.EditWorkflowEvent.EditWorkflowEventHandler;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * A GwtEvent fired when a user wants to edit an existing workflow.
 * 
 * @author psarando
 * 
 */
public class EditWorkflowEvent extends GwtEvent<EditWorkflowEventHandler> {

    public interface EditWorkflowEventHandler extends EventHandler {
        void onEditWorkflow(EditWorkflowEvent event);
    }

    public static final GwtEvent.Type<EditWorkflowEventHandler> TYPE = new GwtEvent.Type<>();
    private final HasId workflowToEdit;
    private Splittable serviceWorkflowJson;

    public EditWorkflowEvent(HasId workflowToEdit) {
        this.workflowToEdit = workflowToEdit;
    }

    public EditWorkflowEvent(HasId workflowToEdit, Splittable serviceWorkflowJson) {
        this(workflowToEdit);
        this.serviceWorkflowJson = serviceWorkflowJson;
    }

    @Override
    protected void dispatch(EditWorkflowEventHandler handler) {
        handler.onEditWorkflow(this);
    }

    @Override
    public GwtEvent.Type<EditWorkflowEventHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * @return the workflowToEdit
     */
    public HasId getWorkflowToEdit() {
        return workflowToEdit;
    }

    /**
     * @return the serviceWorkflowJson
     */
    public Splittable getServiceWorkflowJson() {
        return serviceWorkflowJson;
    }
}
