package org.iplantc.de.analysis.client.gin.factory;

import org.iplantc.de.analysis.client.AnalysisParametersView;
import org.iplantc.de.client.models.analysis.AnalysisParameter;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author jstroot
 */
public interface AnalysisParamViewFactory {
    AnalysisParametersView createParamView(ListStore<AnalysisParameter> listStore);
}
