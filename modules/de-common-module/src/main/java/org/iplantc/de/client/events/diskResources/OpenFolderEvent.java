package org.iplantc.de.client.events.diskResources;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class OpenFolderEvent extends GwtEvent<OpenFolderEvent.OpenFolderEventHandler> {
    public interface OpenFolderEventHandler extends EventHandler {
        void onRequestOpenFolder(OpenFolderEvent event);
    }

    public static final Type<OpenFolderEventHandler> TYPE = new Type<OpenFolderEventHandler>();
    private final String folderId;

    public OpenFolderEvent(String folderId) {
        this.folderId = folderId;
    }

    @Override
    public Type<OpenFolderEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getFolderId() {
        return folderId;
    }

    @Override
    protected void dispatch(OpenFolderEventHandler handler) {
        handler.onRequestOpenFolder(this);
    }
}
