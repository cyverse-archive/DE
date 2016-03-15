package org.iplantc.de.client.models.analysis.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisSharingRequestList {
    @AutoBean.PropertyName("sharing")
    List<AnalysisSharingRequest> getAnalysisSharingRequestList();

    @AutoBean.PropertyName("sharing")
    void setAnalysisSharingRequestList(List<AnalysisSharingRequest> sharinglist);

}
