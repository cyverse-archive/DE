package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.events.*;
import org.iplantc.de.apps.client.events.AppUpdatedEvent.AppUpdatedEventHandler;
import org.iplantc.de.apps.client.presenter.proxy.AppGroupProxy;
import org.iplantc.de.apps.client.views.AppInfoView;
import org.iplantc.de.apps.client.views.AppsView;
import org.iplantc.de.apps.client.views.dialogs.NewToolRequestDialog;
import org.iplantc.de.apps.client.views.dialogs.SubmitAppForPublicDialog;
import org.iplantc.de.apps.client.views.widgets.AppsViewToolbar;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEventHandler;
import org.iplantc.de.apps.client.views.widgets.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The presenter for the AppsView.
 * 
 * <p>
 * Events fired from this presenter:
 * <ul>
 * <li> {@link AppDeleteEvent}</li>
 * <li> {@link AppGroupCountUpdateEvent}</li>
 * <li> {@link CreateNewAppEvent}</li>
 * <li> {@link CreateNewWorkflowEvent}</li>
 * <ul>
 * 
 * @author jstroot
 * 
 */
public class AppsViewPresenterImpl implements AppsView.Presenter {

    private final EventBus eventBus;
    private static String WORKSPACE;
    private static String USER_APPS_GROUP;
    private static String FAVORITES;

    protected final AppsView view;
    protected Builder builder;

    private final AppGroupProxy appGroupProxy;
    private AppsViewToolbar toolbar;

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
    public AppsViewPresenterImpl(final AppsView view, final AppGroupProxy proxy,
                                 final AppsViewToolbar toolbar,
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


        builder = new MyBuilder(this);

        // Initialize AppGroup TreeStore proxy and loader
        this.appGroupProxy = proxy;

        if (toolbar != null) {
            this.toolbar = toolbar;
            this.view.setNorthWidget(this.toolbar);
            this.toolbar.setPresenter(this);
        }

        this.view.setPresenter(this);

        initHandlers();
        initConstants();
    }

    protected void initHandlers() {
        eventHandlers.add(eventBus.addHandler(AppSearchResultLoadEvent.TYPE, new AppSearchResultLoadEventHandler() {
            @Override
            public void onLoad(AppSearchResultLoadEvent event) {
                if (event.getSource() == getAppSearchRpcProxy()) {
                    String searchText = event.getSearchText();
                    updateSearchRegex(searchText);

                    List<App> results = event.getResults();
                    int total = results == null ? 0 : results.size();

                    view.selectAppGroup(null);
                    view.setCenterPanelHeading(displayStrings.searchAppResultsHeader(searchText, total));
                    view.setApps(results);
                    view.unMaskCenterPanel();
                }
            }
        }));
        eventHandlers.add(eventBus.addHandler(AppFavoritedEvent.TYPE, new AppFavoritedEventHander() {
            @Override
            public void onAppFavorited(AppFavoritedEvent event) {
                AppGroup favAppGrp = view.findAppGroupByName(FAVORITES);
                if (favAppGrp != null) {
                    int tmp = event.isFavorite() ? 1 : -1;

                    view.updateAppGroupAppCount(favAppGrp, favAppGrp.getAppCount() + tmp);
                }
                // If the current app group is Workspace or Favorites, remove the app from the list.
                if (getSelectedAppGroup().getName().equalsIgnoreCase(WORKSPACE) || getSelectedAppGroup().getName().equalsIgnoreCase(FAVORITES)) {
                    view.removeApp(view.findApp(event.getAppId()));
                }
            }
        }));

        eventHandlers.add(eventBus.addHandler(AppUpdatedEvent.TYPE, new AppUpdatedEventHandler() {
            @Override
            public void onAppUpdated(AppUpdatedEvent event) {

                // JDS Always assume that the app is in the "Apps Under Development" group
                AppGroup userAppGrp = view.findAppGroupByName(USER_APPS_GROUP);
                if (userAppGrp != null) {
                    view.selectAppGroup(userAppGrp.getId());
                }

            }
        }));

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
    public void onAppGroupSelected(final AppGroup ag) {
        if (toolbar != null) {
            toolbar.setAppMenuEnabled(false);
            toolbar.setWorkflowMenuEnabled(false);
        }

        searchRegex = null;
        List<String> groupNames = computeGroupHirarchy(ag);
        view.setCenterPanelHeading(Joiner.on(" >> ").join(groupNames));
        fetchApps(ag);
    }

    @Override
    public List<String> computeGroupHirarchy(final AppGroup ag) {
        List<String> groupNames = Lists.newArrayList();

        for (AppGroup group : getGroupHierarchy(ag)) {
            groupNames.add(group.getName());
        }
        Collections.reverse(groupNames);
        return groupNames;
    }

    @Override
    public void onAppSelected(final App app) {
        if (app != null) {
            toolbar.setAppMenuEnabled(true);
            toolbar.setWorkflowMenuEnabled(true);
            if (app.isPublic()) {
                if (app.getStepCount() == 1) {
                    toolbar.setDeleteAppMenuItemEnabled(false);
                    toolbar.setSubmitAppMenuItemEnabled(false);
                    toolbar.setCopyAppMenuItemEnabled(true);

                    if (userInfo.getEmail().equals(app.getIntegratorEmail())) {
                        // JDS If the current user is the integrator
                        toolbar.setEditAppMenuItemEnabled(true);
                    } else {
                        toolbar.setEditAppMenuItemEnabled(false);
                    }
                    toolbar.setDeleteWorkflowMenuItemEnabled(false);
                    toolbar.setSubmitWorkflowMenuItemEnabled(false);
                    toolbar.setCopyWorkflowMenuItemEnabled(false);
                    toolbar.setEditWorkflowMenuItemEnabled(false);
                } else {
                    toolbar.setDeleteWorkflowMenuItemEnabled(false);
                    toolbar.setSubmitWorkflowMenuItemEnabled(false);
                    toolbar.setCopyWorkflowMenuItemEnabled(true);
                    toolbar.setEditWorkflowMenuItemEnabled(false);
                    toolbar.setDeleteAppMenuItemEnabled(false);
                    toolbar.setSubmitAppMenuItemEnabled(false);
                    toolbar.setCopyAppMenuItemEnabled(false);
                    toolbar.setEditAppMenuItemEnabled(false);
                }
            } else {
                if (app.getStepCount() == 1) {
                    toolbar.setEditAppMenuItemEnabled(true);
                    toolbar.setDeleteAppMenuItemEnabled(true);
                    toolbar.setSubmitAppMenuItemEnabled(true);
                    toolbar.setCopyAppMenuItemEnabled(true);
                    toolbar.setEditWorkflowMenuItemEnabled(false);
                    toolbar.setDeleteWorkflowMenuItemEnabled(false);
                    toolbar.setSubmitWorkflowMenuItemEnabled(false);
                    toolbar.setCopyWorkflowMenuItemEnabled(false);
                } else {
                    toolbar.setEditWorkflowMenuItemEnabled(true);
                    toolbar.setDeleteWorkflowMenuItemEnabled(true);
                    toolbar.setSubmitWorkflowMenuItemEnabled(true);
                    toolbar.setCopyWorkflowMenuItemEnabled(true);
                    toolbar.setEditAppMenuItemEnabled(false);
                    toolbar.setDeleteAppMenuItemEnabled(false);
                    toolbar.setSubmitAppMenuItemEnabled(false);
                    toolbar.setCopyAppMenuItemEnabled(false);
                }
            }

            if (!app.isDisabled()) {
                if (app.getStepCount() == 1) {
                    toolbar.setAppRunMenuItemEnabled(true);
                    toolbar.setWorkflowRunMenuItemEnabled(false);
                } else {
                    toolbar.setWorkflowRunMenuItemEnabled(true);
                    toolbar.setAppRunMenuItemEnabled(false);
                }
            } else {
                toolbar.setAppRunMenuItemEnabled(false);
                toolbar.setWorkflowRunMenuItemEnabled(false);
            }
        } else {
            toolbar.setDeleteWorkflowMenuItemEnabled(false);
            toolbar.setSubmitWorkflowMenuItemEnabled(false);
            toolbar.setCopyWorkflowMenuItemEnabled(false);
            toolbar.setEditWorkflowMenuItemEnabled(false);
            toolbar.setDeleteAppMenuItemEnabled(false);
            toolbar.setSubmitAppMenuItemEnabled(false);
            toolbar.setCopyAppMenuItemEnabled(false);
            toolbar.setEditAppMenuItemEnabled(false);
            toolbar.setAppRunMenuItemEnabled(false);
            toolbar.setWorkflowRunMenuItemEnabled(false);
            toolbar.setAppMenuEnabled(true);
            toolbar.setWorkflowMenuEnabled(true);
        }
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
                ErrorHandler.post(errorStrings.retrieveAppListingFailed(), caught);
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
    public List<App> getAllSelectedApps() {
        return view.getAllSelectedApps();
    }

    @Override
    public AppGroup getSelectedAppGroup() {
        return view.getSelectedAppGroup();
    }

    @Override
    public void onAppInfoClicked() {
        showAppInfoWindow(getSelectedApp());
    }

    private void showAppInfoWindow(App app) {
        Dialog appInfoWin = new Dialog();
        appInfoWin.setModal(true);
        appInfoWin.setResizable(false);
        appInfoWin.setHeadingText(app.getName());
        appInfoWin.setPixelSize(450, 300);
        appInfoWin.add(new AppInfoView(app, this));
        appInfoWin.getButtonBar().clear();
        appInfoWin.show();
    }

    @Override
    public void onRequestToolClicked() {
        new NewToolRequestDialog().show();
    }

    @Override
    public void onCopyClicked() {
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
    public void onDeleteClicked() {
        final List<App> apps = getAllSelectedApps();
        if (apps == null || apps.isEmpty()) {
            return;
        }

        ConfirmMessageBox msgBox = new ConfirmMessageBox(displayStrings.warning(), displayStrings.appDeleteWarning());

        msgBox.addHideHandler(new HideHandler() {

            @Override
            public void onHide(HideEvent event) {
                Dialog btn = (Dialog)event.getSource();
                String text = btn.getHideButton().getItemId();
                if (text.equals(PredefinedButton.YES.name())) {
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
    public void createNewAppClicked() {
        eventBus.fireEvent(new CreateNewAppEvent());
    }

    @Override
    public void createWorkflowClicked() {
        eventBus.fireEvent(new CreateNewWorkflowEvent());
    }

    @Override
    public void onAppInfoClick(App app) {
        showAppInfoWindow(app);
    }

    @Override
    public void onEditClicked() {
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
    public AppsViewToolbar getToolbar() {
        return toolbar;
    }

    @Override
    public Builder builder() {
        return builder;
    }

    private class MyBuilder implements Builder {

        private final AppsViewPresenterImpl presenter;

        MyBuilder(AppsViewPresenterImpl presenter) {
            this.presenter = presenter;
        }

        @Override
        public void go(HasOneWidget container) {
            presenter.go(container);
        }

        @Override
        public void go(HasOneWidget container, AppGroup selectedAppGroup, App selectedApp) {
            presenter.go(container, selectedAppGroup, selectedApp);
        }

        @Override
        public Builder hideToolbarAppButton() {
            presenter.getToolbar().hideAppMenu();
            return this;
        }

        @Override
        public Builder hideToolbarWorkFlowButton() {
            presenter.getToolbar().hideWorkflowMenu();
            return this;
        }
    }

    @Override
    public Grid<App> getAppsGrid() {
        return view.getAppsGrid();
    }

    @Override
    public void onAppRunClick() {
        fireRunAppEvent(getSelectedApp());
    }

    @Override
    public AppSearchRpcProxy getAppSearchRpcProxy() {
        return toolbar.getAppSearchRpcProxy();

    }

    @Override
    public void onAppNameSelected(final App app) {
        if (app.isRunnable()) {
            fireRunAppEvent(app);
        } else {
            announcer.schedule(new ErrorAnnouncementConfig(errorStrings.appLaunchWithoutToolError()));
        }
    }

    private void fireRunAppEvent(final App app) {
        if (app != null && !app.isDisabled()) {
            eventBus.fireEvent(new RunAppEvent(app));
        }
    }

    @Override
    public AppGroup getAppGroupFromElement(Element el) {
        return view.getAppGroupFromElement(el);
    }

    @Override
    public App getAppFromElement(Element el) {
        return view.getAppFromElement(el);
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

    @Override
    public List<AppGroup> getGroupHierarchy(AppGroup grp) {
        return view.getGroupHierarchy(grp, null);
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
