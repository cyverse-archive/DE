package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyList {

    @PropertyName("ontologies")
    List<Ontology> getOntologies();

    @PropertyName("ontologies")
    void setOntologies(List<Ontology> ontologies);
}
