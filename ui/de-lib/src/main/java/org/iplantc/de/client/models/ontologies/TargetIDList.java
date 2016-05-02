package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.*;

import java.util.List;

/**
 * @author aramsey
 */
public interface TargetIDList {

    @PropertyName("target-ids")
    List<String> getTargetIds();

    @PropertyName("target-ids")
    void setTargetIds(List<String> ids);
}
