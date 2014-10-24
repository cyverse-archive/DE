package org.iplantc.de.client.models.pipelines;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * An AutoBean interface for a service Pipeline analysis.
 * 
 * @author psarando
 * 
 */
public interface ServicePipeline extends HasId, HasName, HasDescription {

    public List<ServicePipelineStep> getSteps();

    public void setSteps(List<ServicePipelineStep> publishSteps);

    public void setTasks(List<ServicePipelineTask> publishTasks);

    public List<ServicePipelineTask> getTasks();

    public List<ServicePipelineMapping> getMappings();

    public void setMappings(List<ServicePipelineMapping> publishMappings);

    @PropertyName("id")
    public void setId(String id);
}
