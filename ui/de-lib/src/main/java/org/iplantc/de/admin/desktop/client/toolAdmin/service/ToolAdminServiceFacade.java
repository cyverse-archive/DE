package org.iplantc.de.admin.desktop.client.toolAdmin.service;

import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolImportUpdateRequest;
import org.iplantc.de.client.models.tool.ToolImportUpdateRequestList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

/**
 * Created by aramsey on 10/28/15.
 */
public interface ToolAdminServiceFacade {

    void getTools(String searchTerm, AsyncCallback<List<Tool>> callback);

    void getToolDetails(String toolId, AsyncCallback<ToolImportUpdateRequest> callback);

    void addTool(ToolImportUpdateRequestList requestList, AsyncCallback<Void> callback);

    void updateTool(ToolImportUpdateRequest request, AsyncCallback<Void> callback);

    void deleteTool(String toolId, AsyncCallback<Void> callback);

}
