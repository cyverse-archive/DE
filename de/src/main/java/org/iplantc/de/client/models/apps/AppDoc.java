package org.iplantc.de.client.models.apps;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * @author jstroot
 */
public interface AppDoc {

    @PropertyName("app_id")
    String getAppId();

    @PropertyName("documentation")
    String getDocumentation();

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
