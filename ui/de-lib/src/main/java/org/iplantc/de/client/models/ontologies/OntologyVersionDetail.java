package org.iplantc.de.client.models.ontologies;

import com.google.web.bindery.autobean.shared.AutoBean.*;

/**
 * @author aramsey
 */
public interface OntologyVersionDetail {

    String getVersion();

    @PropertyName("applied_by")
    String getAppliedBy();

    String getApplied();
}
