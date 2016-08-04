package org.iplantc.de.apps.client.presenter.hierarchies;

import static com.google.common.base.Preconditions.checkNotNull;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.apps.client.events.AppFavoritedEvent;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.AppUpdatedEvent;
import org.iplantc.de.apps.client.events.selection.AppFavoriteSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppInfoSelectedEvent;
import org.iplantc.de.apps.client.events.selection.AppRatingDeselected;
import org.iplantc.de.apps.client.events.selection.AppRatingSelected;
import org.iplantc.de.apps.client.events.selection.DetailsCategoryClicked;
import org.iplantc.de.apps.client.events.selection.DetailsHierarchyClicked;
import org.iplantc.de.apps.client.events.selection.OntologyHierarchySelectionChangedEvent;
import org.iplantc.de.apps.client.gin.AppCategoryTreeStoreProvider;
import org.iplantc.de.apps.client.gin.OntologyHierarchyTreeStoreProvider;
import org.iplantc.de.apps.client.gin.factory.OntologyHierarchiesViewFactory;
import org.iplantc.de.apps.client.presenter.callbacks.DeleteRatingCallback;
import org.iplantc.de.apps.client.presenter.callbacks.RateAppCallback;
import org.iplantc.de.apps.client.views.details.dialogs.AppDetailsDialog;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.AppCategory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.services.AppUserServiceFacade;
import org.iplantc.de.client.services.OntologyServiceFacade;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.widgets.DETabPanel;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.List;
import java.util.Map;

/**
 * @author aramsey
 */
public class OntologyHierarchiesPresenterImpl implements OntologyHierarchiesView.Presenter,
                                                         OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler,
                                                         AppRatingSelected.AppRatingSelectedHandler,
                                                         AppRatingDeselected.AppRatingDeselectedHandler,
                                                         AppFavoriteSelectedEvent.AppFavoriteSelectedEventHandler,
                                                         DetailsHierarchyClicked.DetailsHierarchyClickedHandler,
                                                         DetailsCategoryClicked.DetailsCategoryClickedHandler {

    class AppDetailsCallback implements AsyncCallback<App> {

        private final AppDetailsDialog dlg;
        private App app;

        public AppDetailsCallback(App app, AppDetailsDialog dlg) {
            this.app = app;
            this.dlg = dlg;
        }

        @Override
        public void onFailure(final Throwable caught) {
            announcer.schedule(new ErrorAnnouncementConfig(appearance.fetchAppDetailsError(caught)));
        }

        @Override
        public void onSuccess(final App appDetails) {
            TreeStore<OntologyHierarchy> hierarchyTreeStore = getHierarchyTreeStore();
            TreeStore<AppCategory> categoryTreeStore = getCategoryTreeStore();

            if (app.getAppType().equalsIgnoreCase(App.EXTERNAL_APP)) {
                categoryTreeStore.add(appDetails.getGroups());
            }

            List<OntologyHierarchy> hierarchies = appDetails.getHierarchies();
            if (hierarchies != null && hierarchies.size() == 0) {
                hierarchies = unclassifiedHierarchies;
            }
            addHierarchies(hierarchyTreeStore, null, hierarchies);

            dlg.show(appDetails,
                     searchRegexPattern,
                     hierarchyTreeStore,
                     categoryTreeStore,
                     OntologyHierarchiesPresenterImpl.this,
                     OntologyHierarchiesPresenterImpl.this,
                     OntologyHierarchiesPresenterImpl.this,
                     OntologyHierarchiesPresenterImpl.this,
                     OntologyHierarchiesPresenterImpl.this);
        }
    }

    @Inject IplantAnnouncer announcer;
    OntologyUtil ontologyUtil;
    @Inject AsyncProvider<AppDetailsDialog> appDetailsDlgAsyncProvider;
    @Inject AppUserServiceFacade appUserService;
    DETabPanel viewTabPanel;
    private OntologyServiceFacade serviceFacade;
    private OntologyHierarchiesView.OntologyHierarchiesAppearance appearance;
    protected String searchRegexPattern;
    private final EventBus eventBus;
    HandlerManager handlerManager;
    Map<String, List<OntologyHierarchy>> iriToHierarchyMap = new FastMap<>();
    private OntologyHierarchiesViewFactory viewFactory;
    String baseID;
    List<OntologyHierarchy> unclassifiedHierarchies = Lists.newArrayList();


    @Inject
    public OntologyHierarchiesPresenterImpl(OntologyHierarchiesViewFactory viewFactory,
                                            OntologyServiceFacade serviceFacade,
                                            EventBus eventBus,
                                            OntologyHierarchiesView.OntologyHierarchiesAppearance appearance) {

        this.serviceFacade = serviceFacade;
        this.appearance = appearance;
        this.viewFactory = viewFactory;
        this.eventBus = eventBus;

        this.ontologyUtil = OntologyUtil.getInstance();
    }

    @Override
    public void go(final DETabPanel tabPanel) {
        viewTabPanel = tabPanel;
        serviceFacade.getRootHierarchies(new AsyncCallback<List<OntologyHierarchy>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<OntologyHierarchy> result) {
                if (result != null && result.size() > 0) {
                    createViewTabs(result);
                }
            }
        });
    }

    @Override
    public void setViewDebugId(String baseID) {
        this.baseID = baseID;
    }

    @Override
    public void onAppInfoSelected(final AppInfoSelectedEvent event) {
        appDetailsDlgAsyncProvider.get(new AsyncCallback<AppDetailsDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(final AppDetailsDialog dlg) {
                App app = event.getApp();
                appUserService.getAppDetails(app,
                                             new AppDetailsCallback(app, dlg));
            }
        });
    }

    void createViewTabs(List<OntologyHierarchy> results) {
        for (OntologyHierarchy hierarchy : results) {
            TreeStore<OntologyHierarchy> treeStore = getHierarchyTreeStore();
            OntologyHierarchiesView view = viewFactory.create(treeStore);
            Tree<OntologyHierarchy, String> tree = view.getTree();

            tree.mask(appearance.getAppCategoriesLoadingMask());
            getFilteredHierarchies(hierarchy, tree);
            String hierarchyDebugId = baseID + "." + hierarchy.getIri();
            view.asWidget().ensureDebugId(hierarchyDebugId);
            view.addOntologyHierarchySelectionChangedEventHandler(this);
            //As a preference, insert the hierarchy tabs before the HPC tab which is last
            viewTabPanel.insert(tree, viewTabPanel.getWidgetCount() - 1, new TabItemConfig(appearance.hierarchyLabelName(hierarchy)), hierarchyDebugId);
        }
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        searchRegexPattern = event.getSearchPattern();
        for (Widget widget : viewTabPanel) {
            if (widget instanceof Tree) {
                ((Tree)widget).getSelectionModel().deselectAll();
            }
        }
    }

    TreeStore<OntologyHierarchy> getHierarchyTreeStore() {
        TreeStore<OntologyHierarchy> treeStore = new OntologyHierarchyTreeStoreProvider().get();
        treeStore.addSortInfo(new Store.StoreSortInfo<>(ontologyUtil.getOntologyNameComparator(),
                                                        SortDir.ASC));
        return treeStore;
    }

    TreeStore<AppCategory> getCategoryTreeStore() {
        return new AppCategoryTreeStoreProvider().get();
    }

    void getFilteredHierarchies(final OntologyHierarchy root, final Tree<OntologyHierarchy, String> tree) {
        serviceFacade.getFilteredHierarchies(root.getIri(),
                                             ontologyUtil.convertHierarchyToAvu(root),
                                             new AsyncCallback<OntologyHierarchy>() {
                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     ErrorHandler.post(caught);
                                                     tree.unmask();
                                                 }

                                                 @Override
                                                 public void onSuccess(OntologyHierarchy result) {
                                                     if (result == null || result.getSubclasses() == null) {
                                                         result = root;
                                                         result.setSubclasses(Lists.<OntologyHierarchy>newArrayList());
                                                     }
                                                     OntologyHierarchy unclassifiedChild =
                                                             ontologyUtil.addUnclassifiedChild(result);
                                                     unclassifiedHierarchies.add(unclassifiedChild);
                                                     //Set the key for the current root (which won't appear in the tree, but will be the name of the tab)
                                                     // which will allow the children to know the full path from its parent to node
                                                     ontologyUtil.getOrCreateHierarchyPathTag(result);
                                                     addHierarchies(tree.getStore(), null, result.getSubclasses());
                                                     tree.unmask();
                                                 }
                                             });
    }

    void addHierarchies(TreeStore<OntologyHierarchy> treeStore, OntologyHierarchy parent, List<OntologyHierarchy> children) {
        if ((children == null)
            || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            treeStore.add(children);

        } else {
            treeStore.add(parent, children);
        }

        helperMap(children);

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(treeStore, hierarchy, hierarchy.getSubclasses());
        }
    }

    void helperMap(List<OntologyHierarchy> children) {
        for (OntologyHierarchy hierarchy : children) {
            String iri = hierarchy.getIri();
            List<OntologyHierarchy> hierarchies = iriToHierarchyMap.get(iri);
            if (hierarchies == null) {
                hierarchies = Lists.newArrayList();
            }
            hierarchies.add(hierarchy);
            iriToHierarchyMap.put(hierarchy.getIri(), hierarchies);
        }
    }

    @Override
    public void onOntologyHierarchySelectionChanged(OntologyHierarchySelectionChangedEvent event) {
        fireEvent(event);
    }

    @Override
    public void onAppRatingDeselected(AppRatingDeselected event) {
        final App appToUnRate = event.getApp();
        appUserService.deleteRating(appToUnRate, new DeleteRatingCallback(appToUnRate,
                                                                          eventBus));
    }

    @Override
    public void onAppRatingSelected(AppRatingSelected event) {
        final App appToRate = event.getApp();
        appUserService.rateApp(appToRate,
                               event.getScore(),
                               new RateAppCallback(appToRate,
                                                   eventBus));
    }

    @Override
    public void onAppFavoriteSelected(AppFavoriteSelectedEvent event) {
        final App app = event.getApp();
        appUserService.favoriteApp(app, !app.isFavorite(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                announcer.schedule(new ErrorAnnouncementConfig(appearance.favServiceFailure()));
            }

            @Override
            public void onSuccess(Void result) {
                app.setFavorite(!app.isFavorite());
                // Have to fire global events.
                eventBus.fireEvent(new AppFavoritedEvent(app));
                eventBus.fireEvent(new AppUpdatedEvent(app));
            }
        });
    }

    @Override
    public void onDetailsHierarchyClicked(DetailsHierarchyClicked event) {
        OntologyHierarchy hierarchy = event.getHierarchy();
        checkNotNull(hierarchy);

        String id = ontologyUtil.getOrCreateHierarchyPathTag(hierarchy);
        for (int i = 0; i < viewTabPanel.getWidgetCount(); i++) {
            Tree<OntologyHierarchy, String> tree = ((Tree)viewTabPanel.getWidget(i));
            OntologyHierarchy selectedHierarchy = tree.getStore().findModelWithKey(id);
            if (selectedHierarchy != null) {
                viewTabPanel.setActiveWidget(tree);
                tree.scrollIntoView(selectedHierarchy);
                tree.getSelectionModel().select(selectedHierarchy, true);
                break;
            }
        }
    }

    @Override
    public void onDetailsCategoryClicked(DetailsCategoryClicked event) {
        AppCategory category = event.getCategory();
        checkNotNull(category);

        for (int i = 0 ; i < viewTabPanel.getWidgetCount() ; i++) {
            Tree<AppCategory, String> tree = ((Tree)viewTabPanel.getWidget(i));
            AppCategory selectedCategory = tree.getStore().findModelWithKey(category.getId());
            if (selectedCategory != null) {
                viewTabPanel.setActiveWidget(tree);
                tree.scrollIntoView(selectedCategory);
                tree.getSelectionModel().select(selectedCategory, true);
                break;
            }
        }
    }

    @Override
    public HandlerRegistration addOntologyHierarchySelectionChangedEventHandler(
            OntologyHierarchySelectionChangedEvent.OntologyHierarchySelectionChangedEventHandler handler) {
        return ensureHandlers().addHandler(OntologyHierarchySelectionChangedEvent.TYPE, handler);
    }

    HandlerManager createHandlerManager() {
        return new HandlerManager(this);
    }

    HandlerManager ensureHandlers() {
        return handlerManager == null ? handlerManager = createHandlerManager() : handlerManager;
    }

    void fireEvent(GwtEvent<?> event) {
        if (handlerManager != null) {
            handlerManager.fireEvent(event);
        }
    }
}
