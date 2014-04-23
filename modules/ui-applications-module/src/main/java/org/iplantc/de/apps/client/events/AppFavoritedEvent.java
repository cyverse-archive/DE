package org.iplantc.de.apps.client.events;

import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class AppFavoritedEvent extends GwtEvent<AppFavoritedEvent.AppFavoritedEventHandler> {

    public static interface AppFavoritedEventHandler extends EventHandler {
        void onAppFavorited(AppFavoritedEvent appFavoritedEvent);
    }

    public static interface HasAppFavoritedEventHandlers {
        HandlerRegistration addAppFavoritedEventHandler(AppFavoritedEventHandler eventHandler);
    }

    public static final GwtEvent.Type<AppFavoritedEventHandler> TYPE = new GwtEvent.Type<AppFavoritedEventHandler>();
    private final App app;

    public AppFavoritedEvent(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    @Override
    public GwtEvent.Type<AppFavoritedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppFavoritedEventHandler handler) {
        handler.onAppFavorited(this);
    }
}
