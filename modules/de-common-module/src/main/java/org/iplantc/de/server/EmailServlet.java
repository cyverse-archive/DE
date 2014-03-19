package org.iplantc.de.server;

import org.iplantc.de.server.service.IplantEmailClient;
import org.iplantc.de.server.service.IplantEmailClient.MessageRequest;
import org.iplantc.de.shared.services.EmailService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import javax.servlet.ServletException;

/**
 * A servlet for sending simple emails. The server address is read from the mail.smtp.host property.
 *
 * @author hariolf
 *
 */
public class EmailServlet extends RemoteServiceServlet implements EmailService {
    private static final long serialVersionUID = -3893564670515471591L;

    /**
     * The client used to send message requests to the iPlant email service.
     */
    private IplantEmailClient client;

    /**
     * The default constructor.
     */
    public EmailServlet() {}

    /**
     * @param client the client used to send message requests to the iPlant email service.
     */
    public EmailServlet(IplantEmailClient client) {
        this.client = client;
    }

    /**
     * Initializes the servlet.
     *
     * @throws ServletException if the servlet can't be initialized.
     * @throws IllegalStateException if the iPlant e-mail client bean isn't defined.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        if (client == null) {
            client = IplantEmailClient.getClient(getServletContext());
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @return
     */
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
