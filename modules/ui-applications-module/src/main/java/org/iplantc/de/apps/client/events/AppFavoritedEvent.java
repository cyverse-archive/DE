package org.iplantc.de.apps.client.events;

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
    private final String appId;
    private final boolean favorite;

    public AppFavoritedEvent(String appId, boolean favorite) {
        this.appId = appId;
        this.favorite = favorite;
    }

    public String getAppId() {
        return appId;
    }

    @Override
    public GwtEvent.Type<AppFavoritedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isFavorite() {
        return favorite;
    }

    @Override
    protected void dispatch(AppFavoritedEventHandler handler) {
        handler.onAppFavorited(this);
    }
}
