package org.iplantc.de.client.models.analysis.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisPermission {

    @AutoBean.PropertyName("analysis_id")
    String getId();

    @AutoBean.PropertyName("analysis_id")
    void setId(String id);

    void setPermission(String permission);

    String getPermission();

}
