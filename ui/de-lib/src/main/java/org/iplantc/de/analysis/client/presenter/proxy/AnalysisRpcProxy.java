package org.iplantc.de.analysis.client.presenter.proxy;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.client.models.analysis.Analysis;
import org.iplantc.de.client.services.AnalysisServiceFacade;
import org.iplantc.de.commons.client.ErrorHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;

/**
 * An RpcProxy for the loader used by the {@link org.iplantc.de.analysis.client.views.widget.AnalysisSearchField} and {@link AnalysesView} grid.
 * 
 * @author psarando, jstroot
 * 
 */
public class AnalysisRpcProxy extends RpcProxy<FilterPagingLoadConfig, PagingLoadResult<Analysis>> {
    private class GetAnalysesCallback implements AsyncCallback<PagingLoadResultBean<Analysis>> {
        private final AsyncCallback<PagingLoadResult<Analysis>> callback;

        public GetAnalysesCallback(AsyncCallback<PagingLoadResult<Analysis>> callback) {
            this.callback = callback;
        }

        @Override
        public void onSuccess(PagingLoadResultBean<Analysis> response) {
            callback.onSuccess(response);
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(appearance.analysesRetrievalFailure(), caught);
            callback.onFailure(caught);
        }
    }

    private final AnalysesView.Presenter.Appearance appearance;
    private final AnalysisServiceFacade analysisService;

    @Inject
    AnalysisRpcProxy(final AnalysesView.Presenter.Appearance appearance,
                     final AnalysisServiceFacade analysisService){
        this.appearance = appearance;
        this.analysisService = analysisService;
    }

    @Override
    public void load(final FilterPagingLoadConfig loadConfig, final AsyncCallback<PagingLoadResult<Analysis>> callback) {
        analysisService.getAnalyses(loadConfig, new GetAnalysesCallback(callback));
    }
}
