package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.AppSavedEvent.AppSavedEventHandler;
import org.iplantc.de.client.models.HasId;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class AppSavedEvent extends GwtEvent<AppSavedEventHandler> {

    public interface AppSavedEventHandler extends EventHandler {
        void onAppSaved(AppSavedEvent event);
    }

    public static final GwtEvent.Type<AppSavedEventHandler> TYPE = new GwtEvent.Type<>();
    private final HasId app;

    public AppSavedEvent(final HasId app) {
        Preconditions.checkNotNull(app);
        this.app = app;
    }

    @Override
    public GwtEvent.Type<AppSavedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppSavedEventHandler handler) {
        handler.onAppSaved(this);
    }

    public HasId getApp() {
        return app;
    }

}
