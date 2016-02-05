package org.iplantc.de.client.models.notifications.payload;

import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * Created by sriram on 2/4/16.
 */
public interface PayloadApps {

    @AutoBean.PropertyName("app_id")
    String getId();

    @AutoBean.PropertyName("category_id")
    String getCategoryId();

    @AutoBean.PropertyName("app_name")
    String getAppName();

}
