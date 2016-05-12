package org.iplantc.de.admin.desktop.client.ontologies.gin.factory;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppToOntologyHierarchyDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyHierarchyToAppDND;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public interface OntologiesViewFactory {

    OntologiesView create(TreeStore<OntologyHierarchy> treeStore,
                          AppCategoriesView categoriesView,
                          @Assisted("oldGridView") AdminAppsGridView oldGridView,
                          @Assisted("newGridView") AdminAppsGridView newGridView,
                          OntologyHierarchyToAppDND dndHandler,
                          AppToOntologyHierarchyDND appDndHandler);
}
