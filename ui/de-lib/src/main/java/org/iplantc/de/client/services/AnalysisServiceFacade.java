package org.iplantc.de.client.services;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.analysis.AnalysisStepsInfo;
import org.iplantc.de.client.models.analysis.sharing.AnalysisSharingRequest;
import org.iplantc.de.client.models.analysis.sharing.AnalysisSharingRequestList;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUnsharingRequest;
import org.iplantc.de.client.models.analysis.sharing.AnalysisUnsharingRequestList;
import org.iplantc.de.client.models.apps.App;
import org.iplantc.de.client.models.apps.sharing.AppSharingRequestList;
import org.iplantc.de.client.models.apps.sharing.AppUnSharingRequestList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.List;

public interface AnalysisServiceFacade {

    /**
     * Get all the analyses for the current user's workspace
     *
     * @param loadConfig optional remote paging and sorting configs.
     * @param callback executed when RPC call completes.
     */
    void getAnalyses(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResultBean<Analysis>> callback);

    /**
     * Delete an analysis execution
     * 
     * @param analysesToDelete the analyses to be deleted.
     * @param callback executed when RPC call completes.
     */
    void deleteAnalyses(List<Analysis> analysesToDelete, AsyncCallback<String> callback);

    /**
     * Renames an analysis.
     *
     * @param analysis the analysis which will be renamed
     * @param newName the new analysis name
     * @param callback executed when RPC call completes.
     */
    void renameAnalysis(Analysis analysis, String newName, AsyncCallback<Void> callback);

    /**
     * Stop a currently running analysis
     * 
     * @param analysisId the analysis to be stopped.
     * @param callback executed when RPC call completes.
     */
    void stopAnalysis(Analysis analysis, AsyncCallback<String> callback);

    void getAnalysisParams(Analysis analysis, AsyncCallback<List<AnalysisParameter>> callback);

    void updateAnalysisComments(Analysis analysis, String newComment, AsyncCallback<Void> callback);

    /**
     * 
     * @sriram: the URL path for the new service will be `/analyses/:analysis-id/steps`
     * @param analysis
     * @param callback
     */
    void getAnalysisSteps(Analysis analysis, AsyncCallback<AnalysisStepsInfo> callback);

    void shareAnalyses(AnalysisSharingRequestList request, AsyncCallback<String> callback);

    void unshareAnalyses(AnalysisUnsharingRequestList request, AsyncCallback<String> callback);

    void getPermissions(List<Analysis> analyses, AsyncCallback<String> callback);

}