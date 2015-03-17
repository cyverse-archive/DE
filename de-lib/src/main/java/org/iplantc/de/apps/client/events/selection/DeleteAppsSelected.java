package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class DeleteAppsSelected extends GwtEvent<DeleteAppsSelected.DeleteAppsSelectedHandler> {
    public static interface DeleteAppsSelectedHandler extends EventHandler {
        void onDeleteAppsSelected(DeleteAppsSelected event);
    }

    public static interface HasDeleteAppsSelectedHandlers {
        HandlerRegistration addDeleteAppsSelectedHandler(DeleteAppsSelectedHandler handler);
    }

    public static final Type<DeleteAppsSelectedHandler> TYPE = new Type<>();
    private final List<App> appsToBeDeleted;

    public DeleteAppsSelected(final List<App> appsToBeDeleted) {
        Preconditions.checkNotNull(appsToBeDeleted);
        Preconditions.checkArgument(!appsToBeDeleted.isEmpty());
        this.appsToBeDeleted = appsToBeDeleted;
    }

    public List<App> getAppsToBeDeleted() {
        return appsToBeDeleted;
    }

    public Type<DeleteAppsSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(DeleteAppsSelectedHandler handler) {
        handler.onDeleteAppsSelected(this);
    }
}
