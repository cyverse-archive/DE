package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.commons.client.views.window.configs.FileViewerWindowConfig;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent.CreateNewFileEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CreateNewFileEvent extends GwtEvent<CreateNewFileEventHandler> {
    
    public interface CreateNewFileEventHandler extends EventHandler{
        void onCreateNewFile(CreateNewFileEvent event);
    }

    
    public static final GwtEvent.Type<CreateNewFileEventHandler> TYPE = new GwtEvent.Type<>();
    private final Folder parentFolder;
    private FileViewerWindowConfig config;

    public CreateNewFileEvent(FileViewerWindowConfig config) {
        this.parentFolder = config.getParentFolder();
        this.config = config;
    }
    

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateNewFileEventHandler> getAssociatedType() {
      return TYPE;
    }

    public Folder getParentFolder() {
        return parentFolder;
    }

    @Override
    protected void dispatch(CreateNewFileEventHandler handler) {
        handler.onCreateNewFile(this);
        
    }

    public FileViewerWindowConfig geFileViewerWindowConfig() {
        return config;
    }

    public void setFileViewerWindowConfig(FileViewerWindowConfig config) {
        this.config = config;
    }
    
}
