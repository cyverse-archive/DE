package org.iplantc.de.admin.desktop.client.ontologies.model;

import org.iplantc.de.client.models.ontologies.OntologyHierarchy;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author aramsey
 */
public interface OntologyHierarchyProperties extends PropertyAccess<OntologyHierarchy> {

    ValueProvider<OntologyHierarchy, String> iri();

    ValueProvider<OntologyHierarchy, String> label();
}
