package org.iplantc.de.diskResource.client.events.search;

import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author jstroot
 */
public class SavedSearchDeletedEvent extends GwtEvent<SavedSearchDeletedEvent.SavedSearchDeletedEventHandler> {
    public static interface SavedSearchDeletedEventHandler extends EventHandler {
        void onSavedSearchDeleted(SavedSearchDeletedEvent event);
    }

    public static interface HasSavedSearchDeletedEventHandlers {
        HandlerRegistration addSavedSearchDeletedEventHandler(SavedSearchDeletedEventHandler handler);
    }

    public static final Type<SavedSearchDeletedEventHandler> TYPE = new Type<>();
    private final DiskResourceQueryTemplate savedSearch;

    public SavedSearchDeletedEvent(DiskResourceQueryTemplate savedSearch) {
        this.savedSearch = savedSearch;
    }

    public Type<SavedSearchDeletedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public DiskResourceQueryTemplate getSavedSearch() {
        return savedSearch;
    }

    protected void dispatch(SavedSearchDeletedEventHandler handler) {
        handler.onSavedSearchDeleted(this);
    }
}
