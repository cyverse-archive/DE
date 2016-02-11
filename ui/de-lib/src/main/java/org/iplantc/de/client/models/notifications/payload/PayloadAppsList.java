package org.iplantc.de.client.models.notifications.payload;

import com.google.web.bindery.autobean.shared.AutoBean;

import java.util.List;

/**
 * Created by sriram on 2/4/16.
 */
public interface PayloadAppsList {

    String getAction();

    @AutoBean.PropertyName("apps")
    List<PayloadApps> getApps();
}
