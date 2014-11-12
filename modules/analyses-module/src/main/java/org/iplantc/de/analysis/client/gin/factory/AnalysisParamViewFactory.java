package org.iplantc.de.analysis.client.gin.factory;

import org.iplantc.de.analysis.client.views.widget.AnalysisParamView;
import org.iplantc.de.analysis.client.views.widget.AnalysisParamViewColumnModel;
import org.iplantc.de.client.models.analysis.AnalysisParameter;

import com.sencha.gxt.data.shared.ListStore;

/**
 * @author jstroot
 */
public interface AnalysisParamViewFactory {
    AnalysisParamView createParamView(AnalysisParamViewColumnModel paramViewColumnModel,
                                      ListStore<AnalysisParameter> listStore);
}
