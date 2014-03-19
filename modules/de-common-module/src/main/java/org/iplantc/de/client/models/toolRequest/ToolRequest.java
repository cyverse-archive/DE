package org.iplantc.de.client.models.toolRequest;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/
 * dfc0b110e73a40229762033ffeb267a9b10373bc
 * /doc/endpoints/app-metadata/tool-requests.md#listing-tool-requests
 * 
 * 
 * Reference the link above
 * 
 * @author jstroot
 * 
 */
public interface ToolRequest extends HasName, HasId {
    @PropertyName("uuid")
    void setId(String id);

    @PropertyName("date_submitted")
    Date getDateSubmitted();

    @PropertyName("date_updated")
    Date getDateUpdated();

    @PropertyName("date_updated")
    void setDateUpdated(Date dateUpdated);

    String getStatus();

    void setStatus(String status);

    @PropertyName("updated_by")
    String getUpdatedBy();

    @Override
    @PropertyName("uuid")
    String getId();

    String getVersion();

}
