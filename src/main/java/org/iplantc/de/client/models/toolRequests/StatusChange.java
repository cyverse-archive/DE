package org.iplantc.de.client.models.toolRequests;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * Models a change record for a tool's request status.
 * 
 * @link https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-
 *       metadata/tool-requests.md#obtaining-tool-request-details
 */
public interface StatusChange {

    String getComments();

    String getStatus();

    @PropertyName("status_date")
    String getChangeTime();

    @PropertyName("updated_by")
    String getChanger();

}
