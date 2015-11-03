package org.iplantc.de.server.services;

import org.iplantc.de.shared.exceptions.ServiceCallFailedException;

import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A client for the iPlant e-mail services.
 *
 * @author Dennis Roberts, jstroot
 */
@Component
public class IplantEmailClient {

    /**
     * Used to log debugging messages.
     */
    private static final Logger LOG = LoggerFactory.getLogger(IplantEmailClient.class);

    @Value("${org.iplantc.services.email-base}")
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        LOG.trace("Set baseUrl = {}", baseUrl);
    }

    /**
     * The base URL to use when connecting to the e-mail services.
     */
    private String baseUrl;

    /**
     * Sends a message.
     *
     * @param request the request used to format the message.
     * @throws ServiceCallFailedException if the request fails.
     */
    public void sendMessage(MessageRequest request) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(baseUrl);
        try {
            post.setEntity(new StringEntity(request.toString()));
            post.setHeader("Content-Type", "application/json");
            String responseBody = client.execute(post, new ServiceResponseHandler());
            LOG.trace(responseBody);
        } catch (IOException e) {
            LOG.error("message request failed", e);
            throw new ServiceCallFailedException(e);
        }
        catch (ServiceCallFailedException e) {
            LOG.error("message request failed", e);
            throw e;
        }
        finally {
            IOUtils.closeQuietly(client);
        }
    }

    /**
     * Represents a simple message request.
     */
    public static final class MessageRequest {

        /**
         * The name of the message template to use. At this time, this iplant-email client is only capable of using the
         * blank template.
         */
        private static final String TEMPLATE_NAME = "blank";

        /**
         * The recipient address for the message.
         */
        private String toAddress;

        /**
         * An e-mail address to send a courtesy copy to.
         */
        private String ccAddress;

        /**
         * The source address for the message.
         */
        private String fromAddress;

        /**
         * The name associated with the source address (optional).
         */
        private String fromName;

        /**
         * The message subject.
         */
        private String subject;

        /**
         * The message text.
         */
        private String contents = "";

        /**
         * Sets the e-mail address to send the message to. This field is required.
         *
         * @param toAddress the e-mail address to send the message to.
         * @return the message request.
         */
        public MessageRequest setToAddress(String toAddress) {
            this.toAddress = toAddress;
            return this;
        }

        /**
         * Sets the e-mail address to send a courtesy copy of the message to.  This field is optional; the message
         * will not be sent to any other recipients if this field is not specified.
         *
         * @param ccAddress the e-mail address t send the courtesy copy to.
         * @return the message request.
         */
        public MessageRequest setCcAddress(String ccAddress) {
            this.ccAddress = ccAddress;
            return this;
        }

        /**
         * Sets the e-mail address to send the message from. This field is required.
         *
         * @param fromAddress the e-mail address to send the message from.
         * @return the message request.
         */
        public MessageRequest setFromAddress(String fromAddress) {
            this.fromAddress = fromAddress;
            return this;
        }

        /**
         * Sets the name to send the message from. This field is optional; if it's not provided then no from name will
         * be associated with the message.
         *
         * @param fromName the name to send the message from.
         * @return the message request.
         */
        public MessageRequest setFromName(String fromName) {
            this.fromName = fromName;
            return this;
        }

        /**
         * Sets the e-mail subject. This field is required.
         *
         * @param subject the message subject.
         * @return the message request.
         */
        public MessageRequest setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Sets the message contents. This field is optional and defaults to the empty string.
         *
         * @param contents the message contents.
         * @return the message request.
         */
        public MessageRequest setContent(String contents) {
            if (contents != null) {
                this.contents = contents;
            }
            return this;
        }

        /**
         * Verifies that required message request settings are actually specified.
         *
         * @param name the name of the request setting.
         * @param value the value of the message request setting.
         * @return the value.
         * @throws IllegalArgumentException if a required setting is missing.
         */
        private String requiredSetting(String name, String value) {
            if (value == null) {
                throw new IllegalArgumentException("required email request setting, " + name + ", missing");
            }
            return value;
        }

        /**
         * Creates the JSON representation of the message request in the format expected by iplant-email.
         *
         * @return the JSON representation of the message request.
         */
        public JSONObject toJson() {
            JSONObject json = new JSONObject();
            json.put("to", requiredSetting("toAddress", toAddress));
            json.put("cc", ccAddress);
            json.put("from-addr", requiredSetting("fromAddress", fromAddress));
            json.put("from-name", fromName);
            json.put("subject", requiredSetting("subject", subject));
            json.put("template", TEMPLATE_NAME);
            json.put("values", createValuesJson());
            return json;
        }

        /**
         * Creates the JSON object to use for the values that are plugged into the e-mail message template.
         *
         * @return the JSON object.
         */
        private JSONObject createValuesJson() {
            JSONObject json = new JSONObject();
            json.put("contents", contents);
            return json;
        }

        /**
         * Returns the string representation of the message request in the format expected by iplant-email.
         *
         * @return the string representation of the message request.
         */
        @Override
        public String toString() {
            return toJson().toString(4);
        }
    }
}
