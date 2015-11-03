package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;
import com.google.web.bindery.autobean.shared.Splittable;

/**
 * AutoBean interfaces for the data service "restore" endpoint response.
 * 
 * @author psarando
 * 
 */
public interface RestoreResponse {

    public interface RestoredResource {
        @PropertyName("partial-restore")
        boolean isPartialRestore();
    }

    Splittable getRestored();
}
