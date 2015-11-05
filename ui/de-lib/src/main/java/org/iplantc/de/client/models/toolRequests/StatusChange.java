package org.iplantc.de.client.models.toolRequests;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Models a change record for a tool's request status.
 */
public interface StatusChange {

    String getComments();

    String getStatus();

    @PropertyName("status_date")
    String getChangeTime();

    @PropertyName("updated_by")
    String getChanger();

}
