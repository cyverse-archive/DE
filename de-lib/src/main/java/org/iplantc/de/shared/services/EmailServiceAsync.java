package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A service for sending simple emails (asynchronous part).
 * 
 * @author hariolf
 * 
 */
public interface EmailServiceAsync {

    void sendEmail(String subject, String message, String fromAddress, String toAddress, AsyncCallback<Void> callback);
}
