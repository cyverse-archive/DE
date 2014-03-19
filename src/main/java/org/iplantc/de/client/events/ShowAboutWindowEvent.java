package org.iplantc.de.client.events;

import org.iplantc.de.client.events.ShowAboutWindowEvent.ShowAboutWindowEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ShowAboutWindowEvent extends GwtEvent<ShowAboutWindowEventHandler> {

    public interface ShowAboutWindowEventHandler extends EventHandler {
        void showAboutWindowRequested(ShowAboutWindowEvent event);
    }

    public static final GwtEvent.Type<ShowAboutWindowEventHandler> TYPE = new GwtEvent.Type<ShowAboutWindowEventHandler>();

    @Override
    public GwtEvent.Type<ShowAboutWindowEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowAboutWindowEventHandler handler) {
        handler.showAboutWindowRequested(this);
    }

}
