package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class AppNameSelectedEvent extends GwtEvent<AppNameSelectedEvent.AppNameSelectedEventHandler> {

    public interface AppNameSelectedEventHandler extends EventHandler {
        void onAppNameSelected(AppNameSelectedEvent event);
    }

    public static interface HasAppNameSelectedEventHandlers {
        HandlerRegistration addAppNameSelectedEventHandler(AppNameSelectedEventHandler handler);
    }

    private final App selectedApp;

    public AppNameSelectedEvent(App selectedApp) {
        this.selectedApp = selectedApp;
    }

    @Override
    public Type<AppNameSelectedEventHandler> getAssociatedType() {
        return AppHyperlinkCell.EVENT_TYPE;
    }

    public App getSelectedApp() {
        return selectedApp;
    }

    @Override
    protected void dispatch(AppNameSelectedEventHandler handler) {
        handler.onAppNameSelected(this);
    }
}
