package org.iplantc.de.desktop.client.presenter;

import static com.google.common.base.Preconditions.checkNotNull;

import org.iplantc.de.client.DEClientConstants;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.IsHideable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.UserSettings;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.client.models.analysis.AnalysesAutoBeanFactory;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.notifications.Notification;
import org.iplantc.de.client.models.notifications.NotificationAutoBeanFactory;
import org.iplantc.de.client.models.notifications.NotificationCategory;
import org.iplantc.de.client.models.notifications.NotificationMessage;
import org.iplantc.de.client.models.notifications.payload.PayloadAnalysis;
import org.iplantc.de.client.models.notifications.payload.PayloadApps;
import org.iplantc.de.client.models.notifications.payload.PayloadAppsList;
import org.iplantc.de.client.models.notifications.payload.PayloadRequest;
import org.iplantc.de.client.models.requestStatus.RequestHistory;
import org.iplantc.de.client.services.DEFeedbackServiceFacade;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.services.MessageServiceFacade;
import org.iplantc.de.client.services.UserSessionServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.collaborators.client.views.ManageCollaboratorsDialog;
import org.iplantc.de.collaborators.client.views.ManageCollaboratorsView;
import org.iplantc.de.commons.client.CommonUiConstants;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.commons.client.requests.KeepaliveTimer;
import org.iplantc.de.commons.client.util.WindowUtil;
import org.iplantc.de.commons.client.views.dialogs.IplantErrorDialog;
import org.iplantc.de.commons.client.views.window.configs.AnalysisWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.AppWizardConfig;
import org.iplantc.de.commons.client.views.window.configs.AppsWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.DiskResourceWindowConfig;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.client.DesktopView;
import org.iplantc.de.desktop.client.presenter.util.MessagePoller;
import org.iplantc.de.desktop.client.presenter.util.NotificationWebSocketManager;
import org.iplantc.de.desktop.client.presenter.util.SystemMessageWebSocketManager;
import org.iplantc.de.desktop.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.desktop.shared.DeModule;
import org.iplantc.de.fileViewers.client.callbacks.LoadGenomeInCoGeCallback;
import org.iplantc.de.notifications.client.events.WindowShowRequestEvent;
import org.iplantc.de.notifications.client.utils.NotifyInfo;
import org.iplantc.de.notifications.client.views.dialogs.RequestHistoryDialog;
import org.iplantc.de.shared.events.UserLoggedOutEvent;
import org.iplantc.de.shared.services.PropertyServiceAsync;
import org.iplantc.de.systemMessages.client.events.NewSystemMessagesEvent;
import org.iplantc.de.systemMessages.client.view.NewMessageView;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.util.KeyNav;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.box.AutoProgressMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;

import com.sksamuel.gwt.websockets.WebsocketListener;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author jstroot
 */
public class DesktopPresenterImpl implements DesktopView.Presenter {



    interface AuthErrors {
        String API_NAME = "api_name";
        String ERROR = "error";
        String ERROR_DESCRIPTION = "error_description";
    }

    interface QueryStrings {
        String APP_CATEGORY = "app-category";
        String FOLDER = "folder";
        String TYPE = "type";
        String APP_ID = "app-id";
    }

    public interface TypeQueryValues {
        String APPS = "apps";
        String DATA = "data";
    }

    final DesktopWindowManager desktopWindowManager;
    @Inject IplantAnnouncer announcer;
    @Inject CommonUiConstants commonUiConstants;
    @Inject DEClientConstants deClientConstants;
    @Inject DEProperties deProperties;
    @Inject Provider<ErrorHandler> errorHandlerProvider;
    @Inject Provider<DEFeedbackServiceFacade> feedbackServiceProvider;
    @Inject Provider<FileEditorServiceFacade> fileEditorServiceProvider;
    @Inject MessageServiceFacade messageServiceFacade;
    @Inject NotificationAutoBeanFactory notificationFactory;
    @Inject DiskResourceAutoBeanFactory diskResourceFactory;
    @Inject AnalysesAutoBeanFactory analysesFactory;
    @Inject PropertyServiceAsync propertyServiceFacade;
    @Inject UserInfo userInfo;
    @Inject UserSessionServiceFacade userSessionService;
    @Inject UserSettings userSettings;
    @Inject NotifyInfo notifyInfo;
    @Inject DiskResourceUtil diskResourceUtil;
    @Inject DesktopPresenterAppearance appearance;

    private final EventBus eventBus;
    private final KeepaliveTimer keepaliveTimer;
    private final MessagePoller messagePoller;
    private final SaveSessionPeriodic ssp;
    private final NewMessageView.Presenter systemMsgPresenter;
    private final DesktopView view;
    private final WindowManager windowManager;
    private NotificationWebSocketManager notificationWebSocketManager;
    private SystemMessageWebSocketManager systemMessageWebSocketManager;
    private boolean loggedOut;

    public static final int NEW_NOTIFICATION_LIMIT = 10;

    @Inject
    public DesktopPresenterImpl(final DesktopView view,
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
        this.loggedOut = false;
        this.view.setPresenter(this);
        globalEventHandler.setPresenter(this, this.view);
        windowEventHandler.setPresenter(this, desktopWindowManager);
        if (DebugInfo.isDebugIdEnabled()) {
            this.view.ensureDebugId(DeModule.Ids.DESKTOP);
        }

        initNotificationWebSocket();
        initSystemMessageWebSocket();
    }

    private void initNotificationWebSocket() {
        notificationWebSocketManager = NotificationWebSocketManager.getInstace();
        notificationWebSocketManager.openWebSocket(new WebsocketListener() {

            @Override
            public void onClose() {
                GWT.log("Wesbsocket onClose()");
                //if websocket connection closed unexpectedly, retry connection!
                if(!loggedOut) {
                    GWT.log("reconnecting...");
                    notificationWebSocketManager.openWebSocket(this);
                }
            }

            @Override
            public void onMessage(String msg) {
                GWT.log("onMessage(): " + msg);
                processNotification(msg);
            }

            @Override
            public void onOpen() {
                GWT.log("websocket onOpen()");
            }
        });
    }

    private void initSystemMessageWebSocket() {
        systemMessageWebSocketManager = SystemMessageWebSocketManager.getInstace();
        systemMessageWebSocketManager.openWebSocket(new WebsocketListener() {

            @Override
            public void onClose() {
                GWT.log("Wesbsocket onClose()");
                //if websocket connection closed unexpectedly, retry connection!
                if(!loggedOut) {
                    GWT.log("reconnecting...");
                    systemMessageWebSocketManager.openWebSocket(this);
                }
            }

            @Override
            public void onMessage(String msg) {
                GWT.log("onMessage(): " + msg);
                processSystemMessage(msg);
            }

            @Override
            public void onOpen() {
                GWT.log("websocket onOpen()");
            }
        });
    }

    private void processSystemMessage(String msg){
        JSONObject obj = null;
        try {
            obj = JSONParser.parseStrict(msg).isObject();
            eventBus.fireEvent(new NewSystemMessagesEvent());
        } catch (Exception e) {
            //ignore error and message as it not in json format
          }
    }

    private void processNotification(String msg) {
        JSONObject obj = null;
        try {
             obj = JSONParser.parseStrict(msg).isObject();
        } catch (Exception e) {
            //ignore error and message as it not in json format
            return;
        }
        Number num = JsonUtil.getInstance().getNumber(obj, "total");
        view.setUnseenNotificationCount(num.intValue());
        GWT.log("count -->" + num.intValue());
        JSONObject notifi = JsonUtil.getInstance().getObject(obj, "message");
        GWT.log("notifi-->" + notifi.toString());
        Notification n =
                AutoBeanCodex.decode(notificationFactory, Notification.class, notifi.toString()).as();
        if (n != null) {
            loadMessageInView(n);
        }

    }

    private void loadMessageInView(Notification n) {
        NotificationMessage newMessage = n.getMessage();
        ListStore<NotificationMessage> nmStore = view.getNotificationStore();
        final NotificationMessage modelWithKey =
                nmStore.findModelWithKey(Long.toString(newMessage.getTimestamp()));
        if (modelWithKey == null) {
            nmStore.add(newMessage);
                displayNotificationPopup(newMessage);
        }
    }


    void displayNotificationPopup(NotificationMessage nm) {
        if (NotificationCategory.ANALYSIS.equals(nm.getCategory())) {
            PayloadAnalysis analysisPayload =
                    AutoBeanCodex.decode(notificationFactory, PayloadAnalysis.class, nm.getContext())
                                 .as();
            if ("Failed".equals(analysisPayload.getStatus())) { //$NON-NLS-1$
                notifyInfo.displayWarning(nm.getMessage());
            } else {
                notifyInfo.display(nm.getMessage());
            }
        } else {
            notifyInfo.display(nm.getMessage());
        }
    }

    public static native void doIntro() /*-{
		var introjs = $wnd.introJs();
		introjs.setOption("showStepNumbers", false);
		introjs.setOption("skipLabel", "Exit");
		introjs.setOption("overlayOpacity", .5);
		introjs.start();
    }-*/;


    @Override
    public void doLogout(boolean sessionTimeout) {
       cleanUp();
        //session is timed-out, following rpc call will fail and cause 401.
        if(!sessionTimeout) {
            userSessionService.logout(new RuntimeCallbacks.LogoutCallback(userSessionService,
                                                                          deClientConstants,
                                                                          userSettings,
                                                                          appearance,
                                                                          getOrderedWindowStates()));
        } else {
            final String redirectUrl = GWT.getHostPageBaseURL() + deClientConstants.logoutUrl();
            Window.Location.assign(redirectUrl);
        }
    }

    private void cleanUp() {
        loggedOut = true;
        messagePoller.stop();
        notificationWebSocketManager.closeWebSocket();
        systemMessageWebSocketManager.closeWebSocket();
    }

    @Override
    public void doMarkAllSeen(final boolean announce) {
       messageServiceFacade.markAllNotificationsSeen(new AsyncCallback<Void>() {
           @Override
           public void onFailure(Throwable caught) {
               errorHandlerProvider.get().post(caught);
           }

           @Override
           public void onSuccess(Void result) {
               for(NotificationMessage nm : view.getNotificationStore().getAll()){
                   nm.setSeen(true);
                   view.getNotificationStore().update(nm);
               }
               view.setUnseenNotificationCount(0);
               if(!announce){
                   return;
               }
               announcer.schedule(new SuccessAnnouncementConfig(appearance.markAllAsSeenSuccess(), true, 3000));
           }
       });
    }

    @Override
    public void doSeeAllNotifications() {
        show(ConfigFactory.notifyWindowConfig(NotificationCategory.ALL));
        view.hideNotificationMenu();
    }

    @Override
    public void doSeeNewNotifications() {
        show(ConfigFactory.notifyWindowConfig(NotificationCategory.NEW));
        view.hideNotificationMenu();
    }

    public void doViewGenomes(final File file) {
        JSONObject obj = new JSONObject();
        JSONArray pathArr = new JSONArray();
        pathArr.set(0, new JSONString(file.getPath()));
        obj.put("paths", pathArr);
        fileEditorServiceProvider.get().loadGenomesInCoge(obj, new LoadGenomeInCoGeCallback(null));
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
        propertyServiceFacade.getProperties(new InitializationCallbacks.PropertyServiceCallback(deProperties,
                                                                        userInfo,
                                                                        userSettings,
                                                                        userSessionService,
                                                                        errorHandlerProvider,
                                                                        appearance,
                                                                        panel,
                                                                        this));
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
     * FIXME REFACTOR JDS The manage collaborators presenter should be used here, not the view.
     */
    @Override
    public void onCollaboratorsClick() {
        new ManageCollaboratorsDialog(ManageCollaboratorsView.MODE.MANAGE).show();
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

    /**
     * FIXME REFACTOR JDS Create notifications module and move this implementation there
     */
    @Override
    public void onNotificationSelected(final NotificationMessage selectedItem) {
        checkNotNull(selectedItem);
        checkNotNull(selectedItem.getCategory());
        checkNotNull(selectedItem.getContext());

        String context = selectedItem.getContext();
        switch(selectedItem.getCategory()){

            case APPS:
                final AppsWindowConfig appsConfig = ConfigFactory.appsWindowConfig();
                final PayloadAppsList pal = AutoBeanCodex.decode(notificationFactory,
                                                                 PayloadAppsList.class,
                                                                 context).as();
                if (pal != null && pal.getApps() != null && pal.getApps().size() > 0) {
                    PayloadApps payload = pal.getApps().get(0);
                    final String appCategoryId = payload.getCategoryId();
                    final String appId = payload.getId();
                    appsConfig.setSelectedAppCategory(CommonModelUtils.getInstance()
                                                                      .createHasIdFromString(
                                                                              appCategoryId));
                    appsConfig.setSelectedApp(CommonModelUtils.getInstance()
                                                              .createHasIdFromString(appId));
                    EventBus.getInstance()
                            .fireEvent(new WindowShowRequestEvent(appsConfig, true));
                }

                break;

            case DATA:
                // execute data context
                File file = AutoBeanCodex.decode(diskResourceFactory, File.class, context).as();
                List<HasId> selectedResources = Lists.newArrayList();
                selectedResources.add(file);

                DiskResourceWindowConfig dataWindowConfig = ConfigFactory.diskResourceWindowConfig(false);
                HasPath folder = diskResourceUtil.getFolderPathFromFile(file);
                dataWindowConfig.setSelectedFolder(folder);
                dataWindowConfig.setSelectedDiskResources(selectedResources);
                show(dataWindowConfig, true);

                break;

            case ANALYSIS:
                AutoBean<Analysis> hAb = AutoBeanCodex.decode(analysesFactory, Analysis.class, context);

                AnalysisWindowConfig analysisWindowConfig = ConfigFactory.analysisWindowConfig();
                analysisWindowConfig.setSelectedAnalyses(Lists.newArrayList(hAb.as()));
                show(analysisWindowConfig, true);
                break;

            case PERMANENTIDREQUEST:
                PayloadRequest request = AutoBeanCodex.decode(notificationFactory,
                                                              PayloadRequest.class,
                                                              context).as();
                getRequestStatusHistory(request.getId(), NotificationCategory.PERMANENTIDREQUEST);
                break;
            case TOOLREQUEST:
                PayloadRequest toolRequest = AutoBeanCodex.decode(notificationFactory,
                                                                      PayloadRequest.class,
                                                                      context).as();

                List<RequestHistory> history = toolRequest.getHistory();

                RequestHistoryDialog dlg = new RequestHistoryDialog(NotificationCategory.TOOLREQUEST.toString(),
                                                                    history);
                dlg.show();

                break;

            default:
                break;
        }

        messageServiceFacade.markAsSeen(selectedItem, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                errorHandlerProvider.get().post(caught);
            }

            @Override
            public void onSuccess(String result) {
                selectedItem.setSeen(true);
                view.getNotificationStore().update(selectedItem);

                final String asString = StringQuoter.split(result).get("count").asString();
                final int count = Integer.parseInt(asString);
                view.setUnseenNotificationCount(count);
            }
        });

    }

    private void getRequestStatusHistory(String id, NotificationCategory cat) {
        if (cat.equals(NotificationCategory.PERMANENTIDREQUEST)) {
            messageServiceFacade.getPermanentIdRequestStatusHistory(id, new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {
                    IplantAnnouncer.getInstance()
                                   .schedule(new ErrorAnnouncementConfig(appearance.requestHistoryError()));
                }

                @Override
                public void onSuccess(String result) {
                    PayloadRequest toolRequest = AutoBeanCodex.decode(notificationFactory,
                                                                      PayloadRequest.class,
                                                                      result).as();

                    List<RequestHistory> history = toolRequest.getHistory();

                    RequestHistoryDialog dlg = new RequestHistoryDialog(NotificationCategory.PERMANENTIDREQUEST.toString(),
                                                                        history);
                    dlg.show();

                }
            });
        }
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
    public void saveUserSettings(final UserSettings value,
                                 final boolean updateSilently) {
        final RuntimeCallbacks.SaveUserSettingsCallback callback = new RuntimeCallbacks.SaveUserSettingsCallback(value,
                                                                               userSettings,
                                                                               announcer,
                                                                               appearance,
                                                                               updateSilently);
        userSessionService.saveUserPreferences(value.asSplittable(), callback);
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
                errorHandlerProvider.get().post(appearance.feedbackServiceFailure(), caught);
            }

            @Override
            public void onSuccess(Void result) {
                isHideable.hide();
                announcer.schedule(new SuccessAnnouncementConfig(appearance.feedbackSubmitted(), true, 3000));
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
        messagePoller.start();
        initKBShortCuts();
        panel.add(view);
        processQueryStrings();
        messageServiceFacade.getRecentMessages(new InitializationCallbacks.GetInitialNotificationsCallback(view, appearance, announcer));
    }

    void restoreWindows(List<WindowState> windowStates) {
        for (WindowState ws : windowStates) {
            desktopWindowManager.show(ws);
        }
    }

    private void getUserSession(final boolean urlHasDataTypeParameter) {
        // do not attempt to get user session for new user
        if (userSettings.isSaveSession() && !urlHasDataTypeParameter && !userInfo.isNewUser()) {
            // This restoreSession's callback will also init periodic session saving.
            final AutoProgressMessageBox progressMessageBox = new AutoProgressMessageBox(appearance.loadingSession(),
                                                                                         appearance.loadingSessionWaitNotice());
            progressMessageBox.getProgressBar().setDuration(1000);
            progressMessageBox.getProgressBar().setInterval(100);
            progressMessageBox.auto();
            final Request req = userSessionService.getUserSession(new RuntimeCallbacks.GetUserSessionCallback(progressMessageBox,
                                                                                             appearance,
                                                                                             announcer,
                                                                                             this));
            progressMessageBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (Dialog.PredefinedButton.CANCEL.equals(event.getHideButton())) {
                        req.cancel();
                        SafeHtml msg = appearance.sessionRestoreCancelled();
                        announcer.schedule(new SuccessAnnouncementConfig(msg, true, 5000));
                    }
                }
            });
        } else if (urlHasDataTypeParameter) {
            doPeriodicSessionSave();
        }
    }

    void initKBShortCuts() {
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
                        desktopWindowManager.closeActiveWindow();
                    }
                }
            }
        };
    }

    void processQueryStrings() {
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
                        final String appId = Window.Location.getParameter(QueryStrings.APP_ID);
                        if (!Strings.isNullOrEmpty(appCategoryId)) {
                            appsConfig.setSelectedAppCategory(CommonModelUtils.getInstance().createHasIdFromString(appCategoryId));
                            windowConfig = appsConfig;
                        } else if (!Strings.isNullOrEmpty(appId)) {
                            AppWizardConfig config = ConfigFactory.appWizardConfig(appId);
                            show(config);
                        }
                        
                    } else if (TypeQueryValues.DATA.equalsIgnoreCase(paramValue)) {
                        hasDataTypeParameter = true;
                        DiskResourceWindowConfig drConfig = ConfigFactory.diskResourceWindowConfig(true);
                        drConfig.setMaximized(true);
                        // If user has multiple folder parameters, the last one will be used.
                        String folderParameter = Window.Location.getParameter(QueryStrings.FOLDER);
                        String selectedFolder = URL.decode(Strings.nullToEmpty(folderParameter));

                        if (!Strings.isNullOrEmpty(selectedFolder)) {
                            HasPath folder = CommonModelUtils.getInstance().createHasPathFromString(selectedFolder);
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

    void setBrowserContextMenuEnabled(boolean enabled){
        setBrowserContextMenuEnabledNative(enabled);
    }

    /**
     * Disable the context menu of the browser using native JavaScript.
     * <p/>
     * This disables the user's ability to right-click on this widget and get the browser's context menu
     */
    private native void setBrowserContextMenuEnabledNative(boolean enabled)
    /*-{
		$doc.oncontextmenu = function() {
			return enabled;
		};
    }-*/;

}
