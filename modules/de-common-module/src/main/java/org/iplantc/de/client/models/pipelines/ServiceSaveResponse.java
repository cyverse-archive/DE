package org.iplantc.de.client.models.pipelines;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * An AutoBean interface for a Pipeline save service response.
 * 
 * @author psarando
 * 
 */
public interface ServiceSaveResponse {

    /**
     * @return The list of IDs of Pipelines that were created or updated in the service call.
     */
    @PropertyName("analyses")
    public List<String> getWorkflowIds();
}
