package org.iplantc.de.client.models.analysis.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisUnsharingRequest {
    void setUser(String user);

    @AutoBean.PropertyName("analyses")
    void setAnalyses(List<String> analyses);

    String getUser();

    @AutoBean.PropertyName("analyses")
    List<String> getAnalyses();
}
