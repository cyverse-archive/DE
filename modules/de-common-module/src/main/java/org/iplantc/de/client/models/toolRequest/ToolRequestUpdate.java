package org.iplantc.de.client.models.toolRequest;


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
public interface ToolRequestUpdate {

    String getStatus();

    void setStatus(String status);

    String getComments();

    void setComments(String comments);

}
