package org.iplantc.de.admin.desktop.client.ontologies.presenter;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.client.models.DEProperties;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.inject.Inject;

/**
 * @author aramsey
 */
public class OntologiesPresenterImpl implements OntologiesView.Presenter {

    @Inject DEProperties properties;
    private OntologiesView view;

    @Inject
    public OntologiesPresenterImpl(OntologiesView view) {
        this.view = view;
    }


    @Override
    public void go(HasOneWidget container) {
        container.setWidget(view);
    }
}
