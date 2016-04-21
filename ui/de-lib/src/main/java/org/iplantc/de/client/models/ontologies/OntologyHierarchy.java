package org.iplantc.de.client.models.ontologies;

import org.iplantc.de.client.models.HasDescription;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyHierarchy extends HasDescription {
    String getIri();

    void setIri(String iri);

    String getLabel();

    void setLabel(String label);

    List<OntologyHierarchy> getSubclasses();

    void setSubclasses(List<OntologyHierarchy> subclasses);
}
