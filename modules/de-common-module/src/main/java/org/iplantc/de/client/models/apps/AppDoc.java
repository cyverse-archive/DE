package org.iplantc.de.client.models.apps;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface AppDoc {

    @PropertyName("app_id")
    String getAppId();

    @PropertyName("documentation")
    String getDocumentaion();

    @PropertyName("documentation")
    void setDocumentation(String doc);

    @PropertyName("created_on")
    String getCreatedOn();

    @PropertyName("created_by")
    String getCreatedBy();

    @PropertyName("modified_on")
    String getModifiedBy();

    @PropertyName("references")
    List<String> getReferences();

}
