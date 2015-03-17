package org.iplantc.de.admin.apps.client.events.selection;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 3/9/15.
 *
 * @author jstroot
 */
public class DeleteCategorySelected extends GwtEvent<DeleteCategorySelected.DeleteCategorySelectedHandler> {
    public static interface DeleteCategorySelectedHandler extends EventHandler {
        void onDeleteCategorySelected(DeleteCategorySelected event);
    }

    public static interface HasDeleteCategorySelectedHandlers {
        HandlerRegistration addDeleteCategorySelectedHandler(DeleteCategorySelectedHandler handler);
    }
    public static Type<DeleteCategorySelectedHandler> TYPE = new Type<>();
    private final List<AppCategory> appCategories;

    public DeleteCategorySelected(final List<AppCategory> appCategories) {
        Preconditions.checkNotNull(appCategories);
        Preconditions.checkArgument(!appCategories.isEmpty());

        this.appCategories = appCategories;
    }

    public List<AppCategory> getAppCategories() {
        return appCategories;
    }

    public Type<DeleteCategorySelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteCategorySelectedHandler handler) {
        handler.onDeleteCategorySelected(this);
    }

}
