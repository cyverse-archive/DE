package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeButtonClickedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.CategorizeHierarchiesToAppEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.RefreshOntologiesEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppToOntologyHierarchyDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyHierarchyToAppDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.CategorizeDialog;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.avu.Avu;
import org.iplantc.de.client.models.avu.AvuAutoBeanFactory;
import org.iplantc.de.client.models.avu.AvuList;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.OntologyUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.data.shared.TreeStore;

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
                                                CategorizeButtonClickedEvent.CategorizeButtonClickedEventHandler {

    private class CategorizeCallback implements AsyncCallback<List<Avu>> {
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

    private class SaveHierarchyAsyncCallback implements AsyncCallback<OntologyHierarchy> {
        private final String iri;
        private boolean clearHierarchies;
        private boolean isLast;

        public SaveHierarchyAsyncCallback(String iri, boolean clearHierarchies, boolean isLast) {
            this.iri = iri;
            this.clearHierarchies = clearHierarchies;
            this.isLast = isLast;
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(caught);
            view.unMaskHierarchyTree();
        }

        @Override
        public void onSuccess(OntologyHierarchy result) {
            if (isValidHierarchy(result)) {
                if (clearHierarchies) {
                    view.clearStore();
                }
                addHierarchies(null,
                               Lists.newArrayList(result));
                if (isLast) {
                    view.reSortHierarchies();
                    view.unMaskHierarchyTree();
                }

            } else {
                announcer.schedule(new ErrorAnnouncementConfig(
                        appearance.invalidHierarchySubmitted(iri)));

            }
        }
    }


    @Inject DEProperties properties;
    @Inject IplantAnnouncer announcer;
    OntologyUtil ontologyUtil;
    private OntologiesView view;
    private OntologyServiceFacade serviceFacade;
    private final TreeStore<OntologyHierarchy> treeStore;
    private OntologiesView.OntologiesViewAppearance appearance;
    private AdminCategoriesView.Presenter categoriesPresenter;
    private AdminAppsGridView.Presenter oldGridPresenter;
    private AdminAppsGridView.Presenter newGridPresenter;
    private AppCategorizeView categorizeView;
    private AvuAutoBeanFactory avuFactory;
    private Map<String, List<OntologyHierarchy>> iriToHierarchyMap = new FastMap<>();

    @Inject
    public OntologiesPresenterImpl(OntologyServiceFacade serviceFacade,
                                   final TreeStore<OntologyHierarchy> treeStore,
                                   OntologiesViewFactory factory,
                                   AvuAutoBeanFactory avuFactory,
                                   OntologiesView.OntologiesViewAppearance appearance,
                                   AdminCategoriesView.Presenter categoriesPresenter,
                                   AdminAppsGridView.Presenter oldGridPresenter,
                                   AdminAppsGridView.Presenter newGridPresenter,
                                   AppCategorizeView categorizeView) {
        this.serviceFacade = serviceFacade;
        this.avuFactory = avuFactory;
        this.treeStore = treeStore;
        this.appearance = appearance;
        this.ontologyUtil = OntologyUtil.getInstance();

        this.categoriesPresenter = categoriesPresenter;
        this.oldGridPresenter = oldGridPresenter;
        this.newGridPresenter = newGridPresenter;
        this.categorizeView = categorizeView;

        this.view = factory.create(treeStore,
                                   categoriesPresenter.getView(),
                                   oldGridPresenter.getView(),
                                   newGridPresenter.getView(),
                                   new OntologyHierarchyToAppDND(appearance, oldGridPresenter, newGridPresenter, this),
                                   new AppToOntologyHierarchyDND(appearance, oldGridPresenter, newGridPresenter, this));

        categoriesPresenter.getView().addAppCategorySelectedEventHandler(oldGridPresenter);
        categoriesPresenter.getView().addAppCategorySelectedEventHandler(oldGridPresenter.getView());
        oldGridPresenter.addStoreRemoveHandler(categoriesPresenter);
        oldGridPresenter.getView().addAppSelectionChangedEventHandler(view);
        newGridPresenter.getView().addAppSelectionChangedEventHandler(view);

        view.addRefreshOntologiesEventHandler(this);
        view.addSelectOntologyVersionEventHandler(this);
        view.addHierarchySelectedEventHandler(this);
        view.addHierarchySelectedEventHandler(newGridPresenter.getView());
        view.addSaveOntologyHierarchyEventHandler(this);
        view.addPublishOntologyClickEventHandler(this);
        view.addCategorizeButtonClickedEventHandler(this);
    }


    @Override
    public void go(HasOneWidget container) {
        HasId betaGroup = CommonModelUtils.getInstance().createHasIdFromString(DEProperties.getInstance().getDefaultBetaCategoryId());

        categoriesPresenter.go(betaGroup);
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
            }
        });
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
        newGridPresenter.getView().clearAndAdd(null);
        serviceFacade.getOntologies(new AsyncCallback<List<Ontology>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unMaskHierarchyTree();
            }

            @Override
            public void onSuccess(List<Ontology> result) {
                Collections.reverse(result);
                view.showOntologyVersions(result);
                view.unMaskHierarchyTree();
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
        view.clearStore();
        newGridPresenter.getView().clearAndAdd(null);
        iriToHierarchyMap.clear();

        view.showTreePanel();
        view.maskHierarchyTree();
        serviceFacade.getOntologyHierarchies(event.getSelectedOntology().getVersion(), new AsyncCallback<List<OntologyHierarchy>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                view.unMaskHierarchyTree();
            }

            @Override
            public void onSuccess(List<OntologyHierarchy> result) {
                if (result.size() == 0) {
                    view.showEmptyTreePanel();
                } else {
                    addHierarchies(null, result);
                }
                view.unMaskHierarchyTree();
            }
        });

    }

    void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
        if ((children == null)
            || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            ontologyUtil.addUnclassifiedChild(children);
            view.addToStore(children);

        } else {
            view.addToStore(parent, children);
        }

        helperMap(children);

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(hierarchy, hierarchy.getSubclasses());
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
        boolean clearHierarchies = true;
        boolean isLast = false;
        for (String iri : iris) {
            if (iris.indexOf(iri) == iris.size() - 1) {
                isLast = true;
            }
            view.maskHierarchyTree();
            serviceFacade.saveOntologyHierarchy(event.getOntology().getVersion(),
                                                iri, new SaveHierarchyAsyncCallback(iri, clearHierarchies, isLast));
            clearHierarchies = false;
        }

        view.showTreePanel();
    }

    @Override
    public void onCategorizeButtonClicked(CategorizeButtonClickedEvent event) {
        final App selectedApp = event.getSelectedApp();
        final List<OntologyHierarchy> hierarchyRoots = event.getHierarchyRoots();
        serviceFacade.getAppAVUs(selectedApp, new CategorizeCallback(selectedApp, hierarchyRoots));
    }

    private boolean isValidHierarchy(OntologyHierarchy result) {
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
            }
        });
    }

    @Override
    public void onHierarchySelected(HierarchySelectedEvent event) {
        OntologyHierarchy hierarchy = event.getHierarchy();
        Ontology editedOntology = event.getEditedOntology();
        if (ontologyUtil.isUnclassified(hierarchy)){
            getUnclassifiedApps(hierarchy, editedOntology);
            return;
        }

        Avu avu = ontologyUtil.convertHierarchyToAvu(hierarchy);

        newGridPresenter.getView().mask(appearance.loadingMask());
        serviceFacade.getAppsByHierarchy(hierarchy.getIri(), avu, new AsyncCallback<List<App>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                newGridPresenter.getView().unmask();
            }

            @Override
            public void onSuccess(List<App> result) {
                newGridPresenter.getView().clearAndAdd(result);
                newGridPresenter.getView().unmask();
            }
        });
    }

    void getUnclassifiedApps(OntologyHierarchy hierarchy, Ontology editedOntology) {
        String parentIri = ontologyUtil.getUnclassifiedParentIri(hierarchy);
        newGridPresenter.getView().mask(appearance.loadingMask());
        Avu avu = ontologyUtil.convertHierarchyToAvu(hierarchy);
        serviceFacade.getUnclassifiedApps(editedOntology.getVersion(), parentIri, avu, new AsyncCallback<List<App>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
                newGridPresenter.getView().unmask();
            }

            @Override
            public void onSuccess(List<App> result) {
                newGridPresenter.getView().clearAndAdd(result);
                newGridPresenter.getView().unmask();
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

}
