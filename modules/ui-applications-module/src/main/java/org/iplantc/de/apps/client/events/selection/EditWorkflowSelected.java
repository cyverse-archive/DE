package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/11/15.
 *
 * @author jstroot
 */
public class EditWorkflowSelected extends GwtEvent<EditWorkflowSelected.EditWorkflowSelectedHandler> {
    public static interface EditWorkflowSelectedHandler extends EventHandler {
        void onEditWorkflowSelected(EditWorkflowSelected event);
    }

    public static interface HasEditWorkflowSelectedHandlers {
        HandlerRegistration addEditWorkflowSelectedHandler(EditWorkflowSelectedHandler handler);
    }

    public static final Type<EditWorkflowSelectedHandler> TYPE = new Type<>();
    private final App workFlow;

    public EditWorkflowSelected(final App workFlow) {
        Preconditions.checkNotNull(workFlow);
        Preconditions.checkArgument(workFlow.getStepCount() > 1,
                                    "Step count must be greater than 1 to be a workflow! " +
                                        "\n Are you using the right event?");
        this.workFlow = workFlow;
    }

    public Type<EditWorkflowSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public App getWorkFlow() {
        return workFlow;
    }

    protected void dispatch(EditWorkflowSelectedHandler handler) {
        handler.onEditWorkflowSelected(this);
    }
}
