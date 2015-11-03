package org.iplantc.de.server.services;

import org.iplantc.de.server.services.IplantEmailClient.MessageRequest;
import org.iplantc.de.shared.services.EmailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet for sending simple emails. The server address is read from the mail.smtp.host property.
 *
 * @author hariolf, jstroot
 */
public class EmailServiceImpl implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);
    /**
     * The client used to send message requests to the iPlant email services.
     */
    private IplantEmailClient client;

    public EmailServiceImpl(final IplantEmailClient client){
        this.client = client;
    }

    @Override
    public void sendEmail(String subject, String message, String fromAddress, String toAddress) {
        MessageRequest request = new MessageRequest()
                                     .setSubject(subject)
                                     .setContent(message)
                                     .setFromAddress(fromAddress)
                                     .setToAddress(toAddress);
        client.sendMessage(request);
    }
}
