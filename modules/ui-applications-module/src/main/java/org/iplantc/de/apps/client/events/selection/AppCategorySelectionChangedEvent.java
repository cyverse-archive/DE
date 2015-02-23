/**
 * 
 */
package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author sriram
 *
 */
public class AppCategorySelectionChangedEvent extends GwtEvent<AppCategorySelectionChangedEvent.AppCategorySelectionChangedEventHandler> {

    public interface AppCategorySelectionChangedEventHandler extends EventHandler {
        void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event);
    }

    public static interface HasAppCategorySelectionChangedEventHandlers {
        HandlerRegistration addAppCategorySelectedEventHandler(AppCategorySelectionChangedEventHandler handler);
    }

    public static final Type<AppCategorySelectionChangedEventHandler> TYPE = new Type<>();

    private final List<AppCategory> appCategorySelection;

    public AppCategorySelectionChangedEvent(final List<AppCategory> appCategorySelection) {
        this.appCategorySelection = appCategorySelection;
    }

    public List<AppCategory> getAppCategorySelection() {
        return appCategorySelection;
    }

    @Override
    public Type<AppCategorySelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(AppCategorySelectionChangedEventHandler handler) {
        handler.onAppCategorySelectionChanged(this);
    }

}
