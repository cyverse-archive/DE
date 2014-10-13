package org.iplantc.de.client.models.pipelines;

import org.iplantc.de.client.models.HasDescription;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

/**
 * An AutoBean interface for a service Pipeline step.
 * 
 * @author psarando
 * 
 */
public interface ServicePipelineStep extends HasName, HasDescription {

    @PropertyName("task_id")
    public String getTaskId();

    @PropertyName("task_id")
    public void setTaskId(String task_id);

    @PropertyName("app_type")
    public String getAppType();

    @PropertyName("app_type")
    public void setAppType(String appType);

}
