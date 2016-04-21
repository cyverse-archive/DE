package org.iplantc.de.admin.desktop.client.ontologies.gin;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.admin.desktop.client.ontologies.presenter.OntologiesPresenterImpl;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.service.impl.OntologyServiceFacadeImpl;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologiesViewImpl;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;

/**
 * @author aramsey
 */
public class OntologiesGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        install(new GinFactoryModuleBuilder().implement(OntologiesView.class, OntologiesViewImpl.class).build(
                OntologiesViewFactory.class));
        bind(OntologiesView.Presenter.class).to(OntologiesPresenterImpl.class);
        bind(OntologyServiceFacade.class).to(OntologyServiceFacadeImpl.class);
    }
}
