package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent.RequestSimpleUploadEventHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class RequestSimpleUploadEvent extends GwtEvent<RequestSimpleUploadEventHandler> {
    public interface RequestSimpleUploadEventHandler extends EventHandler {
        void onRequestSimpleUpload(RequestSimpleUploadEvent event);
    }

    public static final GwtEvent.Type<RequestSimpleUploadEventHandler> TYPE = new GwtEvent.Type<>();
    private final Folder destinationFolder;

    public RequestSimpleUploadEvent(final Folder destinationFolder) {
        Preconditions.checkNotNull(destinationFolder);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(destinationFolder.getPath()));
        this.destinationFolder = destinationFolder;
    }

    @Override
    public GwtEvent.Type<RequestSimpleUploadEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RequestSimpleUploadEventHandler handler) {
        handler.onRequestSimpleUpload(this);
    }

    public Folder getDestinationFolder() {
        return destinationFolder;
    }

}
