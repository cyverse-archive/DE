package org.iplantc.de.diskResource.client.events.search;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.diskResource.client.events.search.UpdateSavedSearchesEvent.UpdateSavedSearchesHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author psarando, jstroot
 * 
 */
public class UpdateSavedSearchesEvent extends GwtEvent<UpdateSavedSearchesHandler> {

    public interface UpdateSavedSearchesHandler extends EventHandler {
        void onUpdateSavedSearches(UpdateSavedSearchesEvent event);
    }

    public static interface HasUpdateSavedSearchesEventHandlers {
        HandlerRegistration addUpdateSavedSearchesEventHandler(UpdateSavedSearchesHandler handler);
    }

    public static final GwtEvent.Type<UpdateSavedSearchesHandler> TYPE = new GwtEvent.Type<>();
    private final List<DiskResourceQueryTemplate> savedSearches;
    private final List<DiskResourceQueryTemplate> removedSearches;

    public UpdateSavedSearchesEvent(List<DiskResourceQueryTemplate> savedSearches,
            List<DiskResourceQueryTemplate> removedSearches) {
        this.savedSearches = savedSearches;
        this.removedSearches = removedSearches;
    }

    @Override
    public GwtEvent.Type<UpdateSavedSearchesHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResourceQueryTemplate> getSavedSearches() {
        return savedSearches;
    }

    public List<DiskResourceQueryTemplate> getRemovedSearches() {
        return removedSearches;
    }

    @Override
    protected void dispatch(UpdateSavedSearchesHandler handler) {
        handler.onUpdateSavedSearches(this);
    }
}
