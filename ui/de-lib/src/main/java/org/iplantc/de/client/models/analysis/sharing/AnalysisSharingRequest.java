package org.iplantc.de.client.models.analysis.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisSharingRequest {

    String getUser();

    @AutoBean.PropertyName("analyses")
    List<AnalysisPermission> getAnalysisPermissions();

    void setUser(String user);

    @AutoBean.PropertyName("analyses")
    void setAnalysisPermissions(List<AnalysisPermission> appPerms);

}
