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
public class RenameCategorySelected extends GwtEvent<RenameCategorySelected.RenameCategorySelectedHandler> {
    public static interface HasRenameCategorySelectedHandlers {
        HandlerRegistration addRenameCategorySelectedHandler(RenameCategorySelectedHandler handler);
    }

    public static interface RenameCategorySelectedHandler extends EventHandler {
        void onRenameCategorySelected(RenameCategorySelected event);
    }
    public static Type<RenameCategorySelectedHandler> TYPE = new Type<>();
    private final AppCategory appCategory;

    public RenameCategorySelected(final AppCategory appCategory) {
        Preconditions.checkNotNull(appCategory);

        this.appCategory = appCategory;
    }

    public AppCategory getAppCategory() {
        return appCategory;
    }

    public Type<RenameCategorySelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RenameCategorySelectedHandler handler) {
        handler.onRenameCategorySelected(this);
    }
}
