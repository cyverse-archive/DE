package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.diskResource.client.events.CreateNewFileEvent.CreateNewFileEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class CreateNewFileEvent extends GwtEvent<CreateNewFileEventHandler> {
    
    public interface CreateNewFileEventHandler extends EventHandler{
        void onCreateNewFile(CreateNewFileEvent event);
    }

    
    private String path;
    public static final GwtEvent.Type<CreateNewFileEventHandler> TYPE = new GwtEvent.Type<CreateNewFileEventHandler>();
    public CreateNewFileEvent(String path) {
        this.path = path;
    }
    
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<CreateNewFileEventHandler> getAssociatedType() {
      return TYPE;
    }

    @Override
    protected void dispatch(CreateNewFileEventHandler handler) {
        handler.onCreateNewFile(this);
        
    }
    
    public String getPath() {
        return path;
    }

}
