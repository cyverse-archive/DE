package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.diskResource.client.events.FileUploadedEvent.FileUploadedEventHandler;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author jstroot
 */
public class FileUploadedEvent extends GwtEvent<FileUploadedEventHandler> {

    public interface FileUploadedEventHandler extends EventHandler {
        void onFileUploaded(FileUploadedEvent event);
    }

    public static final GwtEvent.Type<FileUploadedEventHandler> TYPE = new GwtEvent.Type<>();
    private final HasPath uploadDest;
    private final String filePath;
    private final String response;

    public FileUploadedEvent(final HasPath uploadDest,
                             final String filePath,
                             final String response ) {
        Preconditions.checkNotNull(uploadDest);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(uploadDest.getPath()));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(filePath));
        this.uploadDest = uploadDest;
        this.filePath = filePath;
        this.response = response;
    }

    @Override
    public GwtEvent.Type<FileUploadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FileUploadedEventHandler handler) {
        handler.onFileUploaded(this);
    }

    public HasPath getUploadDestFolder() {
        return uploadDest;
    }

    /**
     * @return the filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

}
