package org.iplantc.de.client.models.systemMessages;

import org.iplantc.de.client.models.sysMsgs.MessageFactory;

import com.google.web.bindery.autobean.shared.AutoBean;

public interface SystemMessageFactory extends MessageFactory {

    AutoBean<SystemMessageList> systemMessageList();

    AutoBean<SystemMessageTypesList> systemMessageTypesList();

    AutoBean<SystemMessage> systemMessage();

}
