package org.iplantc.de.client.models.analysis.sharing;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.sharing.UserPermission;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 3/8/16.
 */
public interface AnalysisUserPermissions extends HasId, HasName{

    @AutoBean.PropertyName("permissions")
    List<UserPermission> getPermissions();
}
