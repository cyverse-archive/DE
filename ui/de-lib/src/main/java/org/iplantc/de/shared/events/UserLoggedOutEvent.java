package org.iplantc.de.shared.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by sriram on 6/2/16.
 */
public class UserLoggedOutEvent extends GwtEvent<UserLoggedOutEvent.UserLoggedOutEventHandler> {

    public interface UserLoggedOutEventHandler extends EventHandler {
        void OnLoggedOut(UserLoggedOutEvent event);
    }


    public static final GwtEvent.Type<UserLoggedOutEventHandler> TYPE = new GwtEvent.Type<>();

    @Override
    public Type<UserLoggedOutEventHandler> getAssociatedType() {
        return TYPE;
    }


    @Override
    protected void dispatch(UserLoggedOutEventHandler handler) {
        handler.OnLoggedOut(this);
    }
}
