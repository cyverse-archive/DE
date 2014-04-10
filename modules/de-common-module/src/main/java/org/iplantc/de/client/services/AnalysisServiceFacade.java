package org.iplantc.de.client.services;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.List;

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
     * Get all the analyses for the current user's workspace
     * @param loadConfig
     * @param callback
     */
    void getAnalyses(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResultBean<Analysis>> callback);

    /**
     * Delete an analysis execution
     * 
     * @param workspaceId unique id for a user's workspace.
     * @param json id of analysis to delete.
     * @param callback executed when RPC call completes.
     */
    void deleteAnalysis(String workspaceId, String json, AsyncCallback<String> callback);

    void renameAnalysis(Analysis analysis, String newName, AsyncCallback<Void> callback);

    /**
     * Stop a currently running analysis
     * 
     * @param analysisId id of the analysis to be stopped.
     * @param callback executed when RPC call completes.
     */
    void stopAnalysis(String analysisId, AsyncCallback<String> callback);

    void getAnalysisParams(String analysisId, AsyncCallback<String> callback);

    void getAnalysisParams(Analysis analysis, AsyncCallback<List<AnalysisParameter>> callback);

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