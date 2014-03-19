/**
 * 
 */
package org.iplantc.de.apps.client.events;

import org.iplantc.de.apps.client.events.handlers.AppSelectedEventHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 *
 */
public class AppSelectedEvent extends GwtEvent<AppSelectedEventHandler> {

    /**
     * Defines the GWT Event Type.
     * 
     * @see org.iplantc.de.apps.client.events.handlers.AppSelectedEventHandler
     */
    public static final GwtEvent.Type<AppSelectedEventHandler> TYPE = new GwtEvent.Type<AppSelectedEventHandler>();

    private final String appId;
    private final Object source;

    public AppSelectedEvent(String appId, Object source) {
        this.appId = appId;
        this.source = source;
    }

    @Override
    public GwtEvent.Type<AppSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppSelectedEventHandler handler) {
        handler.onSelection(this);
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    @Override
    public Object getSource() {
        return source;
    }

}
