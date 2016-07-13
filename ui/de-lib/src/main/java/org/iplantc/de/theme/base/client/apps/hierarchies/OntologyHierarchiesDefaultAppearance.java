package org.iplantc.de.theme.base.client.apps.hierarchies;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.theme.base.client.apps.categories.AppCategoriesViewDefaultAppearance;

/**
 * @author aramsey
 */
public class OntologyHierarchiesDefaultAppearance extends AppCategoriesViewDefaultAppearance implements OntologyHierarchiesView.OntologyHierarchiesAppearance {


    public OntologyHierarchiesDefaultAppearance() {
    }

    @Override
    public String hierarchyLabelName(OntologyHierarchy hierarchy) {
        return hierarchy.getLabel();
    }

}
