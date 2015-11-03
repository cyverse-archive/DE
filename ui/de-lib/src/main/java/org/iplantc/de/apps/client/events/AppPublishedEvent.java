package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.AppPublishedEvent.AppPublishedEventHandler;
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Used to communicate when an <code>App</code> has been successfully published.
 * 
 * @author jstroot
 * 
 */
public class AppPublishedEvent extends GwtEvent<AppPublishedEventHandler> {

    public interface AppPublishedEventHandler extends EventHandler {

        void onAppPublished(AppPublishedEvent appPublishedEvent);
    }

    public static final GwtEvent.Type<AppPublishedEventHandler> TYPE = new GwtEvent.Type<>();
    private final App publishedApp;

    public AppPublishedEvent(App publishedApp) {
        this.publishedApp = publishedApp;
    }

    public App getPublishedApp() {
        return publishedApp;
    }

    @Override
    protected void dispatch(AppPublishedEventHandler handler) {
        handler.onAppPublished(this);
    }

    @Override
    public GwtEvent.Type<AppPublishedEventHandler> getAssociatedType() {
        return TYPE;
    }
}
