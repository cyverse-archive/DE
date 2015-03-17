package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 2/2/15.
 *
 * @author jstroot
 */
public class SimpleDownloadSelected extends GwtEvent<SimpleDownloadSelected.SimpleDownloadSelectedHandler> {
    public static interface SimpleDownloadSelectedHandler extends EventHandler {
        void onSimpleDownloadSelected(SimpleDownloadSelected event);
    }

    public static interface HasSimpleDownloadSelectedHandlers {
        HandlerRegistration addSimpleDownloadSelectedHandler(SimpleDownloadSelectedHandler handler);
    }

    public static final Type<SimpleDownloadSelectedHandler> TYPE = new Type<>();
    private final Folder selectedFolder;
    private final List<DiskResource> selectedDiskResources;

    public SimpleDownloadSelected(final Folder selectedFolder,
                                  final List<DiskResource> selectedDiskResources) {
        this.selectedFolder = selectedFolder;
        this.selectedDiskResources = selectedDiskResources;
    }

    public Type<SimpleDownloadSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getSelectedDiskResources() {
        return selectedDiskResources;
    }

    public Folder getSelectedFolder() {
        return selectedFolder;
    }

    protected void dispatch(SimpleDownloadSelectedHandler handler) {
        handler.onSimpleDownloadSelected(this);
    }
}
