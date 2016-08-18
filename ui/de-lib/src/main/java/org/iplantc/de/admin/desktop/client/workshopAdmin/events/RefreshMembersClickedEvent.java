package org.iplantc.de.admin.desktop.client.workshopAdmin.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * @author dennis
 */
public class RefreshMembersClickedEvent
        extends GwtEvent<RefreshMembersClickedEvent.RefreshMembersClickedEventHandler> {

    public static Type<RefreshMembersClickedEventHandler> TYPE = new Type<>();

    public interface RefreshMembersClickedEventHandler extends EventHandler {
        void onRefreshMembersClicked(RefreshMembersClickedEvent event);
    }

    @Override
    public Type<RefreshMembersClickedEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshMembersClickedEventHandler handler) {
        handler.onRefreshMembersClicked(this);
    }
}
