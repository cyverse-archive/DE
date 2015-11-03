package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/23/15.
 *
 * @author jstroot
 */
public class AppRatingSelected extends GwtEvent<AppRatingSelected.AppRatingSelectedHandler> {

    public static interface AppRatingSelectedHandler extends EventHandler {
        void onAppRatingSelected(AppRatingSelected event);
    }

    public static interface HasAppRatingSelectedEventHandlers {
        HandlerRegistration addAppRatingSelectedHandler(AppRatingSelectedHandler handler);
    }

    public static final Type<AppRatingSelectedHandler> TYPE = new Type<>();
    private final App app;
    private final int score;

    public AppRatingSelected(final App app,
                             final int score) {
        Preconditions.checkNotNull(app);
        this.app = app;
        this.score = score;
    }

    public Type<AppRatingSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public int getScore() {
        return score;
    }

    public App getApp() {
        return app;
    }

    protected void dispatch(AppRatingSelectedHandler handler) {
        handler.onAppRatingSelected(this);
    }
}
