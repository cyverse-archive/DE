package org.iplantc.de.client.models.identifiers;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * 
 * 
 * @author sriram
 * 
 */
public interface PermanentIdRequestStatusHistory {

    String getStatus();

    @PropertyName("updated_by")
    String getUpdatedBy();

    @PropertyName("status_date")
    Date getStatusDate();

    String getComments();

}
