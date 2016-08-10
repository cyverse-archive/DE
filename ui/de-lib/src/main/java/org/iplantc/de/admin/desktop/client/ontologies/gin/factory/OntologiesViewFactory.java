package org.iplantc.de.admin.desktop.client.ontologies.gin.factory;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.admin.desktop.client.ontologies.views.AppToOntologyHierarchyDND;
import org.iplantc.de.admin.desktop.client.ontologies.views.OntologyHierarchyToAppDND;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * @author aramsey
 */
public interface OntologiesViewFactory {

    OntologiesView create(@Assisted("editorTreeStore") TreeStore<OntologyHierarchy> treeStore,
                          @Assisted("previewTreeStore") TreeStore<OntologyHierarchy> previewTreeStore,
                          PagingLoader<FilterPagingLoadConfig, PagingLoadResult<App>> loader,
                          @Assisted("oldGridView") AdminAppsGridView oldGridView,
                          @Assisted("newGridView") AdminAppsGridView newGridView,
                          OntologyHierarchyToAppDND dndHandler,
                          AppToOntologyHierarchyDND appDndHandler);
}
