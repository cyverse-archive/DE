package org.iplantc.de.client.models.sysMsgs;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * describes a list of system messages
 */
public interface MessageList {

	@PropertyName("system-messages")
	List<Message> getList();
	
	@PropertyName("system-messages")
	void setList(List<Message> messages);
	
}
