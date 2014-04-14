package org.iplantc.de.analysis.client.gin;

import org.iplantc.de.analysis.client.views.AnalysesView;

/**
 * Simple interface declaration intended to be implemented by another injector.
 */
public interface AnalysisInjector {

    AnalysesView.Presenter getAnalysesViewPresenter();
}
