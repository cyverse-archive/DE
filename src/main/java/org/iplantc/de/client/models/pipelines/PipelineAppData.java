package org.iplantc.de.client.models.pipelines;

import com.google.gwt.user.client.ui.HasName;

/**
 * An AutoBean interface for Pipeline App Inputs and Outputs.
 * 
 * @author psarando
 *
 */
public interface PipelineAppData extends HasName {

    public String getId();

    public void setId(String id);

    public String getDescription();

    public void setDescription(String description);

    public boolean getRequired();

    public void setRequired(boolean required);

    public String getFormat();

    public void setFormat(String format);
}
