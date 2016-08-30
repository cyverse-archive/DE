package org.iplantc.de.admin.desktop.client.ontologies.model;

import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.commons.client.widgets.DETree;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public class HierarchyTree<O, S> extends DETree<OntologyHierarchy, String> {

    public HierarchyTree(TreeStore<OntologyHierarchy> store,
                         ValueProvider<? super OntologyHierarchy, String> valueProvider) {
        super(store, valueProvider);
    }

    @Override
    public java.lang.String generateDebugId(OntologyHierarchy ontologyHierarchy) {
        return getBaseId() + "." + ontologyHierarchy.getIri() + Belphegor.CatalogIds.TREE_NODE;
    }
}
