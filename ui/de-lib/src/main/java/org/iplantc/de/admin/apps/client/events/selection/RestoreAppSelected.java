package org.iplantc.de.admin.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

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
public class RestoreAppSelected extends GwtEvent<RestoreAppSelected.RestoreAppSelectedHandler> {
    public static interface HasRestoreAppSelectedHandlers {
        HandlerRegistration addRestoreAppSelectedHandler(RestoreAppSelectedHandler handler);
    }

    public static interface RestoreAppSelectedHandler extends EventHandler {
        void onRestoreAppSelected(RestoreAppSelected event);
    }

    public static Type<RestoreAppSelectedHandler> TYPE = new Type<>();
    private final List<App> apps;

    public RestoreAppSelected(final List<App> apps) {
        Preconditions.checkNotNull(apps);
        Preconditions.checkArgument(!apps.isEmpty());

        this.apps = apps;
    }

    public List<App> getApps() {
        return apps;
    }

    public Type<RestoreAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RestoreAppSelectedHandler handler) {
        handler.onRestoreAppSelected(this);
    }
}
