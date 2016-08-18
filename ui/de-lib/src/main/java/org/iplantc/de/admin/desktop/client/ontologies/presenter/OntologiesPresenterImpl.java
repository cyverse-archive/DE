package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeHierarchiesToAppEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.DeleteOntologyButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PreviewHierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RefreshOntologiesEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RefreshPreviewButtonClicked;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppToOntologyHierarchyDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyHierarchyToAppDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.CategorizeDialog;
import org.iplantc.de.admin.desktop.client.services.AppAdminServiceFacade;
import org.iplantc.de.apps.client.events.AppSearchResultLoadEvent;
import org.iplantc.de.apps.client.events.selection.DeleteAppsSelected;
import org.iplantc.de.apps.client.presenter.toolBar.proxy.AppSearchRpcProxy;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.services.AppServiceFacade;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;
import org.iplantc.de.shared.DEProperties;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author aramsey
 */
public class OntologiesPresenterImpl implements OntologiesView.Presenter,
                                                RefreshOntologiesEvent.RefreshOntologiesEventHandler,
                                                SelectOntologyVersionEvent.SelectOntologyVersionEventHandler,
                                                SaveOntologyHierarchyEvent.SaveOntologyHierarchyEventHandler,
                                                PublishOntologyClickEvent.PublishOntologyClickEventHandler,
                                                HierarchySelectedEvent.HierarchySelectedEventHandler,
                                                PreviewHierarchySelectedEvent.PreviewHierarchySelectedEventHandler,
                                                CategorizeButtonClickedEvent.CategorizeButtonClickedEventHandler,
                                                DeleteOntologyButtonClickedEvent.DeleteOntologyButtonClickedEventHandler,
                                                DeleteHierarchyEvent.DeleteHierarchyEventHandler,
                                                DeleteAppsSelected.DeleteAppsSelectedHandler,
                                                RefreshPreviewButtonClicked.RefreshPreviewButtonClickedHandler,
                                                AppSearchResultLoadEvent.AppSearchResultLoadEventHandler {

    class CategorizeCallback implements AsyncCallback<List<Avu>> {
        private final App selectedApp;
        private final List<OntologyHierarchy> hierarchyRoots;

        public CategorizeCallback(App selectedApp,
                                  List<OntologyHierarchy> hierarchyRoots) {
            this.selectedApp = selectedApp;
            this.hierarchyRoots = hierarchyRoots;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
        }

        @Override
        public void onSuccess(List<Avu> result) {
            CategorizeDialog dlg = new CategorizeDialog(appearance,
                                                        selectedApp, categorizeView,
                                                        hierarchyRoots, iriToHierarchyMap, result);
            dlg.addCategorizeHierarchiesToAppEventHandler(new CategorizeHierarchiesToAppEvent.CategorizeHierarchiesToAppEventHandler() {
                @Override
                public void onCategorizeHierarchiesToApp(CategorizeHierarchiesToAppEvent event) {
                    if (event.getSelectedHierarchies() == null || event.getSelectedHierarchies().size() == 0) {
                        clearAvus(event.getTargetApp());
                    } else {
                        categorizeHierarchiesToApp(event);
                    }

                }
            });
        }
    }

    class SaveHierarchyAsyncCallback implements AsyncCallback<OntologyHierarchy> {
        private final String iri;
        private final String ontologyVersion;

        public SaveHierarchyAsyncCallback(String ontologyVersion, String iri) {
            this.ontologyVersion = ontologyVersion;
            this.iri = iri;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            view.unmaskTree(OntologiesView.TreeType.EDITOR);
        }

        @Override
        public void onSuccess(OntologyHierarchy result) {
            if (isValidHierarchy(result)) {
                serviceFacade.getOntologyHierarchies(ontologyVersion, new HierarchiesCallback(ontologyVersion));
            } else {
                announcer.schedule(new ErrorAnnouncementConfig(
                        appearance.invalidHierarchySubmitted(iri)));
                view.unmaskTree(OntologiesView.TreeType.EDITOR);
            }
        }
    }

    class HierarchiesCallback implements AsyncCallback<List<OntologyHierarchy>> {

        final String version;

        public HierarchiesCallback(String version) {
            this.version = version;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            view.unmaskTree(OntologiesView.TreeType.EDITOR);
        }

        @Override
        public void onSuccess(List<OntologyHierarchy> result) {
            view.clearTreeStore(OntologiesView.TreeType.EDITOR);
            if (result.size() == 0) {
                view.showEmptyTreePanel();
            } else {
                boolean isValid = ontologyUtil.createIriToAttrMap(result);
                if (!isValid) {
                    displayErrorToAdmin();
                }
                view.maskTree(OntologiesView.TreeType.EDITOR);
                addHierarchies(OntologiesView.TreeType.EDITOR, null, result);
                getFilteredOntologyHierarchies(version, result);
            }
            view.unmaskTree(OntologiesView.TreeType.EDITOR);
            view.updateButtonStatus();
        }
    }

    @Inject AppAdminServiceFacade adminAppService;
    @Inject DEProperties properties;
    @Inject IplantAnnouncer announcer;
    OntologyUtil ontologyUtil;
    AppSearchRpcProxy proxy;
    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader;
    private OntologiesView view;
    private OntologyServiceFacade serviceFacade;
    private final TreeStore<OntologyHierarchy> editorTreeStore;
    private final TreeStore<OntologyHierarchy> previewTreeStore;
    private OntologiesView.OntologiesViewAppearance appearance;
    private AdminAppsGridView.Presenter previewGridPresenter;
    private AdminAppsGridView.Presenter editorGridPresenter;
    private AppCategorizeView categorizeView;
    private AvuAutoBeanFactory avuFactory;
    private Map<String, List<OntologyHierarchy>> iriToHierarchyMap = new FastMap<>();

    @Inject
    public OntologiesPresenterImpl(OntologyServiceFacade serviceFacade,
                                   AppServiceFacade appService,
                                   final TreeStore<OntologyHierarchy> editorTreeStore,
                                   final TreeStore<OntologyHierarchy> previewTreeStore,
                                   OntologiesViewFactory factory,
                                   AvuAutoBeanFactory avuFactory,
                                   OntologiesView.OntologiesViewAppearance appearance,
                                   AdminAppsGridView.Presenter previewGridPresenter,
                                   AdminAppsGridView.Presenter editorGridPresenter,
                                   AppCategorizeView categorizeView) {
        this.serviceFacade = serviceFacade;
        this.avuFactory = avuFactory;
        this.editorTreeStore = editorTreeStore;
        this.previewTreeStore = previewTreeStore;
        this.appearance = appearance;
        this.ontologyUtil = OntologyUtil.getInstance();

        this.previewGridPresenter = previewGridPresenter;
        this.editorGridPresenter = editorGridPresenter;
        this.categorizeView = categorizeView;

        proxy = getProxy(appService);
        loader = getPagingLoader();

        this.view = factory.create(editorTreeStore,
                                   previewTreeStore,
                                   loader,
                                   previewGridPresenter.getView(),
                                   editorGridPresenter.getView(),
                                   new OntologyHierarchyToAppDND(appearance,
                                                                 previewGridPresenter,
                                                                 editorGridPresenter, this),
                                   new AppToOntologyHierarchyDND(appearance,
                                                                 previewGridPresenter,
                                                                 editorGridPresenter, this));

        previewGridPresenter.getView().addAppSelectionChangedEventHandler(view);
        editorGridPresenter.getView().addAppSelectionChangedEventHandler(view);

        proxy.setHasHandlers(view);

        view.addRefreshOntologiesEventHandler(this);
        view.addSelectOntologyVersionEventHandler(this);
        view.addSelectOntologyVersionEventHandler(previewGridPresenter.getView());
        view.addSelectOntologyVersionEventHandler(editorGridPresenter.getView());
        view.addHierarchySelectedEventHandler(this);
        view.addHierarchySelectedEventHandler(editorGridPresenter.getView());
        view.addPreviewHierarchySelectedEventHandler(this);
        view.addPreviewHierarchySelectedEventHandler(previewGridPresenter.getView());
        view.addSaveOntologyHierarchyEventHandler(this);
        view.addPublishOntologyClickEventHandler(this);
        view.addCategorizeButtonClickedEventHandler(this);
        view.addDeleteOntologyButtonClickedEventHandler(this);
        view.addDeleteHierarchyEventHandler(this);
        view.addDeleteAppsSelectedHandler(this);
        view.addRefreshPreviewButtonClickedHandler(this);

        view.addAppSearchResultLoadEventHandler(this);
        view.addAppSearchResultLoadEventHandler(previewGridPresenter);
        view.addAppSearchResultLoadEventHandler(previewGridPresenter.getView());
        view.addBeforeAppSearchEventHandler(previewGridPresenter.getView());

    }

    PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> getPagingLoader() {
        return new PagingLoader<>(proxy);
    }

    AppSearchRpcProxy getProxy(AppServiceFacade appService) {
        return new AppSearchRpcProxy(appService);
    }


    @Override
    public void go(HasOneWidget container) {
        getOntologies(true);
        container.setWidget(view);
    }

    @Override
    public OntologiesView getView() {
        return view;
    }

    void categorizeHierarchiesToApp(CategorizeHierarchiesToAppEvent event) {
        final App targetApp = event.getTargetApp();
        List<OntologyHierarchy> selectedHierarchies = event.getSelectedHierarchies();

        AvuList avus = ontologyUtil.convertHierarchiesToAvus(selectedHierarchies);

        serviceFacade.setAppAVUs(targetApp, avus, new AsyncCallback<List<Avu>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<Avu> result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.appClassified(targetApp.getName(), result)));
            }
        });
    }

    @Override
    public void appsDNDtoHierarchy(List<App> apps, OntologyHierarchy hierarchy) {
        if (apps != null & apps.size() > 0) {
            for (App app: apps) {
                hierarchyDNDtoApp(hierarchy, app);
            }
        }
    }

    @Override
    public void hierarchyDNDtoApp(final OntologyHierarchy hierarchy, final App targetApp) {
        if (ontologyUtil.isUnclassified(hierarchy)) {
            clearAvus(targetApp);
            return;
        }

        AvuList avuList = ontologyUtil.convertHierarchiesToAvus(hierarchy);
        serviceFacade.addAVUsToApp(targetApp, avuList, new AsyncCallback<List<Avu>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<Avu> result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.appClassified(targetApp.getName(), hierarchy.getLabel())));
                view.selectHierarchy(hierarchy);
                view.deselectAll();
                if (!previewTreeHasHierarchy(hierarchy)) {
                    getFilteredOntologyHierarchies(getSelectedOntology().getVersion(), editorTreeStore.getRootItems());
                }
            }
        });
    }

    boolean previewTreeHasHierarchy(OntologyHierarchy hierarchy) {
        String id = ontologyUtil.getOrCreateHierarchyPathTag(hierarchy);
        return previewTreeStore.findModelWithKey(id) != null;
    }

    void clearAvus(final App targetApp) {
        AvuList avuList = avuFactory.getAvuList().as();
        serviceFacade.setAppAVUs(targetApp, avuList, new AsyncCallback<List<Avu>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<Avu> result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.appAvusCleared(targetApp)));
            }
        });
    }

    @Override
    public void onRefreshOntologies(RefreshOntologiesEvent event) {
        getOntologies(false);
    }

    void getOntologies(final boolean selectActiveOntology) {
        editorGridPresenter.getView().clearAndAdd(null);
        previewGridPresenter.getView().clearAndAdd(null);
        view.clearTreeStore(OntologiesView.TreeType.ALL);
        serviceFacade.getOntologies(new AsyncCallback<List<Ontology>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unmaskTree(OntologiesView.TreeType.EDITOR);
            }

            @Override
            public void onSuccess(List<Ontology> result) {
                Collections.reverse(result);
                view.showOntologyVersions(result);
                view.unmaskTree(OntologiesView.TreeType.EDITOR);
                if (selectActiveOntology) {
                    for (Ontology ontology : result) {
                        if (ontology.isActive()){
                            view.selectActiveOntology(ontology);
                            return;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onSelectOntologyVersion(SelectOntologyVersionEvent event) {
        view.clearTreeStore(OntologiesView.TreeType.ALL);
        editorGridPresenter.getView().clearAndAdd(null);
        previewGridPresenter.getView().clearAndAdd(null);
        iriToHierarchyMap.clear();

        view.showTreePanel();
        view.maskTree(OntologiesView.TreeType.EDITOR);

        getOntologyHierarchies(event.getSelectedOntology().getVersion());

    }

    void getOntologyHierarchies(String version) {
        serviceFacade.getOntologyHierarchies(version,
                                             new HierarchiesCallback(version));
    }


    void getFilteredOntologyHierarchies(String version, List<OntologyHierarchy> result) {
        view.clearTreeStore(OntologiesView.TreeType.PREVIEW);
        for (OntologyHierarchy hierarchy: result) {
            view.maskTree(OntologiesView.TreeType.PREVIEW);
            String attr = ontologyUtil.getAttr(hierarchy);
            if (Strings.isNullOrEmpty(attr)) {
                continue;
            }
            serviceFacade.getFilteredOntologyHierarchy(version, hierarchy.getIri(), attr, new AsyncCallback<OntologyHierarchy>() {
                @Override
                public void onFailure(Throwable caught) {
                    ErrorHandler.post(caught);
                    view.unmaskTree(OntologiesView.TreeType.PREVIEW);
                }

                @Override
                public void onSuccess(OntologyHierarchy result) {
                    if (result != null) {
                        List<OntologyHierarchy> hierarchies = Lists.newArrayList(result);
                        addHierarchies(OntologiesView.TreeType.PREVIEW, null, hierarchies);
                        view.reSortTree(OntologiesView.TreeType.PREVIEW);
                        view.unmaskTree(OntologiesView.TreeType.PREVIEW);
                    }
                }
            });
        }
    }

    void addHierarchies(OntologiesView.TreeType type,
                        OntologyHierarchy parent,
                        List<OntologyHierarchy> children) {
        if ((children == null)
            || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            ontologyUtil.addUnclassifiedChild(children);
            view.addToTreeStore(type, children);
        } else {
            view.addToTreeStore(type, parent, children);

        }

        helperMap(children);

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(type, hierarchy, hierarchy.getSubclasses());
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
    public void onSaveOntologyHierarchy(SaveOntologyHierarchyEvent event) {
        List<String> iris = event.getIris();
        String ontologyVersion = event.getOntology().getVersion();
        for (String iri : iris) {
            view.maskTree(OntologiesView.TreeType.EDITOR);
            serviceFacade.saveOntologyHierarchy(ontologyVersion,
                                                iri, new SaveHierarchyAsyncCallback(ontologyVersion, iri));
        }

        view.showTreePanel();
    }

    @Override
    public void onCategorizeButtonClicked(CategorizeButtonClickedEvent event) {
        final App selectedApp = event.getSelectedApp();
        final List<OntologyHierarchy> hierarchyRoots = event.getHierarchyRoots();
        serviceFacade.getAppAVUs(selectedApp, new CategorizeCallback(selectedApp, hierarchyRoots));
    }

    boolean isValidHierarchy(OntologyHierarchy result) {
        // If there are no subclasses, either the hierarchy had no subcategories (which
        // we do not want as a root, or
        // there was an undetected typo in the iri
        return null != result.getSubclasses();
    }

    @Override
    public void onPublishOntologyClick(PublishOntologyClickEvent event) {
        serviceFacade.setActiveOntologyVersion(event.getNewActiveOntology().getVersion(), new AsyncCallback<OntologyVersionDetail>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(OntologyVersionDetail result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.setActiveOntologySuccess()));
                getOntologies(true);
            }
        });
    }


    @Override
    public void onPreviewHierarchySelected(PreviewHierarchySelectedEvent event) {
        OntologyHierarchy hierarchy = event.getHierarchy();
        Ontology editedOntology = event.getEditedOntology();
        getAppsByHierarchy(previewGridPresenter.getView(), hierarchy, editedOntology);
    }

    @Override
    public void onHierarchySelected(HierarchySelectedEvent event) {
        OntologyHierarchy hierarchy = event.getHierarchy();
        Ontology editedOntology = event.getEditedOntology();
        getAppsByHierarchy(editorGridPresenter.getView(), hierarchy, editedOntology);
    }

    void getAppsByHierarchy(final AdminAppsGridView gridView,
                            OntologyHierarchy hierarchy,
                            Ontology editedOntology) {
        if (ontologyUtil.isUnclassified(hierarchy)){
            getUnclassifiedApps(gridView, hierarchy, editedOntology);
            return;
        }

        String attr = ontologyUtil.getAttr(hierarchy);
        if (Strings.isNullOrEmpty(attr)) {
            displayErrorToAdmin();
            return;
        }

        gridView.mask(appearance.loadingMask());
        serviceFacade.getAppsByHierarchy(editedOntology.getVersion(), hierarchy.getIri(), attr, new AsyncCallback<List<App>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                gridView.unmask();
            }

            @Override
            public void onSuccess(List<App> result) {
                gridView.clearAndAdd(result);
                gridView.unmask();
            }
        });
    }

    void getUnclassifiedApps(final AdminAppsGridView gridView,
                             OntologyHierarchy hierarchy,
                             Ontology editedOntology) {
        String parentIri = ontologyUtil.getUnclassifiedParentIri(hierarchy);
        gridView.mask(appearance.loadingMask());
        Avu avu = ontologyUtil.convertHierarchyToAvu(hierarchy);
        serviceFacade.getUnclassifiedApps(editedOntology.getVersion(), parentIri, avu, new AsyncCallback<List<App>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                gridView.unmask();
            }

            @Override
            public void onSuccess(List<App> result) {
                gridView.clearAndAdd(result);
                gridView.unmask();
            }
        });
    }

    @Override
    public OntologyHierarchy getHierarchyFromElement(Element el) {
        return view.getHierarchyFromElement(el);
    }

    @Override
    public OntologyHierarchy getSelectedHierarchy() {
        return view.getSelectedHierarchy();
    }

    @Override
    public Ontology getSelectedOntology() {
        return view.getSelectedOntology();
    }

    @Override
    public void onDeleteOntologyButtonClicked(final DeleteOntologyButtonClickedEvent event) {
        serviceFacade.deleteOntology(event.getOntologyVersion(), new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(Void result) {
                announcer.schedule(new SuccessAnnouncementConfig(appearance.ontologyDeleted(event.getOntologyVersion())));
                getOntologies(false);
            }
        });
    }

    @Override
    public void onDeleteHierarchy(final DeleteHierarchyEvent event) {
        final OntologyHierarchy deletedHierarchy = event.getDeletedHierarchy();
        serviceFacade.deleteRootHierarchy(event.getEditedOntology().getVersion(),
                                          deletedHierarchy.getIri(),
                                          new AsyncCallback<List<OntologyHierarchy>>() {
                                              @Override
                                              public void onFailure(Throwable caught) {
                                                  ErrorHandler.post(caught);
                                              }

                                              @Override
                                              public void onSuccess(List<OntologyHierarchy> result) {
                                                  announcer.schedule(new SuccessAnnouncementConfig(
                                                          appearance.hierarchyDeleted(deletedHierarchy.getLabel())));
                                                  getOntologyHierarchies(event.getEditedOntology()
                                                                              .getVersion());
                                              }
                                          });
    }

    @Override
    public void onDeleteAppsSelected(DeleteAppsSelected event) {
        Preconditions.checkArgument(event.getAppsToBeDeleted().size() == 1);
        final App selectedApp = event.getAppsToBeDeleted().iterator().next();

        view.maskGrids(appearance.loadingMask());
        adminAppService.deleteApp(selectedApp,
                                  new AsyncCallback<Void>() {

                                      @Override
                                      public void onFailure(Throwable caught) {
                                          view.unmaskGrids();
                                          ErrorHandler.post(caught);
                                      }

                                      @Override
                                      public void onSuccess(Void result) {
                                          view.removeApp(selectedApp);
                                          view.unmaskGrids();
                                      }
                                  });
    }

    void displayErrorToAdmin() {
        ErrorHandler.post(appearance.ontologyAttrMatchingError());
    }

    @Override
    public void onRefreshPreviewButtonClicked(RefreshPreviewButtonClicked event) {
        Ontology editedOntology = event.getEditedOntology();
        List<OntologyHierarchy> roots = event.getRoots();

        Preconditions.checkNotNull(editedOntology);
        Preconditions.checkNotNull(roots);

        String version = editedOntology.getVersion();

        getFilteredOntologyHierarchies(version, roots);
    }

    @Override
    public void onAppSearchResultLoad(AppSearchResultLoadEvent event) {
        view.deselectHierarchies(OntologiesView.TreeType.ALL);
    }
}
