package org.iplantc.de.admin.desktop.client.ontologies.events;

import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class RestoreAppButtonClicked
        extends GwtEvent<RestoreAppButtonClicked.RestoreAppButtonClickedHandler> {
    public static interface RestoreAppButtonClickedHandler extends EventHandler {
        void onRestoreAppButtonClicked(RestoreAppButtonClicked event);
    }

    public interface HasRestoreAppButtonClickedHandlers {
        HandlerRegistration addRestoreAppButtonClickedHandlers(RestoreAppButtonClickedHandler handler);
    }
    public static Type<RestoreAppButtonClickedHandler> TYPE = new Type<RestoreAppButtonClickedHandler>();

    private App app;

    public RestoreAppButtonClicked (App app) {
        this.app = app;
    }

    public Type<RestoreAppButtonClickedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RestoreAppButtonClickedHandler handler) {
        handler.onRestoreAppButtonClicked(this);
    }

    public App getApp() {
        return app;
    }
}
