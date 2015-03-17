package org.iplantc.de.admin.apps.client.events.selection;

import org.iplantc.de.client.models.apps.AppCategory;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 3/9/15.
 *
 * @author jstroot
 */
public class AddCategorySelected extends GwtEvent<AddCategorySelected.AddCategorySelectedHandler> {
    public static interface AddCategorySelectedHandler extends EventHandler {
        void onAddCategorySelected(AddCategorySelected event);
    }

    public static interface HasAddCategorySelectedHandlers {
        HandlerRegistration addAddCategorySelectedHandler(AddCategorySelectedHandler handler);
    }

    public static Type<AddCategorySelectedHandler> TYPE = new Type<>();
    private final List<AppCategory> appCategories;
    private final String newCategoryName;

    public AddCategorySelected(final List<AppCategory> appCategories,
                               final String newCategoryName) {
        this.newCategoryName = newCategoryName;
        Preconditions.checkNotNull(appCategories);
        Preconditions.checkArgument(!appCategories.isEmpty());
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newCategoryName));

        this.appCategories = appCategories;
    }

    public List<AppCategory> getAppCategories() {
        return appCategories;
    }

    public Type<AddCategorySelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public String getNewCategoryName() {
        return newCategoryName;
    }

    protected void dispatch(AddCategorySelectedHandler handler) {
        handler.onAddCategorySelected(this);
    }
}
