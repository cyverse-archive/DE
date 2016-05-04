package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.apps.client.AdminCategoriesView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.HierarchySelectedEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.PublishOntologyClickEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SaveOntologyHierarchyEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyAutoBeanFactory;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.models.ontologies.OntologyMetadata;
import org.iplantc.de.client.models.ontologies.OntologyVersionDetail;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.commons.client.info.SuccessAnnouncementConfig;

import com.google.common.collect.Lists;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.Collections;
import java.util.List;

/**
 * @author aramsey
 */
public class OntologiesPresenterImpl implements OntologiesView.Presenter,
                                                ViewOntologyVersionEvent.ViewOntologyVersionEventHandler,
                                                SelectOntologyVersionEvent.SelectOntologyVersionEventHandler,
                                                SaveOntologyHierarchyEvent.SaveOntologyHierarchyEventHandler,
                                                PublishOntologyClickEvent.PublishOntologyClickEventHandler,
                                                HierarchySelectedEvent.HierarchySelectedEventHandler {

    @Inject DEProperties properties;
    @Inject IplantAnnouncer announcer;
    private OntologiesView view;
    private OntologyServiceFacade serviceFacade;
    private final TreeStore<OntologyHierarchy> treeStore;
    private OntologiesView.OntologiesViewAppearance appearance;
    private AdminCategoriesView.Presenter categoriesPresenter;
    private AdminAppsGridView.Presenter oldGridPresenter;
    private AdminAppsGridView.Presenter newGridPresenter;
    private OntologyAutoBeanFactory beanFactory;
    private String UNCLASSIFIED_LABEL = "Unclassified";
    private String UNCLASSIFIED_IRI_APPEND = "_unclassified";

    @Inject
    public OntologiesPresenterImpl(OntologyServiceFacade serviceFacade,
                                   final TreeStore<OntologyHierarchy> treeStore,
                                   OntologyAutoBeanFactory beanFactory,
                                   OntologiesViewFactory factory,
                                   OntologiesView.OntologiesViewAppearance appearance,
                                   AdminCategoriesView.Presenter categoriesPresenter,
                                   AdminAppsGridView.Presenter oldGridPresenter,
                                   AdminAppsGridView.Presenter newGridPresenter) {
        this.serviceFacade = serviceFacade;
        this.beanFactory = beanFactory;
        this.treeStore = treeStore;
        this.appearance = appearance;

        this.categoriesPresenter = categoriesPresenter;
        this.oldGridPresenter = oldGridPresenter;
        this.newGridPresenter = newGridPresenter;

        this.view = factory.create(treeStore, categoriesPresenter.getView(), oldGridPresenter.getView(), newGridPresenter.getView());

        categoriesPresenter.getView().addAppCategorySelectedEventHandler(oldGridPresenter);
        categoriesPresenter.getView().addAppCategorySelectedEventHandler(oldGridPresenter.getView());
        oldGridPresenter.addStoreRemoveHandler(categoriesPresenter);

        view.addViewOntologyVersionEventHandler(this);
        view.addSelectOntologyVersionEventHandler(this);
        view.addHierarchySelectedEventHandler(this);
        view.addHierarchySelectedEventHandler(newGridPresenter.getView());
        view.addSaveOntologyHierarchyEventHandler(this);
        view.addPublishOntologyClickEventHandler(this);
    }


    @Override
    public void go(HasOneWidget container) {
        HasId betaGroup = CommonModelUtils.getInstance().createHasIdFromString(DEProperties.getInstance().getDefaultBetaCategoryId());

        categoriesPresenter.go(betaGroup);
        getOntologies();
        container.setWidget(view);
    }

    @Override
    public void onViewOntologyVersion(ViewOntologyVersionEvent event) {
        getOntologies();
    }

    void getOntologies() {
        serviceFacade.getOntologies(new AsyncCallback<List<Ontology>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<Ontology> result) {
                Collections.reverse(result);
                view.showOntologyVersions(result);
            }
        });
    }

    @Override
    public void onSelectOntologyVersion(SelectOntologyVersionEvent event) {
        treeStore.clear();
        serviceFacade.getOntologyHierarchies(event.getSelectedOntology().getVersion(), new AsyncCallback<List<OntologyHierarchy>>() {
                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     ErrorHandler.post(caught);
                                                 }

                                                 @Override
                                                 public void onSuccess(List<OntologyHierarchy> result) {
                                                     if (result.size() == 0) {
                                                         view.showEmptyTreePanel();
                                                     }
                                                     else {
                                                         addHierarchies(null, result);
                                                         view.showTreePanel();
                                                     }
            }
        });

    }

    void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
        if ((children == null)
            || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            addUnclassifiedChild(children);
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(hierarchy, hierarchy.getSubclasses());
        }
    }

    void addUnclassifiedChild(List<OntologyHierarchy> children) {
        for (OntologyHierarchy child : children){
            OntologyHierarchy unclassified = beanFactory.getHierarchy().as();
            unclassified.setLabel(UNCLASSIFIED_LABEL);
            unclassified.setIri(child.getIri() + UNCLASSIFIED_IRI_APPEND);
            child.getSubclasses().add(unclassified);
        }
    }

    @Override
    public void onSaveOntologyHierarchy(SaveOntologyHierarchyEvent event) {
        List<String> iris = event.getIris();

        for (final String iri : iris) {
            serviceFacade.saveOntologyHierarchy(event.getOntology().getVersion(),
                                                iri,
                                                new AsyncCallback<OntologyHierarchy>() {
                                                    @Override
                                                    public void onFailure(Throwable caught) {
                                                        ErrorHandler.post(caught);
                                                    }

                                                    @Override
                                                    public void onSuccess(OntologyHierarchy result) {
                                                        if (isValidHierarchy(result)) {
                                                            addHierarchies(null, Lists.newArrayList(result));
                                                        } else {
                                                            announcer.schedule(new ErrorAnnouncementConfig(appearance.invalidHierarchySubmitted(iri)));

                                                        }
                                                    }
                                                });
        }

        view.showTreePanel();
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
        if (isUnclassified(hierarchy)){
            getUnclassifiedApps(hierarchy, editedOntology);
            return;
        }

        OntologyMetadata metadata = getOntologyMetadata(hierarchy);

        newGridPresenter.getView().mask("Loading");
        serviceFacade.getAppsByHierarchy(hierarchy.getIri(), metadata, new AsyncCallback<List<App>>() {
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

    OntologyMetadata getOntologyMetadata(OntologyHierarchy hierarchy) {
        OntologyMetadata metadata = beanFactory.getMetadata().as();
        if (hierarchy.getIri().contains("operation")){
            metadata.setAttr(URL.encodeQueryString(OntologyMetadata.OPERATION_ATTR));
        }
        else{
            metadata.setAttr(URL.encodeQueryString(OntologyMetadata.TOPIC_ATTR));
        }
        return metadata;
    }

    boolean isUnclassified(OntologyHierarchy hierarchy) {
        return hierarchy.getIri().matches(".*" + UNCLASSIFIED_IRI_APPEND + "$");
    }

    void getUnclassifiedApps(OntologyHierarchy hierarchy, Ontology editedOntology) {
        String parentIri = getParentIri(hierarchy);
        newGridPresenter.getView().mask("Loading");
        OntologyMetadata metadata = getOntologyMetadata(hierarchy);
        serviceFacade.getUnclassifiedApps(editedOntology.getVersion(), parentIri, metadata, new AsyncCallback<List<App>>() {
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

    String getParentIri(OntologyHierarchy hierarchy) {
        return hierarchy.getIri().replace(UNCLASSIFIED_IRI_APPEND,"");
    }
}
