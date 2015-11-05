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

    void getToolRequestDetails(HasId toolRequest, AsyncCallback<ToolRequestDetails> callback);

    void updateToolRequest(String id,
                           ToolRequestUpdate trUpdate,
                           AsyncCallback<ToolRequestDetails> callback);

    void getToolRequests(SortInfo sortInfo, String userName, AsyncCallback<List<ToolRequest>> callback);

}
