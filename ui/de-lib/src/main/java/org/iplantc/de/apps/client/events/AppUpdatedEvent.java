package org.iplantc.de.apps.client.events;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by jstroot on 3/12/15.
 *
 * @author jstroot
 */
public class AppUpdatedEvent extends GwtEvent<AppUpdatedEvent.AppUpdatedEventHandler> {
    public static interface AppUpdatedEventHandler extends EventHandler {
        void onAppUpdated(AppUpdatedEvent event);
    }
    public static final Type<AppUpdatedEventHandler> TYPE = new Type<>();
    private final App app;

    public AppUpdatedEvent(final App app) {
        Preconditions.checkNotNull(app);
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public Type<AppUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AppUpdatedEventHandler handler) {
        handler.onAppUpdated(this);
    }
}
