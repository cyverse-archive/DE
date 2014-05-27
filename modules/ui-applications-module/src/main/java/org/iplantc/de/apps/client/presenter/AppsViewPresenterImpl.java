package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.events.*;
import org.iplantc.de.apps.client.presenter.proxy.AppGroupProxy;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.cells.AppFavoriteCell;
import org.iplantc.de.apps.client.views.cells.AppHyperlinkCell;
import org.iplantc.de.apps.client.views.dialogs.AppCommentDialog;
import org.iplantc.de.apps.client.views.dialogs.NewToolRequestDialog;
import org.iplantc.de.apps.client.views.dialogs.SubmitAppForPublicDialog;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.gin.ServicesInjector;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.HttpRedirectException;
import org.iplantc.de.shared.services.ConfluenceServiceFacade;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * The presenter for the AppsView.
 * 
 * @author jstroot
 * 
 */
public class AppsViewPresenterImpl implements AppsView.Presenter {

    private class AppsViewAppUpdatedEventHandler implements AppUpdatedEvent.AppUpdatedEventHandler {
        private final AppsView view;

        public AppsViewAppUpdatedEventHandler(AppsView view) {
            this.view = view;
        }

        @Override
        public void onAppUpdated(AppUpdatedEvent event) {

            // JDS Always assume that the app is in the "Apps Under Development" group
            AppGroup userAppGrp = view.findAppGroupByName(USER_APPS_GROUP);
            if (userAppGrp != null) {
                view.selectAppGroup(userAppGrp.getId());
            }

        }
    }

    private final EventBus eventBus;
    private static String WORKSPACE;
    private static String USER_APPS_GROUP;
    private static String FAVORITES;

    protected final AppsView view;

    private final AppGroupProxy appGroupProxy;

    private final List<HandlerRegistration> eventHandlers = new ArrayList<HandlerRegistration>();

    private HasId desiredSelectedAppId;
    private final AppServiceFacade appService;
    private final AppUserServiceFacade appUserService;
    private final UserInfo userInfo;
    private final DEProperties props;
    private final IplantAnnouncer announcer;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private RegExp searchRegex;

    @Inject
    public AppsViewPresenterImpl(final AppsView view,
                                 final AppGroupProxy proxy,
                                 final AppServiceFacade appService,
                                 final AppUserServiceFacade appUserService,
                                 final EventBus eventBus,
                                 final UserInfo userInfo,
                                 final DEProperties props,
                                 final IplantAnnouncer announcer,
                                 final IplantDisplayStrings displayStrings,
                                 final IplantErrorStrings errorStrings) {
        this.view = view;
        this.appService = appService;
        this.appUserService = appUserService;
        this.eventBus = eventBus;
        this.userInfo = userInfo;
        this.props = props;
        this.announcer = announcer;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;

        // Initialize AppGroup TreeStore proxy and loader
        this.appGroupProxy = proxy;

        this.view.setPresenter(this);

        eventHandlers.add(eventBus.addHandler(AppUpdatedEvent.TYPE, new AppsViewAppUpdatedEventHandler(view)));

        initConstants();
    }

    @Override
    public void onAppFavoriteRequest(final AppFavoriteCell.RequestAppFavoriteEvent event) {
        final App app = event.getApp();
        appUserService.favoriteApp(userInfo.getWorkspaceId(), app.getId(), !app.isFavorite(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                 announcer.schedule(new ErrorAnnouncementConfig(errorStrings.favServiceFailure()));
            }

            @Override
            public void onSuccess(String result) {
                app.setFavorite(!app.isFavorite());
                view.onAppFavorited(new AppFavoritedEvent(app));
            }
        });
    }

    @Override
    public void onAppCommentSelectedEvent(AppCommentSelectedEvent event) {
        final App app = event.getApp();
        final AppFeedback userFeedback = app.getRating();
        final Long commentId = userFeedback.getCommentId();

        // populate dialog via an async call if previous comment ID exists, otherwise show blank dlg
        final AppCommentDialog dlg = new AppCommentDialog(app.getName());
        if ((commentId == null) || (commentId == 0)) {
            dlg.unmaskDialog();
        } else {
            ConfluenceServiceFacade.getInstance().getComment(commentId, new AsyncCallback<String>() {
                @Override
                public void onSuccess(String comment) {
                    dlg.setText(comment);
                    dlg.unmaskDialog();
                }

                @Override
                public void onFailure(Throwable e) {
                    // ErrorHandler.post(e.getMessage(), e);
                    dlg.unmaskDialog();
                }
            });
        }

        Command onConfirm = new Command() {
            @Override
            public void execute() {
                putAppComment(app, dlg.getComment());
            }
        };
        dlg.setCommand(onConfirm);
        dlg.show();
    }

    private void putAppComment(App app, String comment) {
        final AppFeedback userFeedback = app.getRating();

        AsyncCallback<String> callback = new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    userFeedback.setCommentId(Long.valueOf(result));
                } catch (NumberFormatException e) {
                    // no comment id, do nothing
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(I18N.ERROR.confluenceError(), caught);
            }
        };

        String appId = app.getId();
        int rating = userFeedback.getUserRating();
        String appWikiUrl = app.getWikiUrl();
        String authorEmail = app.getIntegratorEmail();

        Long commentId = userFeedback.getCommentId();
        if ((commentId == null) || (commentId == 0)) {
            ServicesInjector.INSTANCE.getAppUserServiceFacade().addAppComment(appId, rating, appWikiUrl,
                    comment, authorEmail, callback);
        } else {
            ServicesInjector.INSTANCE.getAppUserServiceFacade().editAppComment(appId, rating,
                    appWikiUrl, commentId, comment, authorEmail, callback);
        }
    }

    @Override
    public void onAppNameSelected(AppHyperlinkCell.AppNameSelectedEvent event) {
        App app = event.getSelectedApp();
        if (app.isRunnable()) {
            fireRunAppEvent(app);
        } else {
            announcer.schedule(new ErrorAnnouncementConfig(errorStrings.appLaunchWithoutToolError()));
        }
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        updateSearchRegex(event.getSearchText());
    }

    @Override
    public void cleanUp() {
        for (HandlerRegistration hr : eventHandlers) {
            eventBus.removeHandler(hr);
        }
    }

    private void initConstants() {
        WORKSPACE = props.getPrivateWorkspace();

        if (props.getPrivateWorkspaceItems() != null) {
            JSONArray items = JSONParser.parseStrict(props.getPrivateWorkspaceItems()).isArray();
            USER_APPS_GROUP = JsonUtil.getRawValueAsString(items.get(0));
            FAVORITES = JsonUtil.getRawValueAsString(items.get(1));
        }
    }

    /**
     * Sets a string which is a place holder for selection after a call to {@link #fetchApps(AppGroup)}
     * 
     * @param selectedApp
     */
    private void setDesiredSelectedApp(HasId selectedApp) {
        this.desiredSelectedAppId = selectedApp;
    }

    private HasId getDesiredSelectedApp() {
        return desiredSelectedAppId;
    }

    @Override
    public void onAppGroupSelectionChanged(AppGroupSelectionChangedEvent event) {
        searchRegex = null;
        if (event.getAppGroupSelection().isEmpty()){
            return;
        }
        AppGroup ag = event.getAppGroupSelection().get(0);
        fetchApps(ag);
    }

    /**
     * Retrieves the apps for the given group by updating and executing the list loader
     * 
     * @param ag
     */
    protected void fetchApps(final AppGroup ag) {
        view.maskCenterPanel(displayStrings.loadingMask());
        appService.getApps(ag.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
                AutoBean<AppList> bean = AutoBeanCodex.decode(factory, AppList.class, result);
                List<App> apps = bean.as().getApps();
                view.setApps(apps);
                if (getDesiredSelectedApp() != null) {
                    view.selectApp(getDesiredSelectedApp().getId());
                } else {
                    selectFirstApp();
                }
                setDesiredSelectedApp(null);
                view.unMaskCenterPanel();
                view.updateAppGroupAppCount(ag, apps.size());
            }

            @Override
            public void onFailure(Throwable caught) {
                if(caught instanceof HttpRedirectException){
                    Window.alert(displayStrings.agaveAuthRequiredMsg());
                } else {
                    ErrorHandler.post(errorStrings.retrieveAppListingFailed(), caught);
                }
                view.unMaskCenterPanel();
            }
        });
    }

    protected void selectFirstApp() {
        view.selectFirstApp();
    }

    @Override
    public void go(HasOneWidget container, final HasId selectedAppGroup, final HasId selectedApp) {
        container.setWidget(view);

        if (!view.isTreeStoreEmpty()) {
            doInitialAppSelection(selectedAppGroup, selectedApp);
        } else {
            // Fetch AppGroups
            reloadAppGroups(selectedAppGroup, selectedApp);
        }
    }

    protected void reloadAppGroups(final HasId selectedAppGroup, final HasId selectedApp) {
        view.maskWestPanel(displayStrings.loadingMask());
        view.clearAppGroups();
        appGroupProxy.load(null, new AsyncCallback<List<AppGroup>>() {
            @Override
            public void onSuccess(List<AppGroup> result) {
                view.addAppGroups(null, result);
                view.expandAppGroups();
                doInitialAppSelection(selectedAppGroup, selectedApp);
                view.unMaskWestPanel();
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
                view.unMaskWestPanel();
            }
        });
    }

    private void doInitialAppSelection(HasId selectedAppGroup, HasId selectedApp) {
        // Select previous user selections
        if (selectedAppGroup != null) {
            view.selectAppGroup(selectedAppGroup.getId());
            setDesiredSelectedApp(selectedApp);
        } else {
            view.selectFirstAppGroup();
        }

    }

    @Override
    public void go(final HasOneWidget container) {
        go(container, null, null);
    }

    @Override
    public App getSelectedApp() {
        return view.getSelectedApp();
    }

    @Override
    public AppGroup getSelectedAppGroup() {
        return view.getSelectedAppGroup();
    }

    @Override
    public void onRequestToolClicked() {
        new NewToolRequestDialog().show();
    }

    @Override
    public void copySelectedApp() {
        final App selectedApp = getSelectedApp();
        appUserService.appExportable(selectedApp.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                JSONObject exportable = JsonUtil.getObject(result);

                if (JsonUtil.getBoolean(exportable, "can-export", false)) { //$NON-NLS-1$
                    if (selectedApp.getStepCount() > 1) {
                        copyWorkflow(selectedApp);
                    } else {
                        copyApp(selectedApp);
                    }
                } else {
                    ErrorHandler.post(JsonUtil.getString(exportable, "cause")); //$NON-NLS-1$
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
            }
        });

    }

    private void copyWorkflow(final App app) {
        appUserService.copyWorkflow(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                // Update the user's private apps group count.
                AppGroup userAppsGrp = view.findAppGroupByName(USER_APPS_GROUP);
                if (userAppsGrp != null) {
                    view.updateAppGroupAppCount(userAppsGrp, userAppsGrp.getAppCount() + 1);
                }

                // If the current app group is Workspace or the user's private apps, reload that group.
                AppGroup selectedAppGroup = getSelectedAppGroup();
                if (selectedAppGroup != null) {
                    String selectedGroupName = selectedAppGroup.getName();

                    if (selectedGroupName.equalsIgnoreCase(WORKSPACE) || selectedGroupName.equalsIgnoreCase(USER_APPS_GROUP)) {
                        fetchApps(selectedAppGroup);
                    }
                }

                // Fire an EditWorkflowEvent for the new workflow copy.
                Splittable serviceWorkflowJson = StringQuoter.split(result);
                eventBus.fireEvent(new EditWorkflowEvent(app, serviceWorkflowJson));
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
            }
        });
    }

    private void copyApp(final App app) {
        appUserService.copyApp(app.getId(), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                // Update the user's private apps group count.
                AppGroup usersAppsGrp = view.findAppGroupByName(USER_APPS_GROUP);
                if (usersAppsGrp != null) {
                    view.updateAppGroupAppCount(usersAppsGrp, usersAppsGrp.getAppCount() + 1);
                }
                HasId hasId = CommonModelUtils.createHasIdFromString(StringQuoter.split(result).get("analysis_id").asString());
                if (!hasId.getId().isEmpty()) {
                    AppGroup selectedAppGroup = getSelectedAppGroup();
                    if (selectedAppGroup != null) {
                        fetchApps(selectedAppGroup);
                    }
                    eventBus.fireEvent(new EditAppEvent(hasId, false));
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
            }
        });
    }

    private void fetchWorkflowAndFireEditEvent(final App app) {
        appUserService.editWorkflow(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onSuccess(String result) {
                Splittable serviceWorkflowJson = StringQuoter.split(result);
                eventBus.fireEvent(new EditWorkflowEvent(app, serviceWorkflowJson));
            }

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(errorStrings.failToRetrieveApp(), caught);
                announcer.schedule(new ErrorAnnouncementConfig(errorStrings.failToRetrieveApp()));
            }
        });
    }

    @Override
    public void deleteSelectedApps() {
        final List<App> apps = view.getAllSelectedApps();
        if (apps == null || apps.isEmpty()) {
            return;
        }

        ConfirmMessageBox msgBox = new ConfirmMessageBox(displayStrings.warning(), displayStrings.appDeleteWarning());

        msgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
                if(PredefinedButton.YES.equals(event.getHideButton())) {
                    List<String> appIds = Lists.newArrayList();
                    for (App app : apps) {
                        appIds.add(app.getId());
                    }

                    appUserService.deleteAppFromWorkspace(userInfo.getUsername(), userInfo.getFullUsername(), appIds, new AsyncCallback<String>() {

                        @Override
                        public void onFailure(Throwable caught) {
                            ErrorHandler.post(errorStrings.appRemoveFailure(), caught);
                        }

                        @Override
                        public void onSuccess(String result) {
                            for (App app : apps) {
                                // Remove from visible list and update AppGroup app counts
                                view.removeApp(app);

                                // PSAR Always assume that the app is in the
                                // "Apps Under Development" group
                                AppGroup userAppGrp = view.findAppGroupByName(USER_APPS_GROUP);
                                if (userAppGrp != null) {
                                    view.updateAppGroupAppCount(userAppGrp, userAppGrp.getAppCount() - 1);
                                }

                                eventBus.fireEvent(new AppDeleteEvent(app.getId()));
                            }
                        }
                    });
                }

            }
        });
        msgBox.show();

    }

    @Override
    public void submitClicked() {
        App selectedApp = getSelectedApp();
        SubmitAppForPublicDialog dialog = new SubmitAppForPublicDialog(selectedApp);
        dialog.show();

    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
    }

    @Override
    public void createNewAppClicked() {
        eventBus.fireEvent(new CreateNewAppEvent());
    }

    @Override
    public void createWorkflowClicked() {
        eventBus.fireEvent(new CreateNewWorkflowEvent());
    }

    @Override
    public void editSelectedApp() {
        App selectedApp = getSelectedApp();

        if (selectedApp.getStepCount() > 1) {
            fetchWorkflowAndFireEditEvent(selectedApp);
        } else {
            boolean isAppPublished = selectedApp.isPublic();
            boolean isCurrentUserAppIntegrator = userInfo.getEmail().equals(selectedApp.getIntegratorEmail());

            eventBus.fireEvent(new EditAppEvent(selectedApp, isAppPublished && isCurrentUserAppIntegrator));
        }
    }

    @Override
    public AppsView.Presenter hideAppMenu(){
        view.hideAppMenu();
        return this;
    }

    @Override
    public AppsView.Presenter hideWorkflowMenu(){
        view.hideWorkflowMenu();
        return this;
    }

    @Override
    public Grid<App> getAppsGrid() {
        return view.getAppsGrid();
    }

    @Override
    public void runSelectedApp() {
        fireRunAppEvent(getSelectedApp());
    }

    private void fireRunAppEvent(final App app) {
        if (app != null && !app.isDisabled()) {
            eventBus.fireEvent(new RunAppEvent(app));
        }
    }

    @Override
    public String highlightSearchText(String text) {
        if (!Strings.isNullOrEmpty(text)) {
            text = SafeHtmlUtils.fromString(text).asString();

            if (searchRegex != null) {
                return searchRegex.replace(text, "<font style='background: #FF0'>$1</font>"); //$NON-NLS-1$
            }
        }

        return text;
    }

    private void updateSearchRegex(final String searchText) {
        if (Strings.isNullOrEmpty(searchText)) {
            searchRegex = null;
        } else {
            // The search service accepts * and ? wildcards, so convert them for the pattern group.
            String pattern = "(" + searchText.replace("*", ".*").replace('?', '.') + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            searchRegex = RegExp.compile(pattern, "ig"); //$NON-NLS-1$
        }
    }
}
