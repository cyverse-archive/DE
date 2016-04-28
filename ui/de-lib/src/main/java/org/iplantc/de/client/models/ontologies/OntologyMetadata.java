package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.*;

/**
 * @author aramsey
 */
public interface OntologyMetadata {

    @PropertyName("attr")
    void setAttr(String attribute);

    @PropertyName("attr")
    String getAttr();
}
