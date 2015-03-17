package org.iplantc.de.client.models.errorHandling;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface SimpleServiceError {

    @PropertyName("action")
    String getServiceName();

    @PropertyName("error_code")
    String getErrorCode();

    @PropertyName("status")
    String getStatus();
    
    /**
     * XXX SS Moving forward we will have lot of service that will operate on bulk request.
     * The service will determine how much it can handle.
     * 
     * @return
     */
    @PropertyName("limit")
    int getLimit();

    /**
     * XXX JDS This key is only used in one or two error codes and should not be relied upon.
     * TODO CORE-3581 Request consistent error message JSON response format from backend services.
     * 
     * @return
     */
    String getReason();
}
