package org.iplantc.de.apps.client.events.selection;

/**
 * @author sriram
 */
import org.iplantc.de.client.models.apps.App;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import java.util.List;

public class ShareAppsSelected extends
                                   GwtEvent<ShareAppsSelected.ShareAppsSelectedHandler> {

    public static interface ShareAppsSelectedHandler extends EventHandler {
        void onShareAppSelected(ShareAppsSelected event);
    }

    public static interface HasShareAppSelectedHandlers {
        HandlerRegistration addShareAppSelectedHandler(ShareAppsSelectedHandler handler);
    }

    public static final Type<ShareAppsSelectedHandler> TYPE = new Type<>();
    private final List<App> selectedApps;

    public ShareAppsSelected(List<App> selectedApps) {
        this.selectedApps = selectedApps;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ShareAppsSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShareAppsSelectedHandler handler) {
        handler.onShareAppSelected(this);
    }

    public List<App> getSelectedApps() {
        return selectedApps;
    }

}
