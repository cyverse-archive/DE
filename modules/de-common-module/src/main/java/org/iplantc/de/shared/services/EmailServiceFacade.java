package org.iplantc.de.shared.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * A service for sending simple emails (service facade).
 * 
 * @author hariolf
 * 
 */
public class EmailServiceFacade {
    private static EmailServiceFacade service;

    private EmailServiceAsync proxy;

    private EmailServiceFacade() {
        final String SESSION_SERVICE = "email"; //$NON-NLS-1$

        proxy = (EmailServiceAsync)GWT.create(EmailService.class);
        ((ServiceDefTarget)proxy).setServiceEntryPoint(GWT.getModuleBaseURL() + SESSION_SERVICE);
    }

    /**
     * Retrieve service facade singleton instance.
     * 
     * @return a singleton instance of the service facade.
     */
    public static EmailServiceFacade getInstance() {
        if (service == null) {
            service = new EmailServiceFacade();
        }

        return service;
    }

    /**
     * Sends an email to one recipient.
     * 
     * @param subject the email subject
     * @param message the email message
     * @param fromAddress the from address
     * @param toAddress the recipient
     * @param callback called after the service call finishes
     */
    public void sendEmail(String subject, String message, String fromAddress, String toAddress,
            AsyncCallback<Void> callback) {
        proxy.sendEmail(subject, message, fromAddress, toAddress, callback);
    }
}
