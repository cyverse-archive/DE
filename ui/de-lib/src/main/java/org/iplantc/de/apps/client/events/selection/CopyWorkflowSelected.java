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

    public static final Type<CopyWorkflowSelectedHandler> TYPE = new Type<>();
    private final List<App> apps;

    public CopyWorkflowSelected(final List<App> apps) {
        Preconditions.checkNotNull(apps);
        Preconditions.checkArgument(!apps.isEmpty());
        this.apps = apps;
    }

    public Type<CopyWorkflowSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<App> getApps() {
        return apps;
    }

    protected void dispatch(CopyWorkflowSelectedHandler handler) {
        handler.onCopyWorkflowSelected(this);
    }
}
