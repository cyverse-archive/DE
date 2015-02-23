/**
 * 
 */
package org.iplantc.de.analysis.client.models;

import org.iplantc.de.client.models.analysis.AnalysisParameter;
import org.iplantc.de.client.models.apps.integration.ArgumentType;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 * 
 */
public interface AnalysisParameterProperties extends PropertyAccess<AnalysisParameter> {

    ValueProvider<AnalysisParameter, String> name();

    ValueProvider<AnalysisParameter, ArgumentType> type();
}