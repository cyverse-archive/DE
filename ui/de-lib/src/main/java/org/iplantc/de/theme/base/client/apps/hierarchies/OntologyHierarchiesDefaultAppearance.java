package org.iplantc.de.theme.base.client.apps.hierarchies;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.theme.base.client.apps.AppsMessages;
import org.iplantc.de.theme.base.client.apps.categories.AppCategoriesViewDefaultAppearance;

import com.google.gwt.core.client.GWT;

/**
 * @author aramsey
 */
public class OntologyHierarchiesDefaultAppearance extends AppCategoriesViewDefaultAppearance implements OntologyHierarchiesView.OntologyHierarchiesAppearance {

    private AppsMessages displayStrings;

    public OntologyHierarchiesDefaultAppearance() {
        this(GWT.<AppsMessages> create(AppsMessages.class));
    }

    public OntologyHierarchiesDefaultAppearance(AppsMessages appsMessages) {
        this.displayStrings = appsMessages;
    }

    @Override
    public String hierarchyLabelName(OntologyHierarchy hierarchy) {
        return hierarchy.getLabel();
    }

    @Override
    public String ontologyAttrMatchingFailure() {
        return displayStrings.ontologyAttrMatchingFailure();
    }

}
