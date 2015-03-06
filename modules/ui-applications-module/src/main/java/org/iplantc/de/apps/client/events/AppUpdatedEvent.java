package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.AppUpdatedEvent.AppUpdatedEventHandler;
import org.iplantc.de.client.models.HasId;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class AppUpdatedEvent extends GwtEvent<AppUpdatedEventHandler> {

    public interface AppUpdatedEventHandler extends EventHandler {
        void onAppUpdated(AppUpdatedEvent event);
    }

    public static final GwtEvent.Type<AppUpdatedEventHandler> TYPE = new GwtEvent.Type<>();
    private final HasId app;

    public AppUpdatedEvent(final HasId app) {
        Preconditions.checkNotNull(app);
        this.app = app;
    }

    @Override
    public GwtEvent.Type<AppUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppUpdatedEventHandler handler) {
        handler.onAppUpdated(this);
    }

    public HasId getApp() {
        return app;
    }

}
