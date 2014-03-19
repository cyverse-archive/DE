package org.iplantc.de.apps.client.events;

import com.google.gwt.event.shared.GwtEvent;

public class AppFavoritedEvent extends GwtEvent<AppFavoritedEventHander> {

    public static final GwtEvent.Type<AppFavoritedEventHander> TYPE = new GwtEvent.Type<AppFavoritedEventHander>();

    private final String appId;

    private final boolean favorite;

    public AppFavoritedEvent(String appId, boolean favorite) {
        this.appId = appId;
        this.favorite = favorite;
    }

    @Override
    public GwtEvent.Type<AppFavoritedEventHander> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppFavoritedEventHander handler) {
        handler.onAppFavorited(this);
    }

    public String getAppId() {
        return appId;
    }

    public boolean isFavorite() {
        return favorite;
    }

}
