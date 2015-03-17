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
public interface PipelineTask extends HasName {

    @PropertyName("id")
    public String getTaskId();

    @PropertyName("id")
    public void setTaskId(String task_id);

    @PropertyName("app_type")
    public String getAppType();

    @PropertyName("app_type")
    public void setAppType(String appType);

    public String getDescription();

    public void setDescription(String description);

    public Integer getStep();

    public void setStep(Integer step);

    public List<PipelineAppMapping> getMappings();

    public void setMappings(List<PipelineAppMapping> mappings);

    public List<PipelineAppData> getInputs();

    public void setInputs(List<PipelineAppData> inputs);

    public List<PipelineAppData> getOutputs();

    public void setOutputs(List<PipelineAppData> outputs);
}
