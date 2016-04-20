package org.iplantc.de.apps.client.gin.factory;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public interface OntologyHierarchiesViewFactory {
    OntologyHierarchiesView create(TreeStore<OntologyHierarchy> treeStore);
}
