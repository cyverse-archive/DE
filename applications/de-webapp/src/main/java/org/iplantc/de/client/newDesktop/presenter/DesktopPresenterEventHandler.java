package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.events.DefaultUploadCompleteHandler;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.NotificationCountUpdateEvent;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEvent;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEventHandler;
import org.iplantc.de.diskResource.client.events.FileUploadedEvent;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONObject;
import com.google.inject.Inject;

import java.util.List;

/**
 * Performs all global non-window event bus handling for the desktop presenter. Event handlers are
 * not registered until the presenter is set.
 *
 * @author jstroot
 */
public class DesktopPresenterEventHandler implements UserSettingsUpdatedEventHandler,
                                                     NotificationCountUpdateEvent.NotificationCountUpdateEventHandler,
                                                     FileUploadedEvent.FileUploadedEventHandler {

    @Inject EventBus eventBus;
    private final UserSettings userSettings;

    private NewDesktopPresenterImpl presenter;
    private NewDesktopView view;

    @Inject
    public DesktopPresenterEventHandler(final UserSettings userSettings) {
        this.userSettings = userSettings;
    }

    @Override
    public void onCountUpdate(NotificationCountUpdateEvent ncue) {
        view.setUnseenNotificationCount(ncue.getTotal());
    }

    @Override
    public void onFileUploaded(FileUploadedEvent event) {

        DefaultUploadCompleteHandler duc = new DefaultUploadCompleteHandler(event.getUploadDestFolderFolder().toString());
        JSONObject obj = JsonUtil.getObject(event.getResponse());
        String fileJson = JsonUtil.getObject(obj, "file").toString();
        duc.onCompletion(event.getFilepath(), fileJson);
        // FIXME JDS refactor this and roll user notification posting into presenter
        // FIXME JDS Have diskResource presenter listen to this and perform refresh

    }

    @Override
    public void onUpdate(UserSettingsUpdatedEvent usue) {
        presenter.saveUserSettings(userSettings);
        /* FIXME JDS Change this to "lastPathUpdated" or similar
         *           This event is ONLY used to update the last path from file selector fields, etc
         *           It would be more declarative to fire an event which communicates that
         *           "last path has been updated".
         */
    }

    public void setPresenter(NewDesktopPresenterImpl presenter, NewDesktopView view) {
        this.presenter = presenter;
        this.view = view;
        init(eventBus);
    }

    private void init(EventBus eventBus) {
        List<HandlerRegistration> handlerRegistrations = Lists.newArrayList();
        HandlerRegistration handlerRegistration = eventBus.addHandler(UserSettingsUpdatedEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(FileUploadedEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(NotificationCountUpdateEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
    }
}
