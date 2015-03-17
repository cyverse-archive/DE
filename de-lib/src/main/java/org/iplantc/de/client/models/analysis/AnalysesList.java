/**
 * 
 */
package org.iplantc.de.client.models.analysis;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author sriram
 * 
 */
public interface AnalysesList {

    @PropertyName("analyses")
    List<Analysis> getAnalysisList();

    int getTotal();
}
