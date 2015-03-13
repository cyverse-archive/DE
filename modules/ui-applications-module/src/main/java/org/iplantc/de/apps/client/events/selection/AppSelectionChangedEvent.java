/**
 * 
 */
package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author sriram
 *
 */
public class AppSelectionChangedEvent extends GwtEvent<AppSelectionChangedEvent.AppSelectionChangedEventHandler> {

    public interface AppSelectionChangedEventHandler extends EventHandler {
        void onAppSelectionChanged(AppSelectionChangedEvent event);
    }

    public static interface HasAppSelectionChangedEventHandlers {
        HandlerRegistration addAppSelectionChangedEventHandler(AppSelectionChangedEventHandler handler);
    }

    public static final GwtEvent.Type<AppSelectionChangedEventHandler> TYPE = new GwtEvent.Type<>();

    private final List<App> appSelection;

    public AppSelectionChangedEvent(final List<App> appSelection) {
        Preconditions.checkNotNull(appSelection);
        this.appSelection = appSelection;
    }

    public List<App> getAppSelection() {
        return appSelection;
    }

    @Override
    public GwtEvent.Type<AppSelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppSelectionChangedEventHandler handler) {
        handler.onAppSelectionChanged(this);
    }

}
