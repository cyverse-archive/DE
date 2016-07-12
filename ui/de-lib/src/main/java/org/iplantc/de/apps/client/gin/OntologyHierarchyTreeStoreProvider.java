package org.iplantc.de.apps.client.gin;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.client.util.OntologyUtil;

import com.google.inject.Provider;

import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;

/**
 * @author aramsey
 */
public class OntologyHierarchyTreeStoreProvider implements Provider<TreeStore<OntologyHierarchy>> {

    private OntologyUtil ontologyUtil = OntologyUtil.getInstance();

    @Override
    public TreeStore<OntologyHierarchy> get() {
        return new TreeStore<>(new ModelKeyProvider<OntologyHierarchy>() {
            @Override
            public String getKey(OntologyHierarchy item) {
                return ontologyUtil.getOrCreateHierarchyPathTag(item);
            }
        });
    }
}
