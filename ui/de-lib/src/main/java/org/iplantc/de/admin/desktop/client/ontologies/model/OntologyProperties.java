package org.iplantc.de.admin.desktop.client.ontologies.model;

import org.iplantc.de.client.models.ontologies.Ontology;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author aramsey
 */
public interface OntologyProperties extends PropertyAccess<Ontology> {

    ValueProvider<Ontology, String> iri();

    ValueProvider<Ontology, String> version();

    ValueProvider<Ontology, String> createdBy();

    ValueProvider<Ontology, String> createdOn();
}
