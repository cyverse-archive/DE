package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.PreferencesUpdatedEvent;
import org.iplantc.de.client.events.SystemMessageCountUpdateEvent;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEvent;
import org.iplantc.de.commons.client.events.UserSettingsUpdatedEventHandler;
import org.iplantc.de.diskResource.client.events.FileUploadedEvent;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.inject.Inject;

import java.util.List;

/**
 * Performs all global non-window event bus handling for the desktop presenter. Event handlers are
 * not registered until the presenter is set.
 *
 * @author jstroot
 */
public class DesktopPresenterEventHandler implements PreferencesUpdatedEvent.PreferencesUpdatedEventHandler,
                                                     SystemMessageCountUpdateEvent.Handler,
                                                     UserSettingsUpdatedEventHandler,
                                                     FileUploadedEvent.FileUploadedEventHandler {

    @Inject EventBus eventBus;

    private NewDesktopPresenterImpl presenter;

    @Inject
    public DesktopPresenterEventHandler() {
    }

    @Override
    public void onCountUpdate(SystemMessageCountUpdateEvent event) {

    }

    @Override
    public void onFileUploaded(FileUploadedEvent event) {

    }

    @Override
    public void onUpdate(PreferencesUpdatedEvent event) {

    }

    @Override
    public void onUpdate(UserSettingsUpdatedEvent usue) {

    }

    public void setPresenter(NewDesktopPresenterImpl presenter) {
        this.presenter = presenter;
        init(eventBus);
    }

    private void init(EventBus eventBus) {
        List<HandlerRegistration> handlerRegistrations = Lists.newArrayList();
        HandlerRegistration handlerRegistration = eventBus.addHandler(PreferencesUpdatedEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(SystemMessageCountUpdateEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(UserSettingsUpdatedEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
        handlerRegistration = eventBus.addHandler(FileUploadedEvent.TYPE, this);
        handlerRegistrations.add(handlerRegistration);
    }
}
