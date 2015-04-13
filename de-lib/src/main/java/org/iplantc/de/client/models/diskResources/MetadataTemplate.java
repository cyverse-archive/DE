package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface MetadataTemplate extends MetadataTemplateInfo {

	@PropertyName("attributes")
	List<MetadataTemplateAttribute> getAttributes();

    void setAttributes(List<MetadataTemplateAttribute> attributes);
}
