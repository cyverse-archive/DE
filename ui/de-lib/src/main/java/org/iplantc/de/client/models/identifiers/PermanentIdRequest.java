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

    @PropertyName("requested_by")
    String getUsername();

    @PropertyName("date_submitted")
    Date getCreatedDate();

    @PropertyName("date_updated")
    Date getUpdatedDate();

    @PropertyName("updated_by")
    String getUpatedBy();

    PermanentIdRequestStatus getStatus();

    PermanentIdRequestType getType();
}
