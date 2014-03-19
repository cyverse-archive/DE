package org.iplantc.de.apps.client.events.handlers;

import org.iplantc.de.apps.client.events.AppGroupCountUpdateEvent;

import com.google.gwt.event.shared.EventHandler;

public interface AppGroupCountUpdateEventHandler extends EventHandler {
    void onGroupCountUpdate(AppGroupCountUpdateEvent event);
}
