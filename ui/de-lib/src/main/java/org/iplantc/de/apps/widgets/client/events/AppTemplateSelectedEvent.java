package org.iplantc.de.apps.widgets.client.events;

import org.iplantc.de.apps.widgets.client.events.AppTemplateSelectedEvent.AppTemplateSelectedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class AppTemplateSelectedEvent extends GwtEvent<AppTemplateSelectedEventHandler> {

    public interface AppTemplateSelectedEventHandler extends EventHandler {
        void onAppTemplateSelected(AppTemplateSelectedEvent appTemplateSelectedEvent);
    }

    public static interface HasAppTemplateSelectedEventHandlers {
        HandlerRegistration addAppTemplateSelectedEventHandler(AppTemplateSelectedEventHandler handler);
    }

    public static final GwtEvent.Type<AppTemplateSelectedEventHandler> TYPE = new GwtEvent.Type<AppTemplateSelectedEventHandler>();

    public AppTemplateSelectedEvent() {
    }

    @Override
    public GwtEvent.Type<AppTemplateSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppTemplateSelectedEventHandler handler) {
        handler.onAppTemplateSelected(this);
    }

}
