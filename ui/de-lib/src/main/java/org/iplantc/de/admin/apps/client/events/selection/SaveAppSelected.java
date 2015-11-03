package org.iplantc.de.admin.apps.client.events.selection;

import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppDoc;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/17/15.
 *
 * @author jstroot
 */
public class SaveAppSelected extends GwtEvent<SaveAppSelected.SaveAppSelectedHandler> {
    public static interface HasSaveAppSelectedHandlers {
        HandlerRegistration addSaveAppSelectedHandler(SaveAppSelectedHandler handler);
    }
    public static interface SaveAppSelectedHandler extends EventHandler {
        void onSaveAppSelected(SaveAppSelected event);
    }
    public static Type<SaveAppSelectedHandler> TYPE = new Type<>();
    private final App app;
    private final AppDoc doc;

    public SaveAppSelected(final App app,
                           final AppDoc doc) {
        Preconditions.checkNotNull(app);
        Preconditions.checkNotNull(doc);
        this.app = app;
        this.doc = doc;
    }

    public App getApp() {
        return app;
    }

    public Type<SaveAppSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public AppDoc getDoc() {
        return doc;
    }

    protected void dispatch(SaveAppSelectedHandler handler) {
        handler.onSaveAppSelected(this);
    }
}
