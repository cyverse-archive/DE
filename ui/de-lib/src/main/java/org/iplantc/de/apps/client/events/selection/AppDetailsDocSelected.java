package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/4/15.
 *
 * @author jstroot
 */
public class AppDetailsDocSelected extends GwtEvent<AppDetailsDocSelected.AppDetailsDocSelectedHandler> {
    public static interface AppDetailsDocSelectedHandler extends EventHandler {
        void onAppDetailsDocSelected(AppDetailsDocSelected event);
    }

    public static interface HasAppDetailsDocSelectedHandlers {
        HandlerRegistration addAppDetailsDocSelectedHandler(AppDetailsDocSelectedHandler handler);
    }

    public static final Type<AppDetailsDocSelectedHandler> TYPE = new Type<>();
    private final App app;

    public AppDetailsDocSelected(final App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public Type<AppDetailsDocSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(AppDetailsDocSelectedHandler handler) {
        handler.onAppDetailsDocSelected(this);
    }
}
