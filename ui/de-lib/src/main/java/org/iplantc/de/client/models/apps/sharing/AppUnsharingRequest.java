package org.iplantc.de.client.models.apps.sharing;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 2/3/16.
 */
public interface AppUnsharingRequest {

    void setUser(String user);

    @AutoBean.PropertyName("apps")
    void setApps(List<String> apps);

    String getUser();

    @AutoBean.PropertyName("apps")
    List<String> getApps();
}
