package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 2/2/15.
 *
 * @author jstroot
 */
public class SimpleUploadSelected extends GwtEvent<SimpleUploadSelected.SimpleUploadSelectedHandler> {
    public static interface SimpleUploadSelectedHandler extends EventHandler {
        void onSimpleUploadSelected(SimpleUploadSelected event);
    }

    public static interface HasSimpleUploadSelectedHandlers {
        HandlerRegistration addSimpleUploadSelectedHandler(SimpleUploadSelectedHandler handler);
    }

    public static final Type<SimpleUploadSelectedHandler> TYPE = new Type<>();
    private final Folder selectedFolder;

    public SimpleUploadSelected(final Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Type<SimpleUploadSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    protected void dispatch(SimpleUploadSelectedHandler handler) {
        handler.onSimpleUploadSelected(this);
    }
}
