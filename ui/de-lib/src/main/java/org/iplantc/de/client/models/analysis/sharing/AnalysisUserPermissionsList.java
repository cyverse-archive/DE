package org.iplantc.de.client.models.analysis.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisUserPermissionsList {
    @AutoBean.PropertyName("analyses")
    List<AnalysisUserPermissions> getResourceUserPermissionsList();
}
