/**
 * 
 */
package org.iplantc.de.client.models.analysis;

import org.iplantc.de.client.models.apps.integration.SelectionItem;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author sriram, jstroot
 */
public interface AnalysesAutoBeanFactory extends AutoBeanFactory {

    AutoBean<AnalysesList> getAnalysesList();

    AutoBean<Analysis> getAnalyses();

    AutoBean<AnalysisParameter> getAnalysisParam();

    AutoBean<AnalysisParametersList> getAnalysisParamList();

    AutoBean<SelectionItem> getSelectionItem();

    AutoBean<SimpleValue> getSimpleValue();

    AutoBean<AnalysisStepsInfo> getAnalysisStepsInfo();

    AutoBean<AnalysisStep> getAnalysisStep();
}
