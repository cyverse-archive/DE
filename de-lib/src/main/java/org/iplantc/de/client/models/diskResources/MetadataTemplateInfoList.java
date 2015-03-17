package org.iplantc.de.client.models.diskResources;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;


public interface MetadataTemplateInfoList {

	@PropertyName("metadata_templates")
	List<MetadataTemplateInfo> getTemplates();


}
