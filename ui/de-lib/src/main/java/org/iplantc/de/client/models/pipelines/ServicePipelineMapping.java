package org.iplantc.de.client.models.pipelines;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Map;

/**
 * An AutoBean interface for a service Pipeline step input to output mapping.
 * 
 * @author psarando
 * 
 */
public interface ServicePipelineMapping {

    @PropertyName("source_step")
    public Integer getSourceStep();

    @PropertyName("source_step")
    public void setSourceStep(Integer source_step);

    @PropertyName("target_step")
    public Integer getTargetStep();

    @PropertyName("target_step")
    public void setTargetStep(Integer target_step);

    public Map<String, String> getMap();

    public void setMap(Map<String, String> map);
}
