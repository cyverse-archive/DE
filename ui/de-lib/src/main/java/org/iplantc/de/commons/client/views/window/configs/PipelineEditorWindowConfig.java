package org.iplantc.de.commons.client.views.window.configs;

import org.iplantc.de.client.models.pipelines.Pipeline;

import com.google.web.bindery.autobean.shared.Splittable;

/**
 * @author psarando
 * 
 */
public interface PipelineEditorWindowConfig extends WindowConfig {

    public Pipeline getPipeline();

    public void setPipeline(Pipeline pipeline);

    Splittable getServiceWorkflowJson();

    void setServiceWorkflowJson(Splittable workflowJson);
}
