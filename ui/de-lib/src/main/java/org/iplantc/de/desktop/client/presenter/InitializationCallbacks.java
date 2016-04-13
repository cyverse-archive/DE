package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationList;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.desktop.client.DesktopView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class contains the callbacks used by the {@link DesktopPresenterImpl} during DE initialization.
 * @author jstroot
 */
class InitializationCallbacks {
    private static class BootstrapCallback implements AsyncCallback<String> {
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;
        private final UserInfo userInfo;
        private final DesktopPresenterImpl presenter;
        private final UserSessionServiceFacade userSessionService;
        private final UserPreferencesCallback userPreferencesCallback;
        Logger LOG = Logger.getLogger(BootstrapCallback.class.getName());

        public BootstrapCallback(DesktopPresenterImpl presenter,
                                 UserInfo userInfo,
                                 Provider<ErrorHandler> errorHandlerProvider,
                                 DesktopView.Presenter.DesktopPresenterAppearance appearance,
                                 UserSessionServiceFacade userSessionService,
                                 UserPreferencesCallback userPreferencesCallback) {

            this.presenter = presenter;
            this.userInfo = userInfo;
            this.errorHandlerProvider = errorHandlerProvider;
            this.appearance = appearance;
            this.userSessionService = userSessionService;
            this.userPreferencesCallback = userPreferencesCallback;
        }

        @Override
        public void onFailure(Throwable caught) {
            errorHandlerProvider.get().post(appearance.systemInitializationError(), caught);
        }

        @Override
        public void onSuccess(String result) {
            userInfo.init(result);
            if (userInfo.isNewUser()) {
                ConfirmMessageBox box = new ConfirmMessageBox(appearance.welcome(), appearance.introWelcome());
                box.addDialogHideHandler(new DialogHideHandler() {

                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if (event.getHideButton().toString().equalsIgnoreCase("yes")) {
                            LOG.fine("new user tour");
                            presenter.onIntroClick();
                        }

                    }
                });
                box.show();
            }
            userSessionService.getUserPreferences(userPreferencesCallback);
        }
    }

    static class GetInitialNotificationsCallback implements AsyncCallback<NotificationList> {
        private final DesktopView view;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;
        private final IplantAnnouncer announcer;

        public GetInitialNotificationsCallback(final DesktopView view,
                                               final DesktopView.Presenter.DesktopPresenterAppearance appearance,
                                               final IplantAnnouncer announcer) {
            this.view = view;
            this.appearance = appearance;
            this.announcer = announcer;
        }

        @Override
        public void onFailure(Throwable caught) {
            announcer.schedule(new ErrorAnnouncementConfig(appearance.fetchNotificationsError(),
                                                           true,
                                                           3000));
        }

        @Override
        public void onSuccess(NotificationList result) {
            if(result != null) {
                GWT.log("unseen count ^^^^^^" + result.getUnseenTotal());
                view.setUnseenNotificationCount(Integer.parseInt(result.getUnseenTotal()));
            }
            ListStore<NotificationMessage> store = view.getNotificationStore();
            for (Notification n : result.getNotifications()) {
                store.add(n.getMessage());
            }
        }
    }

    static class PropertyServiceCallback implements AsyncCallback<HashMap<String, String>> {
        private final DEProperties deProps;
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;
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
                                       DesktopView.Presenter.DesktopPresenterAppearance appearance,
                                       Panel panel,
                                       DesktopPresenterImpl presenter) {
            this.deProps = deProperties;
            this.userInfo = userInfo;
            this.userSettings = userSettings;
            this.userSessionService = userSessionService;
            this.errorHandlerProvider = errorHandlerProvider;
            this.appearance = appearance;
            this.panel = panel;
            this.presenter = presenter;
        }

        @Override
        public void onFailure(Throwable caught) {
            errorHandlerProvider.get().post(appearance.systemInitializationError(), caught);
        }

        @Override
        public void onSuccess(HashMap<String, String> result) {
            deProps.initialize(result);
            final UserPreferencesCallback userPreferencesCallback = new UserPreferencesCallback(presenter,
                                                                                                panel,
                                                                                                userSettings,
                                                                                                errorHandlerProvider,
                                                                                                appearance);
            userSessionService.bootstrap(new BootstrapCallback(presenter,
                                                               userInfo,
                                                               errorHandlerProvider,
                                                               appearance,
                                                               userSessionService,
                                                               userPreferencesCallback));
        }
    }

    private static class UserPreferencesCallback implements AsyncCallback<String> {
        private final DesktopPresenterImpl presenter;
        private final Panel panel;
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final DesktopView.Presenter.DesktopPresenterAppearance appearance;
        private final UserSettings userSettings;

        public UserPreferencesCallback(DesktopPresenterImpl presenter,
                                       Panel panel,
                                       UserSettings userSettings,
                                       Provider<ErrorHandler> errorHandlerProvider,
                                       DesktopView.Presenter.DesktopPresenterAppearance appearance) {
            this.presenter = presenter;
            this.panel = panel;
            this.userSettings = userSettings;
            this.errorHandlerProvider = errorHandlerProvider;
            this.appearance = appearance;
        }

        @Override
        public void onFailure(Throwable caught) {
            errorHandlerProvider.get().post(appearance.systemInitializationError(), caught);
        }

        @Override
        public void onSuccess(String result) {
            userSettings.setValues(StringQuoter.split(result));
            presenter.postBootstrap(panel);
        }
    }
}
