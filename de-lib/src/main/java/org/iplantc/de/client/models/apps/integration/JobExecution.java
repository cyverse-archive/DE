package org.iplantc.de.client.models.apps.integration;

import org.iplantc.de.client.models.HasDescription;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * 
 * @author jstroot
 * 
 */
public interface JobExecution extends HasName, HasDescription {

    @PropertyName("app_id")
    String getAppTemplateId();

    @PropertyName("app_id")
    void setAppTemplateId(String appTemplateId);

    AppTemplate getAppTemplate();

    void setAppTemplate(AppTemplate at);

    @PropertyName("debug")
    Boolean isRetainInputs();

    @PropertyName("debug")
    void setRetainInputs(Boolean retainInputs);

    @PropertyName("notify")
    Boolean isEmailNotificationEnabled();

    @PropertyName("notify")
    void setEmailNotificationEnabled(Boolean emailNotificationEnabled);

    Boolean isCreateOutputSubfolder();

    void setCreateOutputSubfolder(Boolean createOutputSubfolder);

    @PropertyName("output_dir")
    String getOutputDirectory();

    @PropertyName("output_dir")
    void setOutputDirectory(String outputDirectory);
}
