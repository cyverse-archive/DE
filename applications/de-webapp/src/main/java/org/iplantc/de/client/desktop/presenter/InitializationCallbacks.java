package org.iplantc.de.client.desktop.presenter;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.desktop.DesktopView;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.http.client.Request;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.ListStore;

import java.util.List;
import java.util.Map;

/**
 * This class contains the callbacks used by the {@link DesktopPresenterImpl} during
 * DE initialization.
 */
class InitializationCallbacks {
    private static class BootstrapCallback implements AsyncCallback<String> {
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final IplantErrorStrings errorStrings;
        private final UserInfo userInfo;

        public BootstrapCallback(UserInfo userInfo,
                                 Provider<ErrorHandler> errorHandlerProvider,
                                 IplantErrorStrings errorStrings) {

            this.userInfo = userInfo;
            this.errorHandlerProvider = errorHandlerProvider;
            this.errorStrings = errorStrings;
        }

        @Override
        public void onFailure(Throwable caught) {
            errorHandlerProvider.get().post(errorStrings.systemInitializationError(), caught);
        }

        @Override
        public void onSuccess(String result) {
            userInfo.init(result);
        }
    }

    static class GetInitialNotificationsCallback implements AsyncCallback<List<Notification>> {
        private final DesktopView view;
        private final IplantAnnouncer announcer;

        public GetInitialNotificationsCallback(final DesktopView view,
                                                final IplantAnnouncer announcer) {
            this.view = view;
            this.announcer = announcer;
        }

        @Override
        public void onFailure(Throwable caught) {
            announcer.schedule(new ErrorAnnouncementConfig("There was a problem fetching your current notifications", true, 3000));
        }

        @Override
        public void onSuccess(List<Notification> result) {
            ListStore<NotificationMessage> store = view.getNotificationStore();
            for(Notification n : result) {
                store.add(n.getMessage());
            }
        }
    }

    static class PropertyServiceCallback implements AsyncCallback<Map<String, String>> {
        private final DEProperties deProps;
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final IplantErrorStrings errorStrings;
        private final Panel panel;
        private final DesktopPresenterImpl presenter;
        private final UserInfo userInfo;
        private final UserSessionServiceFacade userSessionService;
        private final UserSettings userSettings;

        public PropertyServiceCallback(DEProperties deProperties,
                                       UserInfo userInfo,
                                       UserSettings userSettings,
                                       UserSessionServiceFacade userSessionService,
                                       Provider<ErrorHandler> errorHandlerProvider,
                                       IplantErrorStrings errorStrings,
                                       Panel panel,
                                       DesktopPresenterImpl presenter) {
            this.deProps = deProperties;
            this.userInfo = userInfo;
            this.userSettings = userSettings;
            this.userSessionService = userSessionService;
            this.errorHandlerProvider = errorHandlerProvider;
            this.errorStrings = errorStrings;
            this.panel = panel;
            this.presenter = presenter;
        }

        @Override
        public void onFailure(Throwable caught) {
            errorHandlerProvider.get().post(errorStrings.systemInitializationError(), caught);
        }

        @Override
        public void onSuccess(Map<String, String> result) {
            deProps.initialize(result);
            final Request bootstrapReq = userSessionService.bootstrap(new BootstrapCallback(userInfo,
                                                                                            errorHandlerProvider,
                                                                                            errorStrings));
            final Request userPrefReq = userSessionService.getUserPreferences(new UserPreferencesCallback(userSettings,
                                                                                                          errorHandlerProvider,
                                                                                                          errorStrings));

            Timer t = new Timer() {
                @Override
                public void run() {
                    if (!bootstrapReq.isPending() && !userPrefReq.isPending()) {
                        // Cancel timer
                        cancel();
                        presenter.postBootstrap(panel);
                    }
                }
            };
            t.scheduleRepeating(1);
        }
    }

    private static class UserPreferencesCallback implements AsyncCallback<String> {
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final IplantErrorStrings errorStrings;
        private final UserSettings userSettings;

        public UserPreferencesCallback(UserSettings userSettings,
                                       Provider<ErrorHandler> errorHandlerProvider,
                                       IplantErrorStrings errorStrings) {
            this.userSettings = userSettings;
            this.errorHandlerProvider = errorHandlerProvider;
            this.errorStrings = errorStrings;
        }

        @Override
        public void onFailure(Throwable caught) {
            errorHandlerProvider.get().post(errorStrings.systemInitializationError(), caught);
        }

        @Override
        public void onSuccess(String result) {
            userSettings.setValues(StringQuoter.split(result));
        }
    }
}
