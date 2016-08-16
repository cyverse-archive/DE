package org.iplantc.de.admin.desktop.client.workshopAdmin.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import org.iplantc.de.client.models.groups.Member;

/**
 * @author dennis
 */
public class MemberSelectedEvent extends GwtEvent<MemberSelectedEvent.MemberSelectedEventHandler> {
    public static Type<MemberSelectedEventHandler> TYPE = new Type<>();
    private final Member member;

    public Member getMember() {
        return member;
    }

    public MemberSelectedEvent(Member member) {
        this.member = member;
    }

    @Override
    public Type<MemberSelectedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MemberSelectedEventHandler handler) {
        handler.onMemberSelected(this);
    }

    public interface MemberSelectedEventHandler extends EventHandler {
        void onMemberSelected(MemberSelectedEvent event);
    }

    public interface HasMemberSelectedEventHandlers {
        HandlerRegistration addMemberSelectedEventHandler(MemberSelectedEventHandler handler);
    }
}
