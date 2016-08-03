package org.iplantc.de.admin.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class AppBetaStatusChanged extends GwtEvent<AppBetaStatusChanged.AppBetaStatusChangedHandler> {

    public static interface AppBetaStatusChangedHandler extends EventHandler {
        void onAppBetaStatusChanged(AppBetaStatusChanged event);
    }

    public interface HasAppBetaStatusChangedHandlers {
        HandlerRegistration addAppBetaStatusChangedHandlers(AppBetaStatusChangedHandler handler);
    }
    public static Type<AppBetaStatusChangedHandler> TYPE = new Type<AppBetaStatusChangedHandler>();

    private App app;

    public AppBetaStatusChanged(App app) {
        this.app = app;
    }

    public Type<AppBetaStatusChangedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AppBetaStatusChangedHandler handler) {
        handler.onAppBetaStatusChanged(this);
    }

    public App getApp() {
        return app;
    }
}
