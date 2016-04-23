package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.SelectOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import com.sencha.gxt.data.shared.TreeStore;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologiesPresenterImpl implements OntologiesView.Presenter,
                                                ViewOntologyVersionEvent.ViewOntologyVersionEventHandler,
                                                SelectOntologyVersionEvent.SelectOntologyVersionEventHandler {

    @Inject DEProperties properties;
    private OntologiesView view;
    private OntologyServiceFacade serviceFacade;
    private final TreeStore<OntologyHierarchy> treeStore;
    private OntologiesViewFactory factory;

    @Inject
    public OntologiesPresenterImpl(OntologyServiceFacade serviceFacade,
                                   final TreeStore<OntologyHierarchy> treeStore,
                                   OntologiesViewFactory factory) {
        this.serviceFacade = serviceFacade;
        this.treeStore = treeStore;
        this.factory = factory;
        this.view = factory.create(treeStore);

        view.addViewOntologyVersionEventHandler(this);
        view.addSelectOntologyVersionEventHandler(this);
    }


    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }

    @Override
    public void onViewOntologyVersion(ViewOntologyVersionEvent event) {
        serviceFacade.getOntologies(new AsyncCallback<List<Ontology>>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(List<Ontology> result) {
                view.showOntologyVersions(result);
            }
        });
    }

    @Override
    public void onSelectOntologyVersion(SelectOntologyVersionEvent event) {
        serviceFacade.getOntologyHierarchies(event.getSelectedOntology().getVersion(),
                                             new AsyncCallback<List<OntologyHierarchy>>() {
                                                 @Override
                                                 public void onFailure(Throwable caught) {
                                                     ErrorHandler.post(caught);
                                                 }

                                                 @Override
                                                 public void onSuccess(List<OntologyHierarchy> result) {
                                                     treeStore.clear();
                                                     addHierarchies(null, result);
            }
        });
    }

    private void addHierarchies(OntologyHierarchy parent, List<OntologyHierarchy> children) {
        if ((children == null)
            || children.isEmpty()) {
            return;
        }
        if (parent == null) {
            treeStore.add(children);
        } else {
            treeStore.add(parent, children);
        }

        for (OntologyHierarchy hierarchy : children) {
            addHierarchies(hierarchy, hierarchy.getSubclasses());
        }
    }
}
