package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class AppNameSelectedEvent extends GwtEvent<AppNameSelectedEvent.AppNameSelectedEventHandler> {

    public interface AppNameSelectedEventHandler extends EventHandler {
        void onAppNameSelected(AppNameSelectedEvent event);
    }

    public static interface HasAppNameSelectedEventHandlers {
        HandlerRegistration addAppNameSelectedEventHandler(AppNameSelectedEventHandler handler);
    }

    public static final Type<AppNameSelectedEventHandler> TYPE = new Type<>();

    private final App selectedApp;

    public AppNameSelectedEvent(App selectedApp) {
        Preconditions.checkNotNull(selectedApp);
        this.selectedApp = selectedApp;
    }

    @Override
    public Type<AppNameSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public App getSelectedApp() {
        return selectedApp;
    }

    @Override
    protected void dispatch(AppNameSelectedEventHandler handler) {
        handler.onAppNameSelected(this);
    }
}
