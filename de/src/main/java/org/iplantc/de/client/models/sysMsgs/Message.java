package org.iplantc.de.client.models.sysMsgs;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.Date;

/**
 * This is the interface of a System message received from the back end.
 * 
 * It is an AutoBean payload.
 */
public interface Message {
	
	/**
	 * Returns the Id of the message
	 * 
	 * @return the message Id.
	 */
	@PropertyName("uuid")
	String getId();
	
	/**
	 * Returns the type of the message
	 * 
	 * @return the message type
	 */
	@PropertyName("type")
	String getType();
	
	/**
	 * Returns the time when the message was.
	 * 	 
	 * @return the time in milliseconds since the POSIX epoch
	 */
	@PropertyName("date_created")
	Date getCreationTime();

	/**
	 * Returns the time when the message becomes available for the user to see.
	 * 
	 * @return the time in milliseconds since the POSIX epoch
	 */
	@PropertyName("activation_date")
	Date getActivationTime();
	
	/**
	 * Returns the time when the message becomes unavailable for the user to see.
	 * 
	 * @return the time in milliseconds since the POSIX epoch
	 */
	@PropertyName("deactivation_date")
	Date getDeactivationTime();

	/**
	 * Indicates whether or not the user has seen this message.
	 * 
	 * @return true if the user has seen the message, otherwise false
	 */
	@PropertyName("acknowledged")
	boolean isSeen();
	
	/**
	 * Sets whether or not the user has seen this message
	 * 
	 * @param seen true to indicate the user has seen the message, otherwise false
	 */
	@PropertyName("acknowledged")
	void setSeen(boolean seen);
	
	/**
	 * Indicates whether or not the user can dismiss this message.
	 * 
	 * @return true indicates the user can dismiss the message and false indicates the opposite.
	 */
	@PropertyName("dismissible")
	boolean isDismissible();
	
	/**
	 * Indicates whether or not the user can log into the DE when this message is active.
	 * 
	 * @return true indicates the user cannot, while false indicates the opposite.
	 */
	@PropertyName("logins_disabled")
	boolean isLoginsDisabled();

	/**
	 * Returns the body of the message.
	 * 
	 * @return the body of the message as text or HTML markup.
	 */
	@PropertyName("message")
	String getBody();
	
}
