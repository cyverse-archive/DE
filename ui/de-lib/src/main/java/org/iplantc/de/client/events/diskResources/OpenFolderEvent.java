package org.iplantc.de.client.events.diskResources;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class OpenFolderEvent extends GwtEvent<OpenFolderEvent.OpenFolderEventHandler> {
    public interface OpenFolderEventHandler extends EventHandler {
        void onRequestOpenFolder(OpenFolderEvent event);
    }

    public static final Type<OpenFolderEventHandler> TYPE = new Type<OpenFolderEventHandler>();
    private final String folderPath;
    private boolean newViewRequested = false;

    public OpenFolderEvent(String folderPath, boolean requestNewView) {
        this.folderPath = folderPath;
        this.newViewRequested = requestNewView;
    }

    @Override
    public Type<OpenFolderEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getFolderPath() {
        return folderPath;
    }

    public boolean newViewRequested() {
        return newViewRequested;
    }

    @Override
    protected void dispatch(OpenFolderEventHandler handler) {
        handler.onRequestOpenFolder(this);
    }
}
