package org.iplantc.de.diskResource.client.search.events;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.search.events.SaveDiskResourceQueryEvent.SaveDiskResourceQueryEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SaveDiskResourceQueryEvent extends GwtEvent<SaveDiskResourceQueryEventHandler> {

    public interface SaveDiskResourceQueryEventHandler extends EventHandler {
        void doSaveDiskResourceQueryTemplate(SaveDiskResourceQueryEvent event);
    }

    public static interface HasSaveDiskResourceQueryEventHandlers {
        HandlerRegistration addSaveDiskResourceQueryEventHandler(SaveDiskResourceQueryEventHandler handler);
    }

    public static final GwtEvent.Type<SaveDiskResourceQueryEventHandler> TYPE = new GwtEvent.Type<SaveDiskResourceQueryEventHandler>();
    private final DiskResourceQueryTemplate queryTemplate;
    private final String originalName;

    public SaveDiskResourceQueryEvent(final DiskResourceQueryTemplate queryTemplate, final String originalName) {
        this.queryTemplate = queryTemplate;
        this.originalName = originalName;
    }

    @Override
    public GwtEvent.Type<SaveDiskResourceQueryEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResourceQueryTemplate getQueryTemplate() {
        return queryTemplate;
    }

    @Override
    protected void dispatch(SaveDiskResourceQueryEventHandler handler) {
        handler.doSaveDiskResourceQueryTemplate(this);
    }

    public String getOriginalName() {
        return originalName;
    }

}
