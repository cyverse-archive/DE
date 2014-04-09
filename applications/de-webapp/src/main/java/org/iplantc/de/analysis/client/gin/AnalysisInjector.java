package org.iplantc.de.analysis.client.gin;

import org.iplantc.de.analysis.client.views.AnalysesView;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

/**
 * Created by jstroot on 4/9/14.
 */
@GinModules(AnalysisModule.class)
public interface AnalysisInjector extends Ginjector {

    AnalysesView.Presenter getAnalysesViewPresenter();
}
