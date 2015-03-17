package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class AppFavoriteSelectedEvent extends GwtEvent<AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler> {

    public interface AppFavoriteSelectedEventHandler extends EventHandler {
        void onAppFavoriteSelected(AppFavoriteSelectedEvent event);
    }

    public static interface HasAppFavoriteSelectedEventHandlers {
        HandlerRegistration addAppFavoriteSelectedEventHandlers(AppFavoriteSelectedEventHandler handler);
    }

    public static final Type<AppFavoriteSelectedEventHandler> TYPE = new Type<>();
    private final App app;

    public AppFavoriteSelectedEvent(final App app) {
        Preconditions.checkNotNull(app);
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    @Override
    public Type<AppFavoriteSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppFavoriteSelectedEventHandler handler) {
        handler.onAppFavoriteSelected(this);
    }
}
