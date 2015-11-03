package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author sriram, jstroot
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
    private final List<String> groupHierarchy;

    public AppCategorySelectionChangedEvent(final List<AppCategory> appCategorySelection,
                                            final List<String> groupHierarchy) {
        Preconditions.checkNotNull(appCategorySelection);
        Preconditions.checkNotNull(groupHierarchy);
        this.appCategorySelection = appCategorySelection;
        this.groupHierarchy = groupHierarchy;
    }

    public List<AppCategory> getAppCategorySelection() {
        return appCategorySelection;
    }

    @Override
    public Type<AppCategorySelectionChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<String> getGroupHierarchy() {
        return groupHierarchy;
    }

    @Override
    protected void dispatch(AppCategorySelectionChangedEventHandler handler) {
        handler.onAppCategorySelectionChanged(this);
    }

}
