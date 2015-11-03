package org.iplantc.de.admin.desktop.client.toolRequest.service;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.toolRequest.ToolRequest;
import org.iplantc.de.client.models.toolRequest.ToolRequestDetails;
import org.iplantc.de.client.models.toolRequest.ToolRequestUpdate;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.SortInfo;

import java.util.List;

/**
 * @author jstroot
 */
public interface ToolRequestServiceFacade {
    String TOOL_REQUESTS = "org.iplantc.services.toolRequests";
    
    String ADMIN_TOOL_REQUESTS = "org.iplantc.services.admin.toolRequests";

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/Donkey/blob/master/doc/endpoints/app-metadata.md#listing-tool-installation-request-details"
     * >Donkey Doc</a><br/>
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-metadata/tool-requests.md#obtaining-tool-request-details"
     * >Metadactyl-clj Doc</a>
     */
    void getToolRequestDetails(HasId toolRequest, AsyncCallback<ToolRequestDetails> callback);

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/Donkey/blob/master/doc/endpoints/app-metadata.md#updating-a-tool-installation-request-administrator"
     * >Donkey Doc</a><br/>
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-metadata/tool-requests.md#updating-the-status-of-a-tool-request"
     * >Metadactyl-clj Doc</a>
     */
    void updateToolRequest(String id,
                           ToolRequestUpdate trUpdate,
                           AsyncCallback<ToolRequestDetails> callback);

    /**
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/Donkey/blob/master/doc/endpoints/app-metadata.md#listing-tool-installation-requests"
     * >Donkey
     * Doc</a>
     * <a href=
     * "https://github.com/iPlantCollaborativeOpenSource/metadactyl-clj/blob/master/doc/endpoints/app-metadata/tool-requests.md#listing-tool-requests"
     * >Metadactyl-clj
     * Doc</a>
     */
    void getToolRequests(SortInfo sortInfo, String userName, AsyncCallback<List<ToolRequest>> callback);

}
