package org.iplantc.de.client.services.stubs;

import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.services.AnalysisServiceFacade;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

import java.util.List;

public class AnalysisServiceFacadeStub implements AnalysisServiceFacade {
    @Override
    public void getAnalyses(FilterPagingLoadConfig loadConfig, AsyncCallback<PagingLoadResultBean<Analysis>> callback) {

    }

    @Override
    public void deleteAnalyses(List<Analysis> analysesToDelete, AsyncCallback<String> callback) {

    }

    @Override
    public void renameAnalysis(Analysis analysis, String newName, AsyncCallback<Void> callback) {

    }

    @Override
    public void stopAnalysis(Analysis analysis, AsyncCallback<String> callback) {

    }

    @Override
    public void getAnalysisParams(Analysis analysis, AsyncCallback<List<AnalysisParameter>> callback) {

    }

    @Override
    public void updateAnalysisComments(Analysis analysis, String newComment, AsyncCallback<Void> callback) {

    }
}
