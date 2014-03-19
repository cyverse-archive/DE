package org.iplantc.de.client.models.toolRequest;

import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/
 * dfc0b110e73a40229762033ffeb267a9b10373bc
 * /doc/endpoints/app-metadata/tool-requests.md#updating-the-status-of-a-tool-request
 * 
 * Reference the link above for details.
 * 
 * @author jstroot
 * 
 */
public interface ToolRequestUpdate extends HasId {

    @Override
    @PropertyName("uuid")
    String getId();

    @PropertyName("uuid")
    void setId(String id);

    ToolRequestStatus getStatus();

    void setStatus(ToolRequestStatus status);

    @PropertyName("username")
    String getUserName();

    @PropertyName("username")
    void setUserName(String userName);

    String getComments();

    void setComments(String comments);

}
