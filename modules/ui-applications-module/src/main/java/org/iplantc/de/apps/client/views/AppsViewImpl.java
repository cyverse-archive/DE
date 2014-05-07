package org.iplantc.de.apps.client.views;

import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppGroupSelectionChangedEvent;
import org.iplantc.de.apps.client.events.AppSelectionChangedEvent;
import org.iplantc.de.apps.client.views.cells.AppFavoriteCell;
import org.iplantc.de.apps.client.views.cells.AppInfoCell;
import org.iplantc.de.apps.client.views.dialogs.SubmitAppForPublicDialog;
import org.iplantc.de.apps.client.views.widgets.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.shared.AppsModule.Ids;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.IsMaskable;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppGroup;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;

import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.theme.gray.client.panel.GrayContentPanelAppearance;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.tips.QuickTip;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeAppearance;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.tree.TreeStyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author jstroot
 *
 */
public class AppsViewImpl extends Composite implements AppsView,
                                                       IsMaskable,
                                                       AppGroupSelectionChangedEvent.HasAppGroupSelectionChangedEventHandlers,
                                                       AppSelectionChangedEvent.HasAppSelectionChangedEventHandlers,
                                                       AppInfoCell.AppInfoClickedEventHandler,
                                                       AppFavoritedEvent.HasAppFavoritedEventHandlers,
                                                       AppFavoriteCell.RequestAppFavoriteEventHandler {
    /**
     * FIXME CORE-2992: Add an ID to the Categories panel collapse tool to assist QA.
     */
    private static String WEST_COLLAPSE_BTN_ID = "idCategoryCollapseBtn"; //$NON-NLS-1$
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiTemplate("AppsView.ui.xml")
    interface MyUiBinder extends UiBinder<Widget, AppsViewImpl> { }

    private String FAVORITES;
    private String USER_APPS_GROUP;
    private String WORKSPACE;

    protected Presenter presenter;

    @UiField(provided = true)
    protected Tree<AppGroup, String> tree;

    @UiField(provided = true)
    TreeStore<AppGroup> treeStore;

    @UiField
    protected Grid<App> grid;

    @UiField
    GridView<App> gridView;

    @UiField
    ListStore<App> listStore;

    @UiField
    protected ColumnModel<App> cm;

    @UiField
    BorderLayoutContainer con;

    @UiField
    ContentPanel westPanel;
    @UiField
    ContentPanel centerPanel;
    @UiField
    ContentPanel eastPanel;

    @UiField
    BorderLayoutData northData;
    @UiField
    BorderLayoutData eastData;

    final DEProperties properties;
    private final AppsView.ViewMenu toolbar;
    private final IplantResources resources;
    private final UserInfo userInfo;
    private final IplantDisplayStrings displayStrings;
    private final AppUserServiceFacade appUserService;

    Logger logger = Logger.getLogger("App View");

    @Inject
    public AppsViewImpl(final Tree<AppGroup, String> tree,
                        final DEProperties properties,
                        final AppsView.ViewMenu toolbar,
                        final IplantResources resources,
                        final UserInfo userInfo,
                        final IplantDisplayStrings displayStrings,
                        final AppUserServiceFacade appUserService) {
        this.tree = tree;
        this.properties = properties;
        this.toolbar = toolbar;
        this.resources = resources;
        this.userInfo = userInfo;
        this.displayStrings = displayStrings;
        this.appUserService = appUserService;
        this.treeStore = tree.getStore();
        initWidget(uiBinder.createAndBindUi(this));
        setNorthWidget(toolbar);
        initConstants();

        this.tree.getSelectionModel().setSelectionMode(SINGLE);
        initTreeStoreSorter();


        grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<App>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<App> event) {
                asWidget().fireEvent(new AppSelectionChangedEvent(event.getSelection()));
            }
        });

        tree.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<AppGroup>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<AppGroup> event) {
                updateCenterPanelHeading(event.getSelection());
                asWidget().fireEvent(new AppGroupSelectionChangedEvent(event.getSelection()));
            }
        });
        setTreeIcons();
        new QuickTip(grid).getToolTipConfig().setTrackMouse(true);
        westPanel.getHeader().getTool(0).getElement().setId(WEST_COLLAPSE_BTN_ID);

    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolbar.asWidget().ensureDebugId(baseID + Ids.MENU_BAR);
        tree.ensureDebugId(baseID + Ids.CATEGORIES);
        grid.ensureDebugId(baseID + Ids.APP_GRID);
        ((AppColumnModel)cm).ensureDebugId(baseID);
    }

    @Override
    public HandlerRegistration addAppFavoritedEventHandler(AppFavoritedEvent.AppFavoritedEventHandler eventHandler) {
        return asWidget().addHandler(eventHandler, AppFavoritedEvent.TYPE);
    }

    @Override
    public void onAppFavoriteRequest(AppFavoriteCell.RequestAppFavoriteEvent event) {
        presenter.onAppFavoriteRequest(event);
    }

    @UiFactory
    protected ColumnModel<App> createColumnModel(){
        return new AppColumnModel(this, displayStrings);
    }

    private void initConstants() {
        WORKSPACE = properties.getPrivateWorkspace();

        if (properties.getPrivateWorkspaceItems() != null) {
            JSONArray items = JSONParser.parseStrict(properties.getPrivateWorkspaceItems()).isArray();
            USER_APPS_GROUP = JsonUtil.getRawValueAsString(items.get(0));
            FAVORITES = JsonUtil.getRawValueAsString(items.get(1));
        }

    }

    @Override
    public void onAppFavorited(AppFavoritedEvent event) {
        final App app = event.getApp();
        grid.getStore().update(app);
        final AppGroup appGroupByName = findAppGroupByName(FAVORITES);
        if(appGroupByName != null){
            int tmp = app.isFavorite() ? 1 : -1;

            updateAppGroupAppCount(appGroupByName, appGroupByName.getAppCount() + tmp);
        }
        final String selectedAppGrpName = getSelectedAppGroup().getName();

        /* If the app is in favorites, remove it.
         * OR If we don't own the app, and the app is no longer a favorite, then remove it
         */
        if(FAVORITES.equalsIgnoreCase(selectedAppGrpName)
            || (WORKSPACE.equalsIgnoreCase(selectedAppGrpName)
                   && !app.isFavorite()
                   && app.isPublic()
                   && !app.getIntegratorEmail().equals(userInfo.getEmail()))){
            removeApp(app);
        }else if(FAVORITES.equalsIgnoreCase(selectedAppGrpName)){
            removeApp(app);
        }
        // Forward event so the App Info window can get it if it is open
        asWidget().fireEvent(event);
    }

    @Override
    public void onAppInfoClicked(AppInfoCell.AppInfoClickedEvent event) {
        final App selectedApp = grid.getSelectionModel().getSelectedItem();
        Dialog appInfoWin = new Dialog();
        appInfoWin.setModal(true);
        appInfoWin.setResizable(false);
        appInfoWin.setHeadingText(selectedApp.getName());
        appInfoWin.setPixelSize(450, 300);
        // Get app favorite requests
        final AppInfoView appInfoView = new AppInfoView(selectedApp, this, appUserService);
        appInfoView.addRequestAppFavoriteEventHandlers(this);
        addAppFavoritedEventHandler(appInfoView);
        appInfoWin.add(appInfoView);
        appInfoWin.getButtonBar().clear();
        appInfoWin.show();
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        selectAppGroup(null);
        presenter.onAppSearchResultLoad(event);
        String searchText = event.getSearchText();

        List<App> results = event.getResults();
        int total = results == null ?0 : results.size();

        centerPanel.setHeadingText(displayStrings.searchAppResultsHeader(searchText, total));
        setApps(results);
        unMaskCenterPanel();
    }

    void updateCenterPanelHeading(List<AppGroup> selection) {
        if(selection.isEmpty())
            return;

        checkArgument(selection.size() == 1, "Only one app group should be selected");
        centerPanel.setHeadingText(Joiner.on(" >> ").join(computeGroupHierarchy(selection.get(0))));
    }

    @Override
    public HandlerRegistration addAppGroupSelectedEventHandler(AppGroupSelectionChangedEvent.AppGroupSelectionChangedEventHandler handler) {
        return asWidget().addHandler(handler, AppGroupSelectionChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addAppSelectionChangedEventHandler(AppSelectionChangedEvent.AppSelectionChangedEventHandler handler) {
        return asWidget().addHandler(handler, AppSelectionChangedEvent.TYPE);
    }

    @UiFactory
    ContentPanel createContentPanel() {
        // FIXME JDS This violates goal of theming. Implement proper theming/appearance.
        return new ContentPanel(new GrayContentPanelAppearance());
    }

    @UiFactory
    ListStore<App> createListStore() {
        return new ListStore<App>(new ModelKeyProvider<App>() {
            @Override
            public String getKey(App item) {
                return item.getId();
            }

        });
    }

    /**
     * FIXME JDS This needs to be implemented in an {@link TreeAppearance}
     */
    private void setTreeIcons() {
        TreeStyle style = tree.getStyle();
        style.setNodeCloseIcon(resources.category());
        style.setNodeOpenIcon(resources.category_open());
        style.setLeafIcon(resources.subCategory());
    }

    private void initTreeStoreSorter() {

        Comparator<AppGroup> comparator = new Comparator<AppGroup>() {

            @Override
            public int compare(AppGroup group1, AppGroup group2) {
                if (treeStore.getRootItems().contains(group1)
                            || treeStore.getRootItems().contains(group2)) {
                    // Do not sort Root groups, since we want to keep the service's root order.
                    return 0;
                }

                return group1.getName().compareToIgnoreCase(group2.getName());
            }
        };

        treeStore.addSortInfo(new StoreSortInfo<AppGroup>(comparator, SortDir.ASC));
    }

    @Override
    public void hideAppMenu() {
        toolbar.hideAppMenu();
    }

    @Override
    public void hideWorkflowMenu() {
        toolbar.hideWorkflowMenu();
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        AppColumnModel appColModel = (AppColumnModel)cm;
        appColModel.addAppInfoClickedEventHandler(this);
        appColModel.addAppNameSelectedEventHandler(presenter);
        appColModel.addRequestAppFavoriteEventHandlers(this);
        appColModel.addAppCommentSelectedEventHandlers(presenter);
        addAppGroupSelectedEventHandler(presenter);
        this.toolbar.init(presenter, this, this, this);
    }

    @Override
    public void maskCenterPanel(final String loadingMask) {
        centerPanel.mask(loadingMask);
    }

    @Override
    public void submitSelectedApp() {
        App selectedApp = grid.getSelectionModel().getSelectedItem();
        new SubmitAppForPublicDialog(selectedApp).show();
    }

    @Override
    public void unMaskCenterPanel() {
        centerPanel.unmask();
    }

    @Override
    public void maskWestPanel(String loadingMask) {
        westPanel.mask(loadingMask);
    }

    @Override
    public void unMaskWestPanel() {
        westPanel.unmask();
    }

    @Override
    public void selectApp(String appId) {
        App app = listStore.findModelWithKey(appId);
        if (app != null) {
            grid.getSelectionModel().select(app, false);
        }
    }

    @Override
    public void selectAppGroup(String appGroupId) {
        if (Strings.isNullOrEmpty(appGroupId)) {
            tree.getSelectionModel().deselectAll();
        } else {
            AppGroup ag = treeStore.findModelWithKey(appGroupId);
            if (ag != null) {
                tree.getSelectionModel().select(ag, false);
                tree.scrollIntoView(ag);
            }
        }
    }

    @Override
    public List<String> computeGroupHierarchy(final AppGroup ag) {
        List<String> groupNames = Lists.newArrayList();

        for (AppGroup group : getGroupHierarchy(ag, null)) {
            groupNames.add(group.getName());
        }
        Collections.reverse(groupNames);
        return groupNames;
    }

    @Override
    public App getSelectedApp() {
        return grid.getSelectionModel().getSelectedItem();
    }

    @Override

    public List<App> getAllSelectedApps() {
        return grid.getSelectionModel().getSelectedItems();
    }

    @Override
    public AppGroup getSelectedAppGroup() {
        return tree.getSelectionModel().getSelectedItem();
    }

    @Override
    public void setApps(final List<App> apps) {
        listStore.clear();

        for (App app : apps) {
            if (listStore.findModel(app) == null) {
                listStore.add(app);
            }
        }
    }


    protected void setNorthWidget(IsWidget widget) {
        northData.setHidden(false);
        con.setNorthWidget(widget, northData);
    }

    @Override
    public void selectFirstApp() {
        grid.getSelectionModel().select(0, false);
    }

    @Override
    public void selectFirstAppGroup() {
        AppGroup ag = treeStore.getRootItems().get(0);
        tree.getSelectionModel().select(ag, false);
        tree.scrollIntoView(ag);
    }

    @Override
    public void addAppGroup(AppGroup parent, AppGroup child) {
        if (child == null) {
            return;
        }

        if (parent == null) {
            treeStore.add(child);
        } else {
            treeStore.add(parent, child);
        }
    }

    @Override
    public void addAppGroups(AppGroup parent, List<AppGroup> children) {
        if ((children == null) || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (AppGroup ag : children) {
            addAppGroups(ag, ag.getGroups());
        }
    }

    @Override
    public void removeApp(App app) {
        grid.getSelectionModel().deselectAll();
        listStore.remove(app);
    }

    protected void deSelectAllAppGroups() {
        tree.getSelectionModel().deselectAll();
    }

    @Override
    public void updateAppGroup(AppGroup appGroup) {
        treeStore.update(appGroup);
    }

    @Override
    public AppGroup findAppGroupByName(String name) {
        for (AppGroup appGroup : treeStore.getAll()) {
            if (appGroup.getName().equalsIgnoreCase(name)) {
                return appGroup;
            }
        }

        return null;
    }

    @Override
    public void updateAppGroupAppCount(AppGroup appGroup, int newCount) {
        int difference = appGroup.getAppCount() - newCount;

        while (appGroup != null) {
            appGroup.setAppCount(appGroup.getAppCount() - difference);
            updateAppGroup(appGroup);
            appGroup = treeStore.getParent(appGroup);
        }

    }

    protected App findApp(String appId) {
        return listStore.findModelWithKey(appId);
    }

    @Override
    public Grid<App> getAppsGrid() {
        return grid;
    }

    @Override
    public void expandAppGroups() {
        tree.expandAll();
    }

    @Override
    public boolean isTreeStoreEmpty() {
        return treeStore.getAll().isEmpty();
    }

    @Override
    public void clearAppGroups() {
        treeStore.clear();
    }

    @Override
    public AppGroup getAppGroupFromElement(Element el) {
        TreeNode<AppGroup> node = tree.findNode(el);
        if (node != null && tree.getView().isSelectableTarget(node.getModel(), el)) {
            return node.getModel();
        }

        return null;
    }

    @Override
    public App getAppFromElement(Element el) {
        Element row = gridView.findRow(el);
        int index = gridView.findRowIndex(row);
        return listStore.get(index);
    }

    @Override
    public String highlightSearchText(String text) {
        return presenter.highlightSearchText(text);
    }

    @Override
    public List<AppGroup> getAppGroupRoots() {
        return treeStore.getRootItems();
    }

    @Override
    public AppGroup getParent(AppGroup child) {
        return treeStore.getParent(child);
    }

    List<AppGroup> getGroupHierarchy(AppGroup grp, List<AppGroup> groups) {
        if (groups == null) {
            groups = new ArrayList<AppGroup>();
        }
        groups.add(grp);
        if (treeStore.getRootItems().contains(grp)) {
            return groups;
        } else {
            return getGroupHierarchy(treeStore.getParent(grp), groups);
        }
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(displayStrings.loadingMask());
    }

    @Override
    public void unmask() {
        con.unmask();
    }
}
