package org.iplantc.de.client.newDesktop.presenter;

import static org.iplantc.de.commons.client.collaborators.presenter.ManageCollaboratorsPresenter.MODE.MANAGE;
import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.desktop.layout.DesktopLayoutType;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.WindowCloseRequestEvent;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.newDesktop.NewDesktopView;
import org.iplantc.de.client.periodic.MessagePoller;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.sysmsgs.view.NewMessageView;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.collaborators.views.ManageCollaboratorsDialog;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.gxt3.dialogs.IplantErrorDialog;
import org.iplantc.de.commons.client.views.window.configs.AppsWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.DiskResourceWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.DeModule;
import org.iplantc.de.shared.services.PropertyServiceFacade;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NewDesktopPresenterImpl implements NewDesktopView.Presenter {

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

    private class GetUserSessionCallback implements AsyncCallback<List<WindowState>> {
        private final IplantAnnouncer announcer;
        private final IplantErrorStrings errorStrings;
        private final AutoProgressMessageBox progressMessageBox;

        public GetUserSessionCallback(final AutoProgressMessageBox progressMessageBox,
                                      final IplantErrorStrings errorStrings,
                                      final IplantAnnouncer announcer) {
            this.progressMessageBox = progressMessageBox;
            this.errorStrings = errorStrings;
            this.announcer = announcer;
        }

        @Override
        public void onFailure(Throwable caught) {
            final SafeHtml message = SafeHtmlUtils.fromTrustedString(errorStrings.loadSessionFailed());
            announcer.schedule(new ErrorAnnouncementConfig(message, true, 5000));
            doPeriodicSessionSave();
            progressMessageBox.hide();
        }

        @Override
        public void onSuccess(List<WindowState> result) {
            restoreWindows(result);
            doPeriodicSessionSave();
            progressMessageBox.hide();
        }
    }

    private static class LogoutCallback implements AsyncCallback<String> {
        private final DEClientConstants constants;
        private final IplantDisplayStrings displayStrings;
        private final IplantErrorStrings errorStrings;
        private final List<WindowState> orderedWindowStates;
        private final UserSessionServiceFacade userSessionService;
        private final UserSettings userSettings;

        private LogoutCallback(final UserSessionServiceFacade userSessionService,
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

    class PropertyServiceCallback implements AsyncCallback<Map<String, String>> {
        private final DEProperties deProps;
        private final Provider<ErrorHandler> errorHandlerProvider;
        private final IplantErrorStrings errorStrings;
        private final Panel panel;
        private final UserInfo userInfo;
        private final UserSessionServiceFacade userSessionService;
        private final UserSettings userSettings;

        public PropertyServiceCallback(DEProperties deProperties,
                                       UserInfo userInfo,
                                       UserSettings userSettings,
                                       UserSessionServiceFacade userSessionService,
                                       Provider<ErrorHandler> errorHandlerProvider,
                                       IplantErrorStrings errorStrings,
                                       Panel panel) {
            this.deProps = deProperties;
            this.userInfo = userInfo;
            this.userSettings = userSettings;
            this.userSessionService = userSessionService;
            this.errorHandlerProvider = errorHandlerProvider;
            this.errorStrings = errorStrings;
            this.panel = panel;
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
                        postBootstrap(panel);
                    }
                }
            };
            t.scheduleRepeating(1);
        }
    }

    private static class SaveUserSettingsCallback implements AsyncCallback<Void> {
        private final UserSettings newValue;
        private final UserSettings userSettings;
        private final IplantAnnouncer announcer;
        private final IplantDisplayStrings displayStrings;

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

    interface AuthErrors {
        String API_NAME = "api_name";
        String ERROR = "error";
        String ERROR_DESCRIPTION = "error_description";
    }

    interface QueryStrings {
        String APP_CATEGORY = "app-category";
        String FOLDER = "folder";
        String TYPE = "type";
    }

    interface TypeQueryValues {
        String APPS = "apps";
        String DATA = "data";
    }

    @Inject
    IplantAnnouncer announcer;
    @Inject
    CommonUiConstants commonUiConstants;
    @Inject
    DEClientConstants deClientConstants;
    @Inject
    DEProperties deProperties;
    @Inject
    IplantDisplayStrings displayStrings;
    @Inject
    Provider<ErrorHandler> errorHandlerProvider;
    @Inject
    Provider<DEFeedbackServiceFacade> feedbackServiceProvider;
    @Inject
    IplantErrorStrings errorStrings;
    @Inject
    PropertyServiceFacade propertyServiceFacade;
    @Inject
    UserInfo userInfo;
    @Inject
    UserSessionServiceFacade userSessionService;
    @Inject
    UserSettings userSettings;
    final DesktopWindowManager desktopWindowManager;
    private final EventBus eventBus;
    private final KeepaliveTimer keepaliveTimer;
    private final MessagePoller messagePoller;
    private final SaveSessionPeriodic ssp;
    private final NewMessageView.Presenter systemMsgPresenter;
    private final NewDesktopView view;
    private final WindowManager windowManager;

    @Inject
    public NewDesktopPresenterImpl(final NewDesktopView view,
                                   final DesktopPresenterEventHandler globalEventHandler,
                                   final DesktopPresenterWindowEventHandler windowEventHandler,
                                   final EventBus eventBus,
                                   final NewMessageView.Presenter systemMsgPresenter,
                                   final WindowManager windowManager,
                                   final DesktopWindowManager desktopWindowManager,
                                   final MessagePoller messagePoller,
                                   final KeepaliveTimer keepaliveTimer) {
        this.view = view;
        this.eventBus = eventBus;
        this.systemMsgPresenter = systemMsgPresenter;
        this.windowManager = windowManager;
        this.keepaliveTimer = keepaliveTimer;
        this.messagePoller = messagePoller;
        this.desktopWindowManager = desktopWindowManager;
        this.desktopWindowManager.setDesktopContainer(view.getDesktopContainer());
        this.ssp = new SaveSessionPeriodic(this);

        this.view.setPresenter(this);
        globalEventHandler.setPresenter(this);
        windowEventHandler.setPresenter(this, desktopWindowManager);
        if (DebugInfo.isDebugIdEnabled()) {
            this.view.ensureDebugId(DeModule.Ids.DESKTOP);
        }
    }

    public static native void doIntro() /*-{
        var introjs = $wnd.introJs();
        introjs.setOption("showStepNumbers", false);
        introjs.setOption("skipLabel", "Exit");
        introjs.start();
    }-*/;

    @Override
    public void doLogout() {
        // Need to stop polling
        messagePoller.stop();
//        cleanUp();

        userSessionService.logout(new LogoutCallback(userSessionService,
                                                     deClientConstants,
                                                     userSettings,
                                                     displayStrings,
                                                     errorStrings,
                                                     getOrderedWindowStates()));
    }

    @Override
    public List<WindowState> getOrderedWindowStates() {
        List<WindowState> windowStates = Lists.newArrayList();
        for (Widget w : windowManager.getStack()) {
            if (w instanceof IPlantWindowInterface) {
                windowStates.add(((IPlantWindowInterface) w).getWindowState());
            }
        }
        return Collections.unmodifiableList(windowStates);
    }

    @Override
    public void go(final Panel panel) {
        // Fetch DE properties, the rest of DE initialization is performed in callback
        propertyServiceFacade.getProperties(new PropertyServiceCallback(deProperties,
                                                                        userInfo,
                                                                        userSettings,
                                                                        userSessionService,
                                                                        errorHandlerProvider,
                                                                        errorStrings,
                                                                        panel));
    }

    @Override
    public void onAboutClick() {
        desktopWindowManager.show(WindowType.ABOUT);
    }

    @Override
    public void onAnalysesWinBtnSelect() {
        desktopWindowManager.show(WindowType.ANALYSES);
    }

    @Override
    public void onAppsWinBtnSelect() {
        desktopWindowManager.show(WindowType.APPS);
    }

    /**
     * FIXME JDS The manage collaborators presenter should be used here, not the view.
     */
    @Override
    public void onCollaboratorsClick() {
        new ManageCollaboratorsDialog(MANAGE).show();
    }

    @Override
    public void onContactSupportClick() {
        WindowUtil.open(commonUiConstants.supportUrl());
    }

    @Override
    public void onDataWinBtnSelect() {
        desktopWindowManager.show(WindowType.DATA);
    }

    @Override
    public void onDocumentationClick() {
        WindowUtil.open(deClientConstants.deHelpFile());
    }

    @Override
    public void onForumsBtnSelect() {
        WindowUtil.open(commonUiConstants.forumsUrl());
    }

    void organizeWindows(DesktopLayoutType type) {

        // TODO Implement window organizing logic

    }

    @Override
    public void saveUserSettings(final UserSettings value) {
        final SaveUserSettingsCallback callback = new SaveUserSettingsCallback(value,
                                                                               userSettings,
                                                                               announcer,
                                                                               displayStrings);
        userSessionService.saveUserPreferences(value.asSplittable(), callback);
    }

    @Override
    public void onIntroClick() {
        doIntro();
    }

    @Override
    public void onSystemMessagesClick() {
        desktopWindowManager.show(WindowType.SYSTEM_MESSAGES);
    }

    @Override
    public void show(final WindowConfig config) {
        desktopWindowManager.show(config, false);
    }

    @Override
    public void show(final WindowConfig config,
                     final boolean updateExistingWindow) {
        desktopWindowManager.show(config, updateExistingWindow);
    }

    @Override
    public void submitUserFeedback(Splittable splittable, final IsHideable isHideable) {
        StringQuoter.create(userInfo.getUsername()).assign(splittable, "username");
        StringQuoter.create(Window.Navigator.getUserAgent()).assign(splittable, "User-agent");
        feedbackServiceProvider.get().submitFeedback(splittable, new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                errorHandlerProvider.get().post(errorStrings.feedbackServiceFailure(), caught);
            }

            @Override
            public void onSuccess(Void result) {
                isHideable.hide();
                announcer.schedule(new SuccessAnnouncementConfig(displayStrings.feedbackSubmitted(), true, 3000));
            }
        });

    }

    void doPeriodicSessionSave() {
        if (userSettings.isSaveSession()) {
            ssp.run();
            messagePoller.addTask(ssp);
            // start if not started...
            messagePoller.start();
        } else {
            messagePoller.removeTask(ssp);
        }
    }

    void postBootstrap(final Panel panel) {
        setBrowserContextMenuEnabled(deProperties.isContextClickEnabled());
        // Initialize keepalive timer
        String target = deProperties.getKeepaliveTarget();
        int interval = deProperties.getKeepaliveInterval();
        if (target != null && !target.equals("") && interval > 0) {
            keepaliveTimer.start(target, interval);
        }

        initMessagePoller();
        initKBShortCuts();
        panel.add(view);
        processQueryStrings();
    }

    void restoreWindows(List<WindowState> windowStates) {
        for (WindowState ws : windowStates) {
            desktopWindowManager.show(ws);
        }
    }

    private void getUserSession(final boolean urlHasDataTypeParameter) {
        if (userSettings.isSaveSession() && !urlHasDataTypeParameter) {
            // This restoreSession's callback will also init periodic session saving.
            final AutoProgressMessageBox progressMessageBox = new AutoProgressMessageBox(displayStrings.loadingSession(),
                                                                                         displayStrings.loadingSessionWaitNotice());
            progressMessageBox.getProgressBar().setDuration(1000);
            progressMessageBox.getProgressBar().setInterval(100);
            progressMessageBox.auto();
            final Request req = userSessionService.getUserSession(new GetUserSessionCallback(progressMessageBox,
                                                                                             errorStrings,
                                                                                             announcer));
            progressMessageBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (Dialog.PredefinedButton.CANCEL.equals(event.getHideButton())) {
                        req.cancel();
                        SafeHtml msg = SafeHtmlUtils.fromString("Session restore cancelled");
                        announcer.schedule(new SuccessAnnouncementConfig(msg, true, 5000));
                    }
                }
            });
        } else if (urlHasDataTypeParameter) {
            doPeriodicSessionSave();
        }
    }

    private void initKBShortCuts() {
        new KeyNav(RootPanel.get()) {
            @Override
            public void handleEvent(NativeEvent event) {
                if (event.getCtrlKey() && event.getShiftKey()) {
                    final String keycode = String.valueOf((char) event.getKeyCode());
                    if (userSettings.getDataShortCut().equals(keycode)) {
                        show(ConfigFactory.diskResourceWindowConfig(true));
                    } else if (userSettings.getAnalysesShortCut().equals(keycode)) {
                        show(ConfigFactory.analysisWindowConfig());
                    } else if (userSettings.getAppsShortCut().equals(keycode)) {
                        show(ConfigFactory.appsWindowConfig());
                    } else if (userSettings.getNotifyShortCut().equals(keycode)) {
                        show(ConfigFactory.notifyWindowConfig(NotificationCategory.ALL));
                    } else if (userSettings.getCloseShortCut().equals(keycode)) {
                        eventBus.fireEvent(new WindowCloseRequestEvent());
                    }
                }
            }
        };
    }

    /**
     * FIXME JDS This needs to be ported to notifications.
     */
    private void initMessagePoller() {
        // Do an initial fetch of message counts, otherwise the initial count will not be fetched until
        // after an entire poll-length of the MessagePoller's timer (15 seconds by default).
        GetMessageCounts notificationCounts = new GetMessageCounts();
        notificationCounts.run();
        messagePoller.addTask(notificationCounts);
        messagePoller.start();
    }

    private void processQueryStrings() {
        boolean hasError = false;
        boolean hasDataTypeParameter = false;
        Map<String, List<String>> params = Window.Location.getParameterMap();
        for (String key : params.keySet()) {

            if (QueryStrings.TYPE.equalsIgnoreCase(key)) { // Process query strings for opening DE windows
                for (String paramValue : params.get(key)) {
                    WindowConfig windowConfig = null;

                    if (TypeQueryValues.APPS.equalsIgnoreCase(paramValue)) {
                        final AppsWindowConfig appsConfig = ConfigFactory.appsWindowConfig();
                        final String appCategoryId = Window.Location.getParameter(QueryStrings.APP_CATEGORY);
                        appsConfig.setSelectedAppGroup(CommonModelUtils.createHasIdFromString(appCategoryId));
                        windowConfig = appsConfig;
                    } else if (TypeQueryValues.DATA.equalsIgnoreCase(paramValue)) {
                        hasDataTypeParameter = true;
                        DiskResourceWindowConfig drConfig = ConfigFactory.diskResourceWindowConfig(true);
                        drConfig.setMaximized(true);
                        // If user has multiple folder parameters, the last one will be used.
                        String folderParameter = Window.Location.getParameter(QueryStrings.FOLDER);
                        String selectedFolder = URL.decode(Strings.nullToEmpty(folderParameter));

                        if (!Strings.isNullOrEmpty(selectedFolder)) {
                            HasPath folder = CommonModelUtils.createHasPathFromString(selectedFolder);
                            drConfig.setSelectedFolder(folder);
                        }
                        windowConfig = drConfig;
                    }

                    if (windowConfig != null) {
                        show(windowConfig);
                    }

                }
            } else if (AuthErrors.ERROR.equalsIgnoreCase(key)) { // Process errors
                hasError = true;
                // Remove underscores, and upper case whole error
                String upperCaseError = Iterables.getFirst(params.get(key), "").replaceAll("_", " ").toUpperCase();
                String apiName = Strings.nullToEmpty(Window.Location.getParameter(AuthErrors.API_NAME));
                String titleApi = apiName.isEmpty() ? "" : " : " + apiName;
                IplantErrorDialog errorDialog = new IplantErrorDialog(upperCaseError + titleApi,
                                                                      Window.Location.getParameter(AuthErrors.ERROR_DESCRIPTION));
                errorDialog.show();
                errorDialog.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        getUserSession(false);
                    }
                });
            }
        }
        if (!hasError) getUserSession(hasDataTypeParameter);
    }

    /**
     * Disable the context menu of the browser using native JavaScript.
     * <p/>
     * This disables the user's ability to right-click on this widget and get the browser's context menu
     */
    private native void setBrowserContextMenuEnabled(boolean enabled)
    /*-{
        $doc.oncontextmenu = function () {
            return enabled;
        };
    }-*/;

}
