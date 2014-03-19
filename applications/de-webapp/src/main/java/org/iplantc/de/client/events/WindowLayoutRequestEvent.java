/**
 * 
 */
package org.iplantc.de.client.events;

import org.iplantc.de.client.desktop.layout.DesktopLayoutType;
import org.iplantc.de.client.events.WindowLayoutRequestEvent.WindowLayoutRequestEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 * 
 */
public class WindowLayoutRequestEvent extends GwtEvent<WindowLayoutRequestEventHandler> {

    public interface WindowLayoutRequestEventHandler extends EventHandler {

        void onWindowLayoutRequest(WindowLayoutRequestEvent event);

    }

    public static final GwtEvent.Type<WindowLayoutRequestEventHandler> TYPE = new GwtEvent.Type<WindowLayoutRequestEventHandler>();
    private DesktopLayoutType type;

    /**
     * @return the type
     */
    public DesktopLayoutType getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(DesktopLayoutType type) {
        this.type = type;
    }

    public WindowLayoutRequestEvent(DesktopLayoutType type) {
        this.type = type;
    }

    @Override
    public GwtEvent.Type<WindowLayoutRequestEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(WindowLayoutRequestEventHandler handler) {
        handler.onWindowLayoutRequest(this);
    }

}
