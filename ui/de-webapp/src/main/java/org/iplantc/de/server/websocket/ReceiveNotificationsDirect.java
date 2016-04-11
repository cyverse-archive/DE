package org.iplantc.de.server.websocket;

import org.iplantc.de.server.websocket.amqp.AMQPConnectionManager;
import org.iplantc.de.server.websocket.util.PropertiesUtil;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by sriram on 4/5/16.
 */


public class ReceiveNotificationsDirect {

    public static final String NOTIFICATION_ROUTING_KEY = "notification.";

    public static final String SYSTEM_MESSAGE_ROUTING_KEY = "system_message";

    private Properties props = PropertiesUtil.getDEProperties();

    private final Logger LOG = LoggerFactory.getLogger(ReceiveNotificationsDirect.class);

    private final Connection connection;

    /**
     * Instantiate new
     */
    public ReceiveNotificationsDirect() {
       connection = AMQPConnectionManager.getInstance().getConnection();
       LOG.info("********** amqp Connection created created!!!!!!");
    }

    /**
     * Create a channel for receving the notifications
     *
     * @return
     */
    public Channel createChannel() {
        try {
            Channel channel = connection.createChannel();
            LOG.info("********** amqp channel created!");
            return channel;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            LOG.error("***** IO Exception when creating channel*****");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("***** Exception when creating channel*****");
            return null;
        }
    }

    /**
     * Bind the channel to a queue and return the queue name
     *
     * @param msgChannel
     * @param routing_key
     * @return
     */
    public String bind(Channel msgChannel, String routing_key) {
        String queueName = null;
        try {
            queueName = msgChannel.queueDeclare().getQueue();
            msgChannel.queueBind(queueName,
                                 props.getProperty(
                                         "org.iplantc.discoveryenvironment.notification.amqp.exchange.name"),
                                 routing_key);
            LOG.info("******* Binding complete *******");
            return queueName;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("***** IO Exception when binding queue *****");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("******* Exception when binding queue *****");
            return null;
        }

    }

    /**
     *
     * Consume messages on this queue
     *
     * @param msgChannel
     * @param consumer
     * @param queueName
     */
    public void consumeMessage(Channel msgChannel, Consumer consumer, String queueName) {
        try {
            msgChannel.basicConsume(queueName, true, consumer);
            LOG.error("***** comsumer reqistered *****");
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("***** IO Exception when consuming message*****");
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("***** Exception when consuming message*****");
        }
    }

}
