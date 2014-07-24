package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadAnalysis;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.utils.NotifyInfo;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;

import java.util.List;

/**
 * This class contains all runtime callbacks used by the {@link NewDesktopPresenterImpl}
 */
class RuntimeCallbacks {
    static class GetRecentNotificationsCallback implements AsyncCallback<List<Notification>> {
        private static final int NEW_NOTIFICATION_LIMIT = 10;
        private final IplantDisplayStrings displayStrings;
        private final NotificationAutoBeanFactory notificationFactory;
        private final NewDesktopView view;

        public GetRecentNotificationsCallback(final IplantDisplayStrings displayStrings,
                                       final NotificationAutoBeanFactory notificationFactory,
                                       final NewDesktopView view) {
            this.displayStrings = displayStrings;
            this.notificationFactory = notificationFactory;
            this.view = view;
        }

        @Override
        public void onFailure(Throwable caught) {

        }

        @Override
        public void onSuccess(List<Notification> result) {

            // this callback shouldn't care if it has been called once or many times
            ListStore<NotificationMessage> nmStore = view.getNotificationStore();
            int numNewMessageNotifications = 0;

            for (Notification n : result) {
                NotificationMessage newMessage = n.getMessage();
                newMessage.setSeen(n.isSeen());

                final NotificationMessage modelWithKey = nmStore.findModelWithKey(Long.toString(newMessage.getTimestamp()));
                if (modelWithKey == null) {
                    nmStore.add(newMessage);
                    numNewMessageNotifications++;
                    if (!newMessage.isSeen()
                            && numNewMessageNotifications < NEW_NOTIFICATION_LIMIT) {
                        displayNotificationPopup(newMessage);
                    }
                }
            }
            if (numNewMessageNotifications > NEW_NOTIFICATION_LIMIT) {
                NotifyInfo.display(displayStrings.newNotificationsAlert());
            }
        }

        void displayNotificationPopup(NotificationMessage nm) {
            if (NotificationCategory.ANALYSIS.equals(nm.getCategory())) {
                if (NotificationCategory.ANALYSIS.equals(nm.getCategory())) {
                    PayloadAnalysis analysisPayload = AutoBeanCodex.decode(notificationFactory,
                                                                           PayloadAnalysis.class, nm.getContext()).as();

                    if ("Failed".equals(analysisPayload.getStatus())) { //$NON-NLS-1$
                        NotifyInfo.displayWarning(nm.getMessage());
                        return;
                    }
                }

                NotifyInfo.display(nm.getMessage());
            }
        }

    }

    static class GetUserSessionCallback implements AsyncCallback<List<WindowState>> {
        private final IplantAnnouncer announcer;
        private final NewDesktopPresenterImpl presenter;
        private final IplantErrorStrings errorStrings;
        private final AutoProgressMessageBox progressMessageBox;

        public GetUserSessionCallback(final AutoProgressMessageBox progressMessageBox,
                                      final IplantErrorStrings errorStrings,
                                      final IplantAnnouncer announcer,
                                      final NewDesktopPresenterImpl presenter) {
            this.progressMessageBox = progressMessageBox;
            this.errorStrings = errorStrings;
            this.announcer = announcer;
            this.presenter = presenter;
        }

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = SafeHtmlUtils.fromTrustedString(errorStrings.loadSessionFailed());
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
            presenter.doPeriodicSessionSave();
            progressMessageBox.hide();
        }

        @Override
        public void onSuccess(List<WindowState> result) {
            presenter.restoreWindows(result);
            presenter.doPeriodicSessionSave();
            progressMessageBox.hide();
        }
    }

    static class LogoutCallback implements AsyncCallback<String> {
        private final DEClientConstants constants;
        private final IplantDisplayStrings displayStrings;
        private final IplantErrorStrings errorStrings;
        private final List<WindowState> orderedWindowStates;
        private final UserSessionServiceFacade userSessionService;
        private final UserSettings userSettings;

        public LogoutCallback(final UserSessionServiceFacade userSessionService,
                               final DEClientConstants constants,
                               final UserSettings userSettings,
                               final IplantDisplayStrings displayStrings,
                               final IplantErrorStrings errorStrings,
                               final List<WindowState> orderedWindowStates) {
            this.userSessionService = userSessionService;
            this.constants = constants;
            this.userSettings = userSettings;
            this.displayStrings = displayStrings;
            this.errorStrings = errorStrings;
            this.orderedWindowStates = orderedWindowStates;
        }

        @Override
        public void onFailure(Throwable arg0) {
            GWT.log("error on logout:" + arg0.getMessage());
            // logout anyway
            logout();
        }

        @Override
        public void onSuccess(String arg0) {
            GWT.log("logout service success:" + arg0);
            logout();
        }

        private void logout() {
            final String redirectUrl = GWT.getHostPageBaseURL() + constants.logoutUrl();
            if (userSettings.isSaveSession()) {
                final AutoProgressMessageBox progressMessageBox = new AutoProgressMessageBox(displayStrings.savingSession(),
                                                                                             displayStrings.savingSessionWaitNotice());
                progressMessageBox.getProgressBar().setDuration(1000);
                progressMessageBox.getProgressBar().setInterval(100);
                progressMessageBox.auto();
                userSessionService.saveUserSession(orderedWindowStates, new AsyncCallback<Void>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        GWT.log(errorStrings.saveSessionFailed(), caught);
                        progressMessageBox.hide();
                        Window.Location.assign(redirectUrl);
                    }

                    @Override
                    public void onSuccess(Void result) {
                        progressMessageBox.hide();
                        Window.Location.assign(redirectUrl);
                    }
                });

                progressMessageBox.show();
            } else {
                Window.Location.assign(redirectUrl);
            }
        }

    }

    static class SaveUserSettingsCallback implements AsyncCallback<Void> {
        private final IplantAnnouncer announcer;
        private final IplantDisplayStrings displayStrings;
        private final UserSettings newValue;
        private final UserSettings userSettings;

        public SaveUserSettingsCallback(final UserSettings newValue,
                                        final UserSettings userSettings,
                                        final IplantAnnouncer announcer,
                                        final IplantDisplayStrings displayStrings) {
            this.newValue = newValue;
            this.userSettings = userSettings;
            this.announcer = announcer;
            this.displayStrings = displayStrings;
        }

        @Override
        public void onFailure(Throwable caught) {
            announcer.schedule(new ErrorAnnouncementConfig("Sorry about that, we were unable to save your preferences."));
        }

        @Override
        public void onSuccess(Void result) {
            userSettings.setValues(newValue.asSplittable());
            announcer.schedule(new SuccessAnnouncementConfig(displayStrings.saveSettings(), true, 3000));
        }
    }
}
