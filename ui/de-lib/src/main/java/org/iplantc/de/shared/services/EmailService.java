package org.iplantc.de.shared.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * A service for sending simple emails.
 * 
 * @author hariolf
 * @author jstroot
 * 
 */
@RemoteServiceRelativePath("email.rpc")
public interface EmailService extends RemoteService {

    /**
     * Sends an email to one recipient.
     * 
     * @param subject the email subject
     * @param message the email message
     * @param fromAddress the from address
     * @param toAddress the recipient
     */
    void sendEmail(String subject, String message, String fromAddress, String toAddress);
}
