package org.iplantc.de.server.services;

import org.iplantc.de.server.services.IplantEmailClient.MessageRequest;
import org.iplantc.de.shared.services.EmailService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * A servlet for sending simple emails. The server address is read from the mail.smtp.host property.
 *
 * @author hariolf, jstroot
 */
public class EmailServiceImpl extends RemoteServiceServlet implements EmailService {
    private static final long serialVersionUID = -3893564670515471591L;
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);
    /**
     * The client used to send message requests to the iPlant email services.
     */
    private IplantEmailClient client;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
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

    @Autowired
    public void setClient(IplantEmailClient client) {
        this.client = client;
        LOG.trace("Set client = {}", client.getClass().getSimpleName());
    }
}
