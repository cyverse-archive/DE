package org.iplantc.de.diskResource.client.events.selection;

import org.iplantc.de.client.models.diskResources.DiskResource;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * Created by jstroot on 1/30/15.
 * @author jstroot
 */
public class SendToTreeViewerSelected extends GwtEvent<SendToTreeViewerSelected.SendToTreeViewerSelectedHandler> {
    public static interface SendToTreeViewerSelectedHandler extends EventHandler {
        void onSendToTreeViewerSelected(SendToTreeViewerSelected event);
    }

    public static interface HasSendToTreeViewerSelectedHandlers {
        HandlerRegistration addSendToTreeViewerSelectedHandler(SendToTreeViewerSelectedHandler handler);
    }

    public static final Type<SendToTreeViewerSelectedHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;

    public SendToTreeViewerSelected(List<DiskResource> selectedDiskResources) {
        this.selectedDiskResources = selectedDiskResources;
    }


    public Type<SendToTreeViewerSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getResourcesToSend() {
        return selectedDiskResources;
    }

    protected void dispatch(SendToTreeViewerSelectedHandler handler) {
        handler.onSendToTreeViewerSelected(this);
    }
}
