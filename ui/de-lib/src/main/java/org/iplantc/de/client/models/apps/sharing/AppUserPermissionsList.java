package org.iplantc.de.client.models.apps.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 2/3/16.
 */
public interface AppUserPermissionsList {

    @AutoBean.PropertyName("apps")
    List<AppUserPermissions> getResourceUserPermissionsList();
}
