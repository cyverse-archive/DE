package org.iplantc.de.admin.desktop.client.ontologies.views.dialogs;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * @author aramsey
 */
public interface DeleteHierarchiesView extends IsWidget{

    void addHierarchyRoots(List<OntologyHierarchy> roots);

    List<OntologyHierarchy> getDeletedHierarchies();
}
