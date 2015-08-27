package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasDescription;
import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;

import java.util.List;

public interface MetadataTemplateAttribute extends HasId, HasName,
		HasDescription {

	String getType();

	void setType(String type);

    void setRequired(Boolean required);

    Boolean isRequired();

    // Applicable only for Enum type
    List<TemplateAttributeSelectionItem> getValues();

    // Applicable only for Enum type
    void setValues(List<TemplateAttributeSelectionItem> values);

}
