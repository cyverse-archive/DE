package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class RunAppSelected extends GwtEvent<RunAppSelected.RunAppSelectedHandler> {
    public static interface HasRunAppSelectedHandlers {
        HandlerRegistration addRunAppSelectedHandler(RunAppSelectedHandler handler);
    }

    public static interface RunAppSelectedHandler extends EventHandler {
        void onRunAppSelected(RunAppSelected event);
    }
    public static final Type<RunAppSelectedHandler> TYPE = new Type<>();
    private final App app;

    public RunAppSelected(final App app) {
        Preconditions.checkNotNull(app);
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public Type<RunAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RunAppSelectedHandler handler) {
        handler.onRunAppSelected(this);
    }
}
