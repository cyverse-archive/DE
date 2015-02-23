package org.iplantc.de.analysis.client.gin.factory;

import org.iplantc.de.analysis.client.AnalysesView;
import org.iplantc.de.analysis.client.AnalysisToolBarView;
import org.iplantc.de.client.models.analysis.Analysis;

import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;

/**
 * Created by jstroot on 2/19/15.
 * @author jstroot
 */
public interface AnalysisToolBarFactory {
    AnalysisToolBarView create(AnalysesView.Presenter presenter,
                               PagingLoader<FilterPagingLoadConfig, PagingLoadResult<Analysis>> loader);
}
