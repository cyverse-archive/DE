package org.iplantc.de.diskResource.client.events.search;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.events.search.DeleteSavedSearchClickedEvent.DeleteSavedSearchEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class DeleteSavedSearchClickedEvent extends GwtEvent<DeleteSavedSearchEventHandler> {

    public interface DeleteSavedSearchEventHandler extends EventHandler {
        void onDeleteSavedSearchClicked(DeleteSavedSearchClickedEvent deleteSavedSearchClickedEvent);
    }

    public static interface HasDeleteSavedSearchClickedEventHandlers {
        HandlerRegistration addDeleteSavedSearchClickedEventHandler(DeleteSavedSearchEventHandler handler);
    }

    public static final GwtEvent.Type<DeleteSavedSearchEventHandler> TYPE = new GwtEvent.Type<>();
    private final DiskResourceQueryTemplate savedSearch;

    public DeleteSavedSearchClickedEvent(DiskResourceQueryTemplate savedSearch) {
        this.savedSearch = savedSearch;
    }

    @Override
    public GwtEvent.Type<DeleteSavedSearchEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResourceQueryTemplate getSavedSearch() {
        return savedSearch;
    }

    @Override
    protected void dispatch(DeleteSavedSearchEventHandler handler) {
        handler.onDeleteSavedSearchClicked(this);
    }
}
