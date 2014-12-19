package org.iplantc.de.desktop.client.events;

import org.iplantc.de.desktop.client.events.WindowHeadingUpdatedEvent.WindowHeadingUpdatedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class WindowHeadingUpdatedEvent extends GwtEvent<WindowHeadingUpdatedEventHandler> {

    public interface WindowHeadingUpdatedEventHandler extends EventHandler {
        void onWindowHeadingUpdated(WindowHeadingUpdatedEvent event);
    }

    public static final GwtEvent.Type<WindowHeadingUpdatedEventHandler> TYPE = new GwtEvent.Type<WindowHeadingUpdatedEvent.WindowHeadingUpdatedEventHandler>();
    private String windowTitle = null;

    public WindowHeadingUpdatedEvent(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public WindowHeadingUpdatedEvent() {}

    @Override
    public GwtEvent.Type<WindowHeadingUpdatedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    @Override
    protected void dispatch(WindowHeadingUpdatedEventHandler handler) {
        handler.onWindowHeadingUpdated(this);
    }

}
