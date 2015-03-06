package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class CopyWorkflowSelected extends GwtEvent<CopyWorkflowSelected.CopyWorkflowSelectedHandler> {
    public static interface CopyWorkflowSelectedHandler extends EventHandler {
        void onCopyWorkflowSelected(CopyWorkflowSelected event);
    }

    public static interface HasCopyWorkflowSelectedHandlers {
        HandlerRegistration addCopyWorkflowSelectedHandler(CopyWorkflowSelectedHandler handler);
    }

    public static Type<CopyWorkflowSelectedHandler> TYPE = new Type<>();
    private final List<App> wfsToBeCopied;

    public CopyWorkflowSelected(final List<App> wfsToBeCopied) {
        Preconditions.checkNotNull(wfsToBeCopied);
        Preconditions.checkArgument(!wfsToBeCopied.isEmpty());
        this.wfsToBeCopied = wfsToBeCopied;
    }

    public Type<CopyWorkflowSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<App> getWfsToBeCopied() {
        return wfsToBeCopied;
    }

    protected void dispatch(CopyWorkflowSelectedHandler handler) {
        handler.onCopyWorkflowSelected(this);
    }
}
