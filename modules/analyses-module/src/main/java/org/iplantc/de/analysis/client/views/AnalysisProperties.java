/**
 * 
 */
package org.iplantc.de.analysis.client.views;

import org.iplantc.de.client.models.analysis.Analysis;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author sriram
 * 
 */
public interface AnalysisProperties extends PropertyAccess<Analysis> {

    ValueProvider<Analysis, String> name();

    ValueProvider<Analysis, String> appName();

    ValueProvider<Analysis, Long> startDate();

    ValueProvider<Analysis, Long> endDate();

    ValueProvider<Analysis, String> status();
}
