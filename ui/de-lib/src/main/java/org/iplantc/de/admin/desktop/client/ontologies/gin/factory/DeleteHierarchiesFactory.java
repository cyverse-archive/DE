package org.iplantc.de.admin.desktop.client.ontologies.gin.factory;

import org.iplantc.de.admin.desktop.client.ontologies.views.dialogs.DeleteHierarchiesDialog;
import org.iplantc.de.client.models.ontologies.Ontology;
import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.google.gwt.event.shared.HasHandlers;

import java.util.List;

/**
 * @author aramsey
 */
public interface DeleteHierarchiesFactory {

    DeleteHierarchiesDialog create(Ontology editedOntology,
                                   List<OntologyHierarchy> roots,
                                   HasHandlers handlers);
}
