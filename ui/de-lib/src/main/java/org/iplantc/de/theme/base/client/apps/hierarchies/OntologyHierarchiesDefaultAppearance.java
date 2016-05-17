package org.iplantc.de.theme.base.client.apps.hierarchies;

import org.iplantc.de.apps.client.OntologyHierarchiesView;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.theme.base.client.apps.categories.AppCategoriesViewDefaultAppearance;

import com.google.gwt.core.client.GWT;

/**
 * @author aramsey
 */
public class OntologyHierarchiesDefaultAppearance extends AppCategoriesViewDefaultAppearance implements OntologyHierarchiesView.OntologyHierarchiesAppearance {

    private final IplantDisplayStrings iplantDisplayStrings;
    private final OntologyHierarchiesDisplayStrings displayStrings;

    public OntologyHierarchiesDefaultAppearance() {
        this(GWT.<IplantDisplayStrings> create(IplantDisplayStrings.class),
             GWT.<OntologyHierarchiesDisplayStrings> create(OntologyHierarchiesDisplayStrings.class));
    }

    public OntologyHierarchiesDefaultAppearance(IplantDisplayStrings iplantDisplayStrings,
                                                OntologyHierarchiesDisplayStrings displayStrings) {
        this.iplantDisplayStrings = iplantDisplayStrings;
        this.displayStrings = displayStrings;
    }

    @Override
    public String hierarchyLabelName(OntologyHierarchy hierarchy) {
        return displayStrings.hierarchyLabelName(hierarchy.getLabel());
    }

}
