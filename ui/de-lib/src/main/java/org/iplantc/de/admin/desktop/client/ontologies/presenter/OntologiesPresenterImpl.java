package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.events.ViewOntologyVersionEvent;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.client.models.DEProperties;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

import java.util.List;

/**
 * @author aramsey
 */
public class OntologiesPresenterImpl implements OntologiesView.Presenter,
                                                ViewOntologyVersionEvent.ViewOntologyVersionEventHandler {

    @Inject DEProperties properties;
    private OntologiesView view;
    private OntologyServiceFacade serviceFacade;

    @Inject
    public OntologiesPresenterImpl(OntologiesView view, OntologyServiceFacade serviceFacade) {
        this.view = view;
        this.serviceFacade = serviceFacade;

        view.addViewOntologyVersionEventHandler(this);
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
}
