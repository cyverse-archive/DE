package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;

public interface AnalysisServiceFacade {

    /**
     * Get all the analyses for a given workspace.
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param loadConfig optional remote paging and sorting configs.
     * @param callback executed when RPC call completes.
     */
    void getAnalyses(String workspaceId, FilterPagingLoadConfig loadConfig, AsyncCallback<String> callback);

    /**
     * Delete an analysis execution
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param json id of analysis to delete.
     * @param callback executed when RPC call completes.
     */
    void deleteAnalysis(String workspaceId, String json, AsyncCallback<String> callback);

    /**
     * Stop a currently running analysis
     * 
     * @param analysisId id of the analysis to be stopped.
     * @param callback executed when RPC call completes.
     */
    void stopAnalysis(String analysisId, AsyncCallback<String> callback);

    void getAnalysisParams(String analysisId, AsyncCallback<String> callback);

    /**
     * Launch a wizard analysis
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param json JSON configuration of analysis to launch.
     * @param callback executed when RPC call completes.
     */
    void launchAnalysis(String workspaceId, String json, AsyncCallback<String> callback);

    /**
     * get json to relaunch an analysis
     * 
     * @param analyisId
     */
    void relaunchAnalysis(HasId analyisId, AsyncCallback<String> callback);

}