package org.iplantc.de.client.models.apps;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

public interface AppDoc {

    @PropertyName("app_id")
    String getAppId();

    @PropertyName("documentation")
    String getDocumentaion();

    @PropertyName("created_on")
    String getCreatedOn();

    @PropertyName("created_by")
    String getCreatedBy();

    @PropertyName("modified_on")
    String getModifiedBy();

}
