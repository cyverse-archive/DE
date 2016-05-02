package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author aramsey
 */
public interface OntologyHierarchyFilterReq {

    @PropertyName("target-ids")
    List<String> getTargetIds();

    @PropertyName("target-ids")
    void setTargetIds(List<String> ids);

    @PropertyName("target-types")
    List<String> getTargetTypes();

    @PropertyName("target-types")
    void setTargetTypes(List<String> types);

}
