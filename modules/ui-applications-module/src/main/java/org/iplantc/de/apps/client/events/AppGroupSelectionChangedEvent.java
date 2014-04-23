/**
 * 
 */
package org.iplantc.de.apps.client.events;

import org.iplantc.de.client.models.apps.AppGroup;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author sriram
 *
 */
public class AppGroupSelectionChangedEvent extends GwtEvent<AppGroupSelectionChangedEvent.AppGroupSelectionChangedEventHandler> {

    public interface AppGroupSelectionChangedEventHandler extends EventHandler {
        void onAppGroupSelectionChanged(AppGroupSelectionChangedEvent event);
    }

    public static interface HasAppGroupSelectionChangedEventHandlers {
        HandlerRegistration addAppGroupSelectedEventHandler(AppGroupSelectionChangedEventHandler handler);
    }

    public static final Type<AppGroupSelectionChangedEventHandler> TYPE = new Type<AppGroupSelectionChangedEventHandler>();

    private final List<AppGroup> appGroupSelection;

    public AppGroupSelectionChangedEvent(final List<AppGroup> appGroupSelection) {
        this.appGroupSelection = appGroupSelection;
    }

    public List<AppGroup> getAppGroupSelection() {
        return appGroupSelection;
    }

    @Override
    public Type<AppGroupSelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppGroupSelectionChangedEventHandler handler) {
        handler.onAppGroupSelectionChanged(this);
    }

}
