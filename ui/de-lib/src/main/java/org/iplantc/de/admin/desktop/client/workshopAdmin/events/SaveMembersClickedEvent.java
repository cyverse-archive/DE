package org.iplantc.de.admin.desktop.client.workshopAdmin.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import org.iplantc.de.client.models.groups.Member;

import java.util.List;

/**
 * @author dennis
 */
public class SaveMembersClickedEvent extends GwtEvent<SaveMembersClickedEvent.SaveMembersClickedEventHandler> {
    public static Type<SaveMembersClickedEventHandler> TYPE = new Type<>();
    private final List<Member> members;

    public List<Member> getMembers() {
        return members;
    }

    public SaveMembersClickedEvent(List<Member> members) {
        this.members = members;
    }

    @Override
    public Type<SaveMembersClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(SaveMembersClickedEventHandler handler) {
        handler.onSaveMembersClicked(this);
    }

    public interface SaveMembersClickedEventHandler extends EventHandler {
        void onSaveMembersClicked(SaveMembersClickedEvent event);
    }
}
