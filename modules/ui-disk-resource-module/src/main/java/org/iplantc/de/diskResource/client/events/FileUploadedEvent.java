package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.FileUploadedEvent.FileUploadedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class FileUploadedEvent extends GwtEvent<FileUploadedEventHandler> {

    public interface FileUploadedEventHandler extends EventHandler {
        void onFileUploaded(FileUploadedEvent event);
    }

    public static final GwtEvent.Type<FileUploadedEventHandler> TYPE = new GwtEvent.Type<FileUploadedEventHandler>();
    private final Folder uploadDest;
    private String filepath;
    private String response;

    public FileUploadedEvent(Folder uploadDest, String filepath, String response ) {
        this.uploadDest = uploadDest;
        this.setFilepath(filepath);
        this.setResponse(response);
    }

    @Override
    public GwtEvent.Type<FileUploadedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(FileUploadedEventHandler handler) {
        handler.onFileUploaded(this);
    }

    public Folder getUploadDestFolderFolder() {
        return uploadDest;
    }

    /**
     * @return the filepath
     */
    public String getFilepath() {
        return filepath;
    }

    /**
     * @param filepath the filepath to set
     */
    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }

}
