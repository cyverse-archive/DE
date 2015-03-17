package org.iplantc.de.client.models.pipelines;

import java.util.Map;

/**
 * An AutoBean interface for Pipeline App Input to Output mappings.
 * 
 * @author psarando
 *
 */
public interface PipelineAppMapping {

    public int getStep();

    public void setStep(int step);

    public String getId();

    public void setId(String id);

    public Map<String, String> getMap();

    public void setMap(Map<String, String> map);
}
