package org.iplantc.de.apps.client.events;

import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class AppCommentSelectedEvent extends
        GwtEvent<AppCommentSelectedEvent.AppCommentSelectedEventHandler> {

    public interface AppCommentSelectedEventHandler extends EventHandler {
        public void onAppCommentSelectedEvent(AppCommentSelectedEvent event);
    }

    public static interface HasAppCommentSelectedEventHandlers {
        HandlerRegistration addAppCommentSelectedEventHandlers(AppCommentSelectedEventHandler handler);
    }

    public static Type<AppCommentSelectedEventHandler> TYPE = new Type<AppCommentSelectedEventHandler>();

    private final App app;

    public AppCommentSelectedEvent(App app) {
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    @Override
    public Type<AppCommentSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppCommentSelectedEventHandler handler) {
        handler.onAppCommentSelectedEvent(this);
    }
}
