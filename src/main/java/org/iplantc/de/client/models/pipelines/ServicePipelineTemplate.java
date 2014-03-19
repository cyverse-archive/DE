package org.iplantc.de.client.models.pipelines;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.apps.DataObject;

import com.google.gwt.user.client.ui.HasName;

import java.util.List;

/**
 * An AutoBean interface for a service Template provided in service Pipeline JSON.
 * 
 * @author psarando
 * 
 */
public interface ServicePipelineTemplate extends HasId, HasName, HasDescription {

    public List<DataObject> getInputs();

    public void setInputs(List<DataObject> inputs);

    public List<DataObject> getOutputs();

    public void setOutputs(List<DataObject> outputs);
}
