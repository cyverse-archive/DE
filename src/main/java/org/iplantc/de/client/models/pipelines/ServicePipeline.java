package org.iplantc.de.client.models.pipelines;

import java.util.List;

/**
 * An AutoBean interface for a service Pipeline.
 * 
 * @author psarando
 * 
 */
public interface ServicePipeline {

    public List<ServicePipelineAnalysis> getAnalyses();

    public void setAnalyses(List<ServicePipelineAnalysis> analyses);

    public List<ServicePipelineTemplate> getTemplates();

    public void setTemplates(List<ServicePipelineTemplate> templates);
}
