package org.iplantc.de.client.models.pipelines;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.integration.FileParameters;

import com.google.gwt.user.client.ui.HasName;

import java.util.List;

/**
 * An AutoBean interface for a service Template provided in service Pipeline JSON.
 * 
 * @author psarando
 * 
 */
public interface ServicePipelineTask extends HasId, HasName, HasDescription {

    public List<FileParameters> getInputs();

    public void setInputs(List<FileParameters> inputs);

    public List<FileParameters> getOutputs();

    public void setOutputs(List<FileParameters> outputs);
}
