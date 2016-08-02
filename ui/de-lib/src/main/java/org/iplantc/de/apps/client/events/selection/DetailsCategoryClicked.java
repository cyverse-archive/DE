package org.iplantc.de.apps.client.events.selection;


import org.iplantc.de.client.models.apps.AppCategory;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author aramsey
 */
public class DetailsCategoryClicked
        extends GwtEvent<DetailsCategoryClicked.DetailsCategoryClickedHandler> {
    public static interface DetailsCategoryClickedHandler extends EventHandler {
        void onDetailsCategoryClicked(DetailsCategoryClicked event);
    }

    public interface HasDetailsCategoryClickedHandlers {
        HandlerRegistration addDetailsCategoryClickedHandler(DetailsCategoryClickedHandler handler);
    }
    public static Type<DetailsCategoryClickedHandler> TYPE = new Type<DetailsCategoryClickedHandler>();

    private AppCategory Category;

    public DetailsCategoryClicked(AppCategory Category) {
        this.Category = Category;
    }

    public Type<DetailsCategoryClickedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DetailsCategoryClickedHandler handler) {
        handler.onDetailsCategoryClicked(this);
    }

    public AppCategory getCategory() {
        return Category;
    }
}
