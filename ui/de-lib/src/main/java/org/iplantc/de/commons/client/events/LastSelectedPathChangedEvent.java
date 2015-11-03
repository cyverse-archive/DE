/**
 *
 */
package org.iplantc.de.commons.client.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author sriram
 */
public class LastSelectedPathChangedEvent extends GwtEvent<LastSelectedPathChangedEvent.LastSelectedPathChangedEventHandler> {

    /**
     * @author sriram
     */
    public static interface LastSelectedPathChangedEventHandler extends EventHandler {
        public void onLastSelectedPathChanged(LastSelectedPathChangedEvent event);
    }

    public static final GwtEvent.Type<LastSelectedPathChangedEventHandler> TYPE = new GwtEvent.Type<>();
    private final boolean updateSilently;

    public LastSelectedPathChangedEvent(final boolean updateSilently) {
        this.updateSilently = updateSilently;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<LastSelectedPathChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isUpdateSilently() {
        return updateSilently;
    }

    @Override
    protected void dispatch(LastSelectedPathChangedEventHandler handler) {
        handler.onLastSelectedPathChanged(this);
    }
}
