package org.iplantc.de.client.models.sysMsgs;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Represents a JSON document containing a list of UUIDS.
 */
public interface IdList {
	
	@PropertyName("uuids")
	List<String> getIds();
	
	@PropertyName("uuids")
	void setIds(List<String> ids);
	
}
