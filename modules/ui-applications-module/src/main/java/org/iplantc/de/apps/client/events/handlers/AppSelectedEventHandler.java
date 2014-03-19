package org.iplantc.de.apps.client.events.handlers;

import org.iplantc.de.apps.client.events.AppSelectedEvent;

import com.google.gwt.event.shared.EventHandler;

public interface AppSelectedEventHandler extends EventHandler {

    public void onSelection(AppSelectedEvent event);
        
}
