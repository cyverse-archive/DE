package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * @author aramsey
 */
public interface Ontology {
    String getIri();

    void setIri(String iri);

    String getVersion();

    void setVersion(String version);

    @PropertyName("created_by")
    String getCreatedBy();

    @PropertyName("created_by")
    void setCreatedBy(String name);

    @PropertyName("created_on")
    String getCreatedOn();

    @PropertyName("created_on")
    void setCreatedOn(String date);

}
