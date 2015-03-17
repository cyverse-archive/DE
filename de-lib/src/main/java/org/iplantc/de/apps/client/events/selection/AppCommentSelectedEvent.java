package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class AppCommentSelectedEvent extends
        GwtEvent<AppCommentSelectedEvent.AppCommentSelectedEventHandler> {

    public interface AppCommentSelectedEventHandler extends EventHandler {
        public void onAppCommentSelectedEvent(AppCommentSelectedEvent event);
    }

    public static interface HasAppCommentSelectedEventHandlers {
        HandlerRegistration addAppCommentSelectedEventHandlers(AppCommentSelectedEventHandler handler);
    }

    public static final Type<AppCommentSelectedEventHandler> TYPE = new Type<>();

    private final App app;

    public AppCommentSelectedEvent(final App app) {
        Preconditions.checkNotNull(app);
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
