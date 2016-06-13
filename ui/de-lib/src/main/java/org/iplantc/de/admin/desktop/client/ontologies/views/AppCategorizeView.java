package org.iplantc.de.admin.desktop.client.ontologies.views;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppCategorizeView extends IsWidget {

    void setHierarchies(List<OntologyHierarchy> categories);

    List<OntologyHierarchy> getSelectedCategories();

    void setSelectedHierarchies(List<OntologyHierarchy> categories);

    void mask(String loadingMask);

    void unmask();

}
