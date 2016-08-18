package org.iplantc.de.admin.desktop.client.workshopAdmin.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.iplantc.de.client.models.groups.Member;

import java.util.List;

/**
 * @author dennis
 */
public class DeleteMembersClickedEvent extends GwtEvent<DeleteMembersClickedEvent.DeleteMembersClickedEventHandler> {
    public static GwtEvent.Type<DeleteMembersClickedEventHandler> TYPE = new GwtEvent.Type<>();
    private final List<Member> members;

    public List<Member> getMembers() {
        return members;
    }

    public DeleteMembersClickedEvent(List<Member> members) {
        this.members = members;
    }

    @Override
    public GwtEvent.Type<DeleteMembersClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(DeleteMembersClickedEventHandler handler) {
        handler.onDeleteMembersClicked(this);
    }

    public interface DeleteMembersClickedEventHandler extends EventHandler {
        void onDeleteMembersClicked(DeleteMembersClickedEvent event);
    }
}
