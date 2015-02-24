package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

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

    public static Type<AppRatingSelectedHandler> TYPE = new Type<>();
    private final App selectedApp;
    private final int score;

    public AppRatingSelected(App selectedApp, int score) {
        this.selectedApp = selectedApp;
        this.score = score;
    }

    public Type<AppRatingSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public int getScore() {
        return score;
    }

    public App getSelectedApp() {
        return selectedApp;
    }

    protected void dispatch(AppRatingSelectedHandler handler) {
        handler.onAppRatingSelected(this);
    }
}
