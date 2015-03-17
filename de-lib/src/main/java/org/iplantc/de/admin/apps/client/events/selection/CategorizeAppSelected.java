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
public class CategorizeAppSelected extends GwtEvent<CategorizeAppSelected.CategorizeAppSelectedHandler> {
    public static interface CategorizeAppSelectedHandler extends EventHandler {
        void onCategorizeAppSelected(CategorizeAppSelected event);
    }

    public static interface HasCategorizeAppSelectedHandlers {
        HandlerRegistration addCategorizeAppSelectedHandler(CategorizeAppSelectedHandler handler);
    }
    public static Type<CategorizeAppSelectedHandler> TYPE = new Type<>();
    private final List<App> apps;

    public CategorizeAppSelected(final List<App> apps) {
        Preconditions.checkNotNull(apps);
        Preconditions.checkArgument(!apps.isEmpty());

        this.apps = apps;
    }

    public List<App> getApps() {
        return apps;
    }

    public Type<CategorizeAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CategorizeAppSelectedHandler handler) {
        handler.onCategorizeAppSelected(this);
    }
}
