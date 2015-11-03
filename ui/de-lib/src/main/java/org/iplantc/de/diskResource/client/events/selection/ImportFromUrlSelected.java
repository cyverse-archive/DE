package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by jstroot on 3/23/15.
 *
 * @author jstroot
 */
public class ImportFromUrlSelected extends GwtEvent<ImportFromUrlSelected.ImportFromUrlSelectedHandler> {
    public static interface HasImportFromUrlSelectedHandlers {
        HandlerRegistration addImportFromUrlSelectedHandler(ImportFromUrlSelectedHandler handler);
    }

    public static interface ImportFromUrlSelectedHandler extends EventHandler {
        void onImportFromUrlSelected(ImportFromUrlSelected event);
    }
    public static Type<ImportFromUrlSelectedHandler> TYPE = new Type<>();
    private final Folder selectedFolder;

    public ImportFromUrlSelected(final Folder selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    public Type<ImportFromUrlSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    protected void dispatch(ImportFromUrlSelectedHandler handler) {
        handler.onImportFromUrlSelected(this);
    }
}
