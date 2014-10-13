package org.iplantc.de.client.models.pipelines;

import java.util.List;

/**
 * An AutoBean interface for a service Pipeline.
 * 
 * @author psarando
 * 
 */
public interface ServicePipeline {

    public List<ServicePipelineApp> getApps();

    public void setApps(List<ServicePipelineApp> analyses);

    public List<ServicePipelineTask> getTasks();

    public void setTasks(List<ServicePipelineTask> templates);
}
