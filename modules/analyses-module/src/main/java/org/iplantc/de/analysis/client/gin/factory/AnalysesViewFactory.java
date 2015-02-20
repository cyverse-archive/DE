package org.iplantc.de.analysis.client.gin.factory;

import org.iplantc.de.analysis.client.AnalysesView;

/**
 * Created by jstroot on 2/19/15.
 * @author jstroot
 */
public interface AnalysesViewFactory {
    AnalysesView create(AnalysesView.Presenter presenter);
}
