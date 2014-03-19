package org.iplantc.de.client.models.toolRequest;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * A Status History AutoBean for PayloadToolRequest.
 * 
 * @author psarando
 * 
 */
public interface ToolRequestHistory {

    ToolRequestStatus getStatus();

    @PropertyName("updated_by")
    String getUpdatedBy();

    @PropertyName("status_date")
    Date getStatusDate();

    String getComments();
}
