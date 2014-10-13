package org.iplantc.de.client.models.pipelines;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * An AutoBean interface for a service Pipeline analysis.
 * 
 * @author psarando
 * 
 */
public interface ServicePipelineApp extends HasId, HasDescription {

    @Override
    @PropertyName("id")
    public String getId();

    @PropertyName("id")
    public void setId(String id);

    @PropertyName("name")
    public String getAppName();

    @PropertyName("name")
    public void setAppName(String name);

    public ImplementorDetails getImplementation();

    public void setImplementation(ImplementorDetails implementation);

    @PropertyName("full_username")
    public String getFullUsername();

    @PropertyName("full_username")
    public void setFullUsername(String full_username);

    public List<ServicePipelineStep> getSteps();

    public void setSteps(List<ServicePipelineStep> publishSteps);

    public List<ServicePipelineMapping> getMappings();

    public void setMappings(List<ServicePipelineMapping> publishMappings);
}
