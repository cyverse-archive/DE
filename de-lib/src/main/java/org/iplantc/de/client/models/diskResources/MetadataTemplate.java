package org.iplantc.de.client.models.diskResources;

import org.iplantc.de.client.models.HasId;

import com.google.gwt.user.client.ui.HasName;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

public interface MetadataTemplate extends HasId, HasName {

	@PropertyName("attributes")
	List<MetadataTemplateAttribute> getAttributes();
}
