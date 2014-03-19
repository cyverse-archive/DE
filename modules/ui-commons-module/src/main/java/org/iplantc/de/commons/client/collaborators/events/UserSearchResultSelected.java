package org.iplantc.de.commons.client.collaborators.events;

import org.iplantc.de.client.models.collaborators.Collaborator;
import org.iplantc.de.commons.client.collaborators.events.UserSearchResultSelected.UserSearchResultSelectedEventHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * An event that is fired when user selects a user from user search field
 * 
 * @author sriram,Paul
 * 
 */
public class UserSearchResultSelected extends GwtEvent<UserSearchResultSelectedEventHandler> {

    public enum USER_SEARCH_EVENT_TAG {

        SHARING, MANAGE

    };

    public interface UserSearchResultSelectedEventHandler extends EventHandler {

        void onUserSearchResultSelected(UserSearchResultSelected userSearchResultSelected);
    }

    public static final GwtEvent.Type<UserSearchResultSelectedEventHandler> TYPE = new GwtEvent.Type<UserSearchResultSelected.UserSearchResultSelectedEventHandler>();
    private final Collaborator collaborator;
    private String tag;

    public UserSearchResultSelected(String tag, Collaborator collaborator) {
        this.collaborator = collaborator;
        this.setTag(tag);
    }

    @Override
    public GwtEvent.Type<UserSearchResultSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UserSearchResultSelectedEventHandler handler) {
        handler.onUserSearchResultSelected(this);
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @param tag the tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

}