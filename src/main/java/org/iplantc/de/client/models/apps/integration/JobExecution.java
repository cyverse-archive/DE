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

    @PropertyName("analysis_id")
    String getAppTemplateId();

    @PropertyName("analysis_id")
    void setAppTemplateId(String appTemplateId);

    AppTemplate getAppTemplate();

    void setAppTemplate(AppTemplate at);

    String getJobType();

    void setJobType(String jobType);

    @PropertyName("debug")
    Boolean isRetainInputs();

    @PropertyName("debug")
    void setRetainInputs(Boolean retainInputs);

    String getWorkspaceId();

    void setWorkspaceId(String workspaceId);

    @PropertyName("notify")
    Boolean isEmailNotificationEnabled();

    @PropertyName("notify")
    void setEmailNotificationEnabled(Boolean emailNotificationEnabled);

    Boolean isCreateOutputSubfolder();

    void setCreateOutputSubfolder(Boolean createOutputSubfolder);

    String getOutputDirectory();

    void setOutputDirectory(String outputDirectory);
}
