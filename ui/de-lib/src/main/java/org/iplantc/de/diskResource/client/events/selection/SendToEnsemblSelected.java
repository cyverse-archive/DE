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
public class SendToEnsemblSelected extends GwtEvent<SendToEnsemblSelected.SendToEnsemblSelectedHandler> {
    public static interface SendToEnsemblSelectedHandler extends EventHandler {
        void onSendToEnsemblSelected(SendToEnsemblSelected event);
    }

    public static interface HasSendToEnsemblSelectedHandlers {
        HandlerRegistration addSendToEnsemblSelectedHandler(SendToEnsemblSelectedHandler handler);
    }

    public static final Type<SendToEnsemblSelectedHandler> TYPE = new Type<>();
    private final List<DiskResource> selectedDiskResources;

    public SendToEnsemblSelected(List<DiskResource> selectedDiskResources) {
        this.selectedDiskResources = selectedDiskResources;
    }


    public Type<SendToEnsemblSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResource> getResourcesToSend() {
        return selectedDiskResources;
    }

    protected void dispatch(SendToEnsemblSelectedHandler handler) {
        handler.onSendToEnsemblSelected(this);
    }
}
