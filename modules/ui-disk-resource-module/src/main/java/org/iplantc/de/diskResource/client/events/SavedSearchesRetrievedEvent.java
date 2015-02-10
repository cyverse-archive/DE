package org.iplantc.de.diskResource.client.events;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;

import com.google.common.base.Preconditions;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

/**
 * @author jstroot
 */
public class SavedSearchesRetrievedEvent extends GwtEvent<SavedSearchesRetrievedEvent.SavedSearchesRetrievedEventHandler> {
    public static interface HasSavedSearchesRetrievedEventHandlers {
        HandlerRegistration addSavedSearchedRetrievedEventHandler(SavedSearchesRetrievedEventHandler handler);
    }

    public static interface SavedSearchesRetrievedEventHandler extends EventHandler {
        void onSavedSearchedRetrieved(SavedSearchesRetrievedEvent event);
    }

    public static final Type<SavedSearchesRetrievedEventHandler> TYPE = new Type<>();
    private final List<DiskResourceQueryTemplate> savedSearches;

    public SavedSearchesRetrievedEvent(List<DiskResourceQueryTemplate> savedSearches) {
        Preconditions.checkNotNull(savedSearches);
        this.savedSearches = savedSearches;
    }

    public Type<SavedSearchesRetrievedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<DiskResourceQueryTemplate> getSavedSearches() {
        return savedSearches;
    }

    protected void dispatch(SavedSearchesRetrievedEventHandler handler) {
        handler.onSavedSearchedRetrieved(this);
    }
}
