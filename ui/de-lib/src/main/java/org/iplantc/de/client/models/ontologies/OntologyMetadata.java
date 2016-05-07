package org.iplantc.de.client.models.ontologies;

import org.iplantc.de.client.models.avu.Avu;

/**
 * @author aramsey
 */
public interface OntologyMetadata extends Avu {

    String OPERATION_ATTR = "rdf:type";
    String TOPIC_ATTR = "http://edamontology.org/has_topic";
}
