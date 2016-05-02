package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.*;

/**
 * @author aramsey
 */
public interface OntologyMetadata {

    String OPERATION_ATTR = "rdf:type";
    String TOPIC_ATTR = "http://edamontology.org/has_topic";

    @PropertyName("attr")
    void setAttr(String attribute);

    @PropertyName("attr")
    String getAttr();
}
