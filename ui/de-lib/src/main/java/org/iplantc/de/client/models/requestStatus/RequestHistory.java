package org.iplantc.de.client.models.requestStatus;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * A Status History AutoBean for Request.
 * 
 * @author psarando
 * 
 */
public interface RequestHistory {

    String getStatus();

    @PropertyName("updated_by")
    String getUpdatedBy();

    @PropertyName("status_date")
    Date getStatusDate();

    String getComments();
}
