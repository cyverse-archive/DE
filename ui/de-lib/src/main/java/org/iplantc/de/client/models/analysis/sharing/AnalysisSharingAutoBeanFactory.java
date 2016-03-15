package org.iplantc.de.client.models.analysis.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisSharingAutoBeanFactory extends AutoBeanFactory {


    AutoBean<AnalysisUserPermissionsList> analysisUserPermissionsList();

    AutoBean<AnalysisPermission> analysisPermission();

    AutoBean<AnalysisSharingRequest> AnalysisSharingRequest();

    AutoBean<AnalysisUnsharingRequest> AnalysisUnsharingRequest();

    AutoBean<AnalysisSharingRequestList> AnalysisSharingRequestList();

    AutoBean<AnalysisUnsharingRequestList> AnalysisUnsharingRequestList();
}
