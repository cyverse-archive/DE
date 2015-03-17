package org.iplantc.de.client.models.sysMsgs;

import com.google.gwt.core.shared.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * The autobean factory for the notification agent system messages JSON responses.
 */
public interface MessageFactory extends AutoBeanFactory {

    /**
     * a default instance of the factory to use
     */
	static final MessageFactory INSTANCE = GWT.create(MessageFactory.class);

    /**
     * the autobean for describing a list of system message Ids.
     */
	AutoBean<IdList> makeIdList();

    /**
     * wraps an existing list of system messages Ids as an autobean.
     * 
     * @param lst the list to wrap
     */
	AutoBean<IdList> makeIdList(IdList lst);

    /**
     * the autobean for a single system message
     */
	AutoBean<Message> makeMessage();
	
    /**
     * the autobean for a list of system messages
     */
	AutoBean<MessageList> makeMessageList();

    /**
     * wraps an existing list of system messages as an autobean
     * 
     * @param lst the list to wrap
     */
	AutoBean<MessageList> makeMessageList(MessageList lst);
	
    /**
     * the autobean for a user name
     */
	AutoBean<User> makeUser();

}
