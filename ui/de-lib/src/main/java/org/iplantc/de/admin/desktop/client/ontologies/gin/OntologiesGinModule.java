package org.iplantc.de.admin.desktop.client.ontologies.gin;

import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.gin.factory.OntologiesViewFactory;
import org.iplantc.de.apps.client.gin.OntologyHierarchyTreeStoreProvider;
import org.iplantc.de.admin.desktop.client.ontologies.presenter.OntologiesPresenterImpl;
import org.iplantc.de.admin.desktop.client.ontologies.service.OntologyServiceFacade;
import org.iplantc.de.admin.desktop.client.ontologies.service.impl.OntologyServiceFacadeImpl;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppCategorizeViewImpl;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologiesViewImpl;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.TypeLiteral;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public class OntologiesGinModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(new TypeLiteral<TreeStore<OntologyHierarchy>>() {})
                .toProvider(OntologyHierarchyTreeStoreProvider.class);
        install(new GinFactoryModuleBuilder().implement(OntologiesView.class, OntologiesViewImpl.class).build(
                OntologiesViewFactory.class));
        bind(OntologiesView.Presenter.class).to(OntologiesPresenterImpl.class);
        bind(OntologyServiceFacade.class).to(OntologyServiceFacadeImpl.class);
        bind(AppCategorizeView.class).to(AppCategorizeViewImpl.class);
    }
}
