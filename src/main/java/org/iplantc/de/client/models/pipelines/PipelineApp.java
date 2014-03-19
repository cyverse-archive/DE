package org.iplantc.de.client.models.pipelines;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * An AutoBean interface for a Pipeline App.
 * 
 * @author psarando
 *
 */
public interface PipelineApp extends HasName {

    public String getId();

    public void setId(String id);

    @PropertyName("template_id")
    public String getTemplateId();

    @PropertyName("template_id")
    public void setTemplateId(String template_id);

    public String getDescription();

    public void setDescription(String description);

    public int getStep();

    public void setStep(int step);

    public List<PipelineAppMapping> getMappings();

    public void setMappings(List<PipelineAppMapping> mappings);

    public List<PipelineAppData> getInputs();

    public void setInputs(List<PipelineAppData> inputs);

    public List<PipelineAppData> getOutputs();

    public void setOutputs(List<PipelineAppData> outputs);
}
