package org.iplantc.de.diskResource.client.events.search;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class SaveDiskResourceQueryClickedEvent extends GwtEvent<SaveDiskResourceQueryClickedEvent.SaveDiskResourceQueryClickedEventHandler> {

    public interface SaveDiskResourceQueryClickedEventHandler extends EventHandler {
        void onSaveDiskResourceQueryClicked(SaveDiskResourceQueryClickedEvent event);
    }

    public static interface HasSaveDiskResourceQueryClickedEventHandlers {
        HandlerRegistration addSaveDiskResourceQueryClickedEventHandler(SaveDiskResourceQueryClickedEventHandler handler);
    }

    public static final GwtEvent.Type<SaveDiskResourceQueryClickedEventHandler> TYPE = new GwtEvent.Type<>();
    private final DiskResourceQueryTemplate queryTemplate;
    private final String originalName;

    public SaveDiskResourceQueryClickedEvent(final DiskResourceQueryTemplate queryTemplate,
                                             final String originalName) {
        this.queryTemplate = queryTemplate;
        this.originalName = originalName;
    }

    @Override
    public GwtEvent.Type<SaveDiskResourceQueryClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResourceQueryTemplate getQueryTemplate() {
        return queryTemplate;
    }

    @Override
    protected void dispatch(SaveDiskResourceQueryClickedEventHandler handler) {
        handler.onSaveDiskResourceQueryClicked(this);
    }

    public String getOriginalName() {
        return originalName;
    }

}
