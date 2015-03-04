package org.iplantc.de.apps.client.presenter;

import org.iplantc.de.apps.client.AppsView;
import org.iplantc.de.apps.client.SubmitAppForPublicUseView;
import org.iplantc.de.apps.client.events.AppDeleteEvent;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewWorkflowEvent;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.apps.client.events.selection.AppCategorySelectionChangedEvent;
import org.iplantc.de.apps.client.events.selection.AppCommentSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppNameSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.presenter.proxy.AppCategoryProxy;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppAutoBeanFactory;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.apps.AppFeedback;
import org.iplantc.de.client.models.apps.AppList;
import org.iplantc.de.client.services.AppMetadataServiceFacade;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.comments.view.dialogs.CommentsDialog;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;
import org.iplantc.de.shared.exceptions.HttpRedirectException;
import org.iplantc.de.tools.requests.client.views.dialogs.NewToolRequestDialog;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * The presenter for the AppsView.
 *
 * @author jstroot
 */
public class AppsViewPresenterImpl implements AppsView.Presenter {

    private static class DeleteRatingCallback implements AsyncCallback<AppFeedback> {
        private final App appToUnrate;
        private final ListStore<App> listStore;

        public DeleteRatingCallback(final App appToUnrate,
                                    final ListStore<App> listStore) {
            this.appToUnrate = appToUnrate;
            this.listStore = listStore;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught.getMessage(), caught);
        }

        @Override
        public void onSuccess(AppFeedback result) {
            appToUnrate.setRating(result);

            // Update app in list store, this should update stars
            listStore.update(appToUnrate);
        }

    }

    private static class RateAppCallback implements AsyncCallback<String> {
        private final ListStore<App> listStore;
        private final int score;
        private final App selectedApp;

        public RateAppCallback(final App selectedApp,
                               final int score,
                               final ListStore<App> listStore) {
            this.selectedApp = selectedApp;
            this.score = score;
            this.listStore = listStore;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(String result) {
            selectedApp.getRating().setUserRating(score);

            // Update app in list store, this should update stars
            listStore.update(selectedApp);
        }
    }

    private class AppsViewAppUpdatedEventHandler implements AppUpdatedEvent.AppUpdatedEventHandler {
        private final AppsView view;

        public AppsViewAppUpdatedEventHandler(AppsView view) {
            this.view = view;
        }

        @Override
        public void onAppUpdated(AppUpdatedEvent event) {

            // JDS Always assume that the app is in the "Apps Under Development" group
            AppCategory userAppCategory = findAppCategoryByName(USER_APPS_GROUP);
            if (userAppCategory != null) {
                view.selectAppCategory(userAppCategory);
            }

        }
    }
    protected final AppsView view;
    @Inject AsyncProvider<CommentsDialog> commentsDialogProvider;
    @Inject JsonUtil jsonUtil;
    @Inject Provider<NewToolRequestDialog> newToolRequestDialogProvider;
    @Inject Provider<SubmitAppForPublicUseView.Presenter> submitAppForPublicPresenterProvider;
    private static String FAVORITES;
    private static String USER_APPS_GROUP;
    private static String WORKSPACE;
    private final Logger LOG = Logger.getLogger(AppsViewPresenterImpl.class.getName());
    private final IplantAnnouncer announcer;
    private final AppCategoryProxy appCategoryProxy;
    private final AppServiceFacade appService;
    private final AppUserServiceFacade appUserService;
    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final EventBus eventBus;
    private final List<HandlerRegistration> eventHandlers = new ArrayList<>();
    private final ListStore<App> listStore;
    private final AppMetadataServiceFacade metadataFacade;
    private final DEProperties props;
    private final TreeStore<AppCategory> treeStore;
    private final UserInfo userInfo;
    private HasId desiredSelectedAppId;
    private RegExp searchRegex;

    @Inject
    public AppsViewPresenterImpl(final AppsView view,
                                 final TreeStore<AppCategory> treeStore,
                                 final AppCategoryProxy proxy,
                                 final AppServiceFacade appService,
                                 final AppUserServiceFacade appUserService,
                                 final EventBus eventBus,
                                 final UserInfo userInfo,
                                 final DEProperties props,
                                 final IplantAnnouncer announcer,
                                 final IplantDisplayStrings displayStrings,
                                 final IplantErrorStrings errorStrings,
                                 final AppMetadataServiceFacade metadataFacade) {
        this.view = view;
        this.treeStore = treeStore;
        this.appService = appService;
        this.appUserService = appUserService;
        this.eventBus = eventBus;
        this.userInfo = userInfo;
        this.props = props;
        this.announcer = announcer;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.metadataFacade = metadataFacade;
        Comparator<AppCategory> comparator = new Comparator<AppCategory>() {

            @Override
            public int compare(AppCategory group1, AppCategory group2) {
                if (treeStore.getRootItems().contains(group1) || treeStore.getRootItems().contains(group2)) {
                    // Do not sort Root groups, since we want to keep the service's root order.
                    return 0;
                }

                return group1.getName().compareToIgnoreCase(group2.getName());
            }
        };

        treeStore.addSortInfo(new Store.StoreSortInfo<>(comparator, SortDir.ASC));

        this.listStore = new ListStore<>(new ModelKeyProvider<App>() {
            @Override
            public String getKey(App item) {
                return item.getId();
            }
        });
        // Initialize AppCategory TreeStore proxy and loader
        this.appCategoryProxy = proxy;

        this.view.setPresenter(this);
        this.view.addAppNameSelectedEventHandler(this);
        this.view.addAppCommentSelectedEventHandlers(this);
        this.view.addAppRatingDeselectedHandler(this);
        this.view.addAppRatingSelectedHandler(this);
        this.view.addAppFavoriteSelectedEventHandlers(this);
        this.view.addAppCategorySelectedEventHandler(this);
        this.view.addAppCategorySelectedEventHandler(this.view.getToolBar());
        this.view.addAppSelectionChangedEventHandler(this.view.getToolBar());

        eventHandlers.add(eventBus.addHandler(AppUpdatedEvent.TYPE, new AppsViewAppUpdatedEventHandler(view)));

        initConstants();
    }

    @Override
    public void cleanUp() {
        for (HandlerRegistration hr : eventHandlers) {
            eventBus.removeHandler(hr);
        }
    }

    @Override
    public List<List<String>> getGroupHierarchies(App app) {
        // Create list of group hierarchies
        List<List<String>> appGroupHierarchies = Lists.newArrayList();
        for(AppCategory appCategory : app.getGroups()) {
            appGroupHierarchies.add(computeGroupHierarchy(appCategory));
        }

        return appGroupHierarchies;
    }

    @Override
    public void onCreateNewAppSelected() {
        eventBus.fireEvent(new CreateNewAppEvent());
    }

    @Override
    public void onCreateNewWorkflowClicked() {
        eventBus.fireEvent(new CreateNewWorkflowEvent());
    }

    @Override
    public void onDeleteAppsSelected(final List<App> apps) {
        if (apps == null || apps.isEmpty()) {
            return;
        }
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
                    // Remove from visible list and update AppCategory app counts
                    listStore.remove(app);

                    // PSAR Always assume that the app is in the
                    // "Apps Under Development" group
                    AppCategory userAppGrp = findAppCategoryByName(USER_APPS_GROUP);
                    if (userAppGrp != null) {
                        updateAppCategoryAppCount(userAppGrp, userAppGrp.getAppCount() - 1);
                    }

                    eventBus.fireEvent(new AppDeleteEvent(app.getId()));
                }
            }
        });

    }

    @Override
    public void onEditAppSelected(App app) {
        if (app.getStepCount() > 1) {
            fetchWorkflowAndFireEditEvent(app);
        } else {
            boolean isAppPublished = app.isPublic();
            boolean isCurrentUserAppIntegrator = userInfo.getEmail().equals(app.getIntegratorEmail());

            eventBus.fireEvent(new EditAppEvent(app, isAppPublished && isCurrentUserAppIntegrator));
        }
    }

    @Override
    public Grid<App> getAppsGrid() {
        return view.getAppsGrid();
    }

    @Override
    public App getSelectedApp() {
        return view.getSelectedApp();
    }

    @Override
    public AppCategory getSelectedAppCategory() {
        return view.getSelectedAppCategory();
    }

    @Override
    public void go(final HasOneWidget container,
                   final HasId selectedAppCategory,
                   final HasId selectedApp) {
        container.setWidget(view);

        if (!treeStore.getAll().isEmpty()) {
            doInitialAppSelection(selectedAppCategory, selectedApp);
        } else {
            // Fetch AppCategories
            reloadAppCategories(selectedAppCategory, selectedApp);
        }
    }

    @Override
    public void go(final HasOneWidget container) {
        go(container, null, null);
    }

    @Override
    public AppsView.Presenter hideAppMenu() {
        view.hideAppMenu();
        return this;
    }

    @Override
    public AppsView.Presenter hideWorkflowMenu() {
        view.hideWorkflowMenu();
        return this;
    }

    @Override
    public void onAppCategorySelectionChanged(AppCategorySelectionChangedEvent event) {
        if (event.getAppCategorySelection().isEmpty()) {
            return;
        }
        AppCategory ag = event.getAppCategorySelection().get(0);
        fetchApps(ag);
        // FIXME move joiner stuff to appearance
        view.updateAppListHeading(Joiner.on(" >> ").join(computeGroupHierarchy(ag)));
    }

    @Override
    public void onAppCommentSelectedEvent(AppCommentSelectedEvent event) {
        final App app = event.getApp();
        commentsDialogProvider.get(new AsyncCallback<CommentsDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig("Something happened while trying to manage comments. Please try again or contact support for help."));
            }

            @Override
            public void onSuccess(CommentsDialog result) {
                result.show(app,
                            app.getIntegratorEmail().equals(userInfo.getEmail()),
                            metadataFacade);
            }
        });
    }

    @Override
    public void onAppFavoriteSelected(final AppFavoriteSelectedEvent event) {
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
                listStore.update(app);
                final AppCategory appCategoryByName = findAppCategoryByName(FAVORITES);
                if (appCategoryByName != null) {
                    int tmp = app.isFavorite() ? 1 : -1;

                    updateAppCategoryAppCount(appCategoryByName, appCategoryByName.getAppCount() + tmp);
                }
                final String selectedAppGrpName = getSelectedAppCategory().getName();

                /*
                 * If the app is in favorites, remove it.
                 * OR If we don't own the app, and the app is no longer a favorite, then remove it
                 */

                if (FAVORITES.equalsIgnoreCase(selectedAppGrpName)
                        || (WORKSPACE.equalsIgnoreCase(selectedAppGrpName) && !app.isFavorite() && app.isPublic() && !app.getIntegratorEmail().equals(userInfo.getEmail()))) {
                    listStore.remove(app);
                } else if (FAVORITES.equalsIgnoreCase(selectedAppGrpName)) {
                    listStore.remove(app);
                }
            }
        });
    }

    @Override
    public void onAppNameSelected(final AppNameSelectedEvent event) {
        App app = event.getSelectedApp();
        onRunAppSelected(app);
    }

    @Override
    public void onAppRatingDeselected(final AppRatingDeselected event) {
        Preconditions.checkNotNull(event.getApp());

        final App appToUnrate = event.getApp();
        appUserService.deleteRating(appToUnrate,
                                    new DeleteRatingCallback(appToUnrate,
                                                             listStore));
    }

    @Override
    public void onAppRatingSelected(final AppRatingSelected event) {
        Preconditions.checkNotNull(event.getSelectedApp());

        final App selectedApp = event.getSelectedApp();
        final int score = event.getScore();
        appUserService.rateApp(selectedApp,
                               event.getScore(),
                               new RateAppCallback(selectedApp,
                                                   score,
                                                   listStore));

    }

    @Override
    public void onCopyAppSelected(List<App> currentSelection) {
        Preconditions.checkState(currentSelection.size() == 1);
        appUserService.copyApp(currentSelection.iterator().next().getId(), new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(String result) {
                // Update the user's private apps group count.
                AppCategory usersAppsGrp = findAppCategoryByName(USER_APPS_GROUP);
                if (usersAppsGrp != null) {
                    updateAppCategoryAppCount(usersAppsGrp, usersAppsGrp.getAppCount() + 1);
                }
                HasId hasId = CommonModelUtils.getInstance().createHasIdFromString(StringQuoter.split(result).get("id").asString());
                if (!hasId.getId().isEmpty()) {
                    AppCategory selectedAppCategory = getSelectedAppCategory();
                    if (selectedAppCategory != null) {
                        fetchApps(selectedAppCategory);
                    }
                    eventBus.fireEvent(new EditAppEvent(hasId, false));
                }
            }
        });
    }

    @Override
    public void onCopyWorkFlowSelected(List<App> currentSelection) {
        Preconditions.checkState(currentSelection.size() == 1);
        final App app = currentSelection.iterator().next();
        appUserService.copyWorkflow(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(String result) {
                // Update the user's private apps group count.
                AppCategory userAppsGrp = findAppCategoryByName(USER_APPS_GROUP);
                if (userAppsGrp != null) {
                    updateAppCategoryAppCount(userAppsGrp, userAppsGrp.getAppCount() + 1);
                }

                // If the current app group is Workspace or the user's private apps, reload that group.
                AppCategory selectedAppCategory = getSelectedAppCategory();
                if (selectedAppCategory != null) {
                    String selectedGroupName = selectedAppCategory.getName();

                    if (selectedGroupName.equalsIgnoreCase(WORKSPACE) || selectedGroupName.equalsIgnoreCase(USER_APPS_GROUP)) {
                        fetchApps(selectedAppCategory);
                    }
                }

                // Fire an EditWorkflowEvent for the new workflow copy.
                Splittable serviceWorkflowJson = StringQuoter.split(result);
                eventBus.fireEvent(new EditWorkflowEvent(app, serviceWorkflowJson));
            }
        });
    }

    @Override
    public void onRequestToolClicked() {
        newToolRequestDialogProvider.get().show();
    }

    @Override
    public void onRunAppSelected(App app) {
        if (app.isRunnable()) {
            if (!app.isDisabled()) {
                eventBus.fireEvent(new RunAppEvent(app));
            }
        } else {
            announcer.schedule(new ErrorAnnouncementConfig(errorStrings.appLaunchWithoutToolError()));
        }
    }

    @Override
    public void setViewDebugId(String baseId) {
        view.asWidget().ensureDebugId(baseId);
    }

    /**
     * Retrieves the apps for the given group by updating and executing the list loader
     */
    protected void fetchApps(final AppCategory ag) {
        view.maskCenterPanel(displayStrings.loadingMask());
        appService.getApps(ag.getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof HttpRedirectException) {
                    com.google.gwt.user.client.Window.alert(displayStrings.agaveAuthRequiredMsg());
                } else {
                    ErrorHandler.post(errorStrings.retrieveAppListingFailed(), caught);
                }
                view.unMaskCenterPanel();
            }

            @Override
            public void onSuccess(String result) {
                // FIXME Update the service signature
                AppAutoBeanFactory factory = GWT.create(AppAutoBeanFactory.class);
                AutoBean<AppList> bean = AutoBeanCodex.decode(factory, AppList.class, result);
                List<App> apps = bean.as().getApps();

                listStore.clear();
                listStore.addAll(apps);

                if (getDesiredSelectedApp() != null) {
                    view.selectApp(getDesiredSelectedApp().getId());
                } else {
                    view.selectFirstApp();
                }
                setDesiredSelectedApp(null);
                view.unMaskCenterPanel();
                updateAppCategoryAppCount(ag, apps.size());
            }
        });
    }

    protected void reloadAppCategories(final HasId selectedAppCategory, final HasId selectedApp) {
        view.maskWestPanel(displayStrings.loadingMask());
        treeStore.clear();
        appCategoryProxy.load(null, new AsyncCallback<List<AppCategory>>() {
            @Override
            public void onFailure(Throwable caught) {
                // TODO Add error message for the user.
                ErrorHandler.post(caught);
                view.unMaskWestPanel();
            }

            @Override
            public void onSuccess(List<AppCategory> result) {
                addAppCategories(null, result);
                view.expandAppCategories();
                doInitialAppSelection(selectedAppCategory, selectedApp);
                view.unMaskWestPanel();
            }
        });
    }

    void addAppCategories(AppCategory parent, List<AppCategory> children) {
        if ((children == null) || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (AppCategory ag : children) {
            addAppCategories(ag, ag.getCategories());
        }
    }

    List<String> computeGroupHierarchy(final AppCategory ag) {
        List<String> groupNames = Lists.newArrayList();

        for (AppCategory group : getGroupHierarchy(ag, null)) {
            groupNames.add(group.getName());
        }
        Collections.reverse(groupNames);
        return groupNames;
    }

    AppCategory findAppCategoryByName(String name) {
        for (AppCategory appCategory : treeStore.getAll()) {
            if (appCategory.getName().equalsIgnoreCase(name)) {
                return appCategory;
            }
        }

        return null;
    }

    List<AppCategory> getGroupHierarchy(AppCategory grp, List<AppCategory> groups) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        groups.add(grp);
        for (AppCategory ap : treeStore.getRootItems()) {
            LOG.fine(ap.getName());
            if (ap.getId().equals(grp.getId())) {
                return groups;
            }
        }
        return getGroupHierarchy(treeStore.getParent(grp), groups);
    }

    void updateAppCategoryAppCount(AppCategory appGroup, int newCount) {
        int difference = appGroup.getAppCount() - newCount;

        while (appGroup != null) {
            appGroup.setAppCount(appGroup.getAppCount() - difference);
            treeStore.update(appGroup);
            appGroup = treeStore.getParent(appGroup);
        }
    }

    private void doInitialAppSelection(HasId selectedAppCategory, HasId selectedApp) {
        // Select previous user selections
        if (selectedAppCategory != null) {
            view.selectAppCategory(selectedAppCategory);
            setDesiredSelectedApp(selectedApp);
        } else {
            view.selectFirstAppCategory();
        }

    }

    private void fetchWorkflowAndFireEditEvent(final App app) {
        appUserService.editWorkflow(app.getId(), new AsyncCallback<String>() {

            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(errorStrings.failToRetrieveApp(), caught);
                announcer.schedule(new ErrorAnnouncementConfig(errorStrings.failToRetrieveApp()));
            }

            @Override
            public void onSuccess(String result) {
                Splittable serviceWorkflowJson = StringQuoter.split(result);
                eventBus.fireEvent(new EditWorkflowEvent(app, serviceWorkflowJson));
            }
        });
    }

    private HasId getDesiredSelectedApp() {
        return desiredSelectedAppId;
    }

    private void initConstants() {
        WORKSPACE = props.getPrivateWorkspace();

        if (props.getPrivateWorkspaceItems() != null) {
            JSONArray items = JSONParser.parseStrict(props.getPrivateWorkspaceItems()).isArray();
            USER_APPS_GROUP = jsonUtil.getRawValueAsString(items.get(0));
            FAVORITES = jsonUtil.getRawValueAsString(items.get(1));
        }
    }

    /**
     * Sets a string which is a place holder for selection after a call to {@link #fetchApps(org.iplantc.de.client.models.apps.AppCategory)}
     */
    private void setDesiredSelectedApp(HasId selectedApp) {
        this.desiredSelectedAppId = selectedApp;
    }
}
