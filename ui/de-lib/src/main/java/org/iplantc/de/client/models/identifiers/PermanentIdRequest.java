package org.iplantc.de.client.models.identifiers;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.diskResources.Folder;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * 
 * 
 * @author sriram
 * 
 */
public interface PermanentIdRequest extends HasId {

    Folder getFolder();

    @PropertyName("original_path")
    String getOriginalPath();

    @PropertyName("requested_by")
    String getRequestedBy();

    @PropertyName("date_submitted")
    Date getDateSubmitted();

    @PropertyName("date_updated")
    Date getDateUpdated();

    @PropertyName("updated_by")
    String getUpatedBy();

    String getStatus();

    void setStatus(String status);

    PermanentIdRequestType getType();
}
