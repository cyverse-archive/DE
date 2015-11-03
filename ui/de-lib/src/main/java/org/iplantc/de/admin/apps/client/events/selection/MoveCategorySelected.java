package org.iplantc.de.admin.apps.client.events.selection;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/9/15.
 *
 * @author jstroot
 */
public class MoveCategorySelected extends GwtEvent<MoveCategorySelected.MoveCategorySelectedHandler> {
    public static interface HasMoveCategorySelectedHandlers {
        HandlerRegistration addMoveCategorySelectedHandler(MoveCategorySelectedHandler handler);
    }

    public static interface MoveCategorySelectedHandler extends EventHandler {
        void onMoveCategorySelected(MoveCategorySelected event);
    }
    public static Type<MoveCategorySelectedHandler> TYPE = new Type<>();
    private final AppCategory appCategory;

    public MoveCategorySelected(final AppCategory appCategory) {
        Preconditions.checkNotNull(appCategory);

        this.appCategory = appCategory;
    }

    public AppCategory getAppCategory() {
        return appCategory;
    }

    public Type<MoveCategorySelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(MoveCategorySelectedHandler handler) {
        handler.onMoveCategorySelected(this);
    }
}
