package org.iplantc.de.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/5/15.
 *
 * @author jstroot
 */
public class EditAppSelected extends GwtEvent<EditAppSelected.EditAppSelectedHandler> {
    public static interface EditAppSelectedHandler extends EventHandler {
        void onEditAppSelected(EditAppSelected event);
    }

    public static interface HasEditAppSelectedHandlers {
        HandlerRegistration addEditAppSelectedHandler(EditAppSelectedHandler handler);
    }

    public static final Type<EditAppSelectedHandler> TYPE = new Type<>();
    private final App app;

    public EditAppSelected(final App app) {
        Preconditions.checkNotNull(app);
        this.app = app;
    }

    public App getApp() {
        return app;
    }

    public Type<EditAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EditAppSelectedHandler handler) {
        handler.onEditAppSelected(this);
    }
}
