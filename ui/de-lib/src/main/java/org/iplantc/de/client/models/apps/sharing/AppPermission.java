package org.iplantc.de.client.models.apps.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Created by sriram on 2/3/16.
 */
public interface AppPermission {

    @AutoBean.PropertyName("app_id")
    String getId();

    @AutoBean.PropertyName("app_id")
    void setId(String id);

    void setPermission(String permission);

    String getPermission();
}
