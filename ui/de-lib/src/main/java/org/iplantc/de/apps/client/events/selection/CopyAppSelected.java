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
public class CopyAppSelected extends GwtEvent<CopyAppSelected.CopyAppSelectedHandler> {
    public static interface CopyAppSelectedHandler extends EventHandler {
        void onCopyAppSelected(CopyAppSelected event);
    }

    public static interface HasCopyAppSelectedHandlers {
        HandlerRegistration addCopyAppSelectedHandler(CopyAppSelectedHandler handler);
    }

    public static final Type<CopyAppSelectedHandler> TYPE = new Type<>();
    private final List<App> apps;

    public CopyAppSelected(final List<App> apps) {
        Preconditions.checkNotNull(apps);
        Preconditions.checkArgument(!apps.isEmpty());
        this.apps = apps;
    }

    public List<App> getApps() {
        return apps;
    }

    public Type<CopyAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(CopyAppSelectedHandler handler) {
        handler.onCopyAppSelected(this);
    }
}
