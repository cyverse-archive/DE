package org.iplantc.de.admin.desktop.client.ontologies.gin.factory;

import org.iplantc.de.admin.apps.client.AdminAppsGridView;
import org.iplantc.de.admin.desktop.client.ontologies.OntologiesView;
import org.iplantc.de.apps.client.AppCategoriesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public interface OntologiesViewFactory {

    OntologiesView create(TreeStore<OntologyHierarchy> treeStore, AppCategoriesView categoriesView, AdminAppsGridView gridView);
}
