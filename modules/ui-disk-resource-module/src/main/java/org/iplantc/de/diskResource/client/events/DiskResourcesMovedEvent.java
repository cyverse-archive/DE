package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.diskResource.client.events.DiskResourcesMovedEvent.DiskResourcesMovedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

import java.util.Set;

public class DiskResourcesMovedEvent extends GwtEvent<DiskResourcesMovedEventHandler> {
    
    public interface DiskResourcesMovedEventHandler extends EventHandler{

        void onDiskResourcesMoved(DiskResourcesMovedEvent event);
        
    }

    public static final GwtEvent.Type<DiskResourcesMovedEventHandler> TYPE = new GwtEvent.Type<DiskResourcesMovedEventHandler>();
    private final Folder destFolder;
    private final Set<DiskResource> resourcesToMove;
    private final Folder srcFolder;
    private final boolean moveContents;


    public DiskResourcesMovedEvent(final Folder srcFolder, final Folder destFolder, final Set<DiskResource> resourcesToMove, final boolean moveContents) {
        this.destFolder = destFolder;
        this.resourcesToMove = resourcesToMove;
        this.srcFolder = srcFolder;
        this.moveContents = moveContents;
    }

    @Override
    public GwtEvent.Type<DiskResourcesMovedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DiskResourcesMovedEventHandler handler) {
        handler.onDiskResourcesMoved(this);
    }
    
    public Folder getDestinationFolder(){
        return destFolder;
    }
    
    public Set<DiskResource> getResourcesToMove(){
        return resourcesToMove;
    }

    /**
     * @return the moveContents
     */
    public boolean isMoveContents() {
        return moveContents;
    }

    /**
     * @return the srcFolder
     */
    public Folder getSrcFolder() {
        return srcFolder;
    }

}
