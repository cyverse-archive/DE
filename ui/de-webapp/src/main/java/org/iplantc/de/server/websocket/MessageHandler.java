package org.iplantc.de.server.websocket;

import org.iplantc.de.server.util.CasUtils;
import org.iplantc.de.server.websocket.ReceiveNotificationsDirect;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.atmosphere.websocket.WebSocketHandlerAdapter;
import org.atmosphere.websocket.WebSocketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by sriram on 4/8/16.
 */
public abstract class MessageHandler extends WebSocketHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    protected ReceiveNotificationsDirect notificationReceiver;

    /**
     *
     * Bind channel to specific queue
     * @param username
     * @param msgChannel
     * @return  Queue name
     */
    public abstract String bindQueue(String username, Channel msgChannel);


    @Override
    public void onOpen(WebSocket webSocket) throws IOException {
        notificationReceiver = new ReceiveNotificationsDirect();
        logger.info("^^^^^^^^^Web socket connection opened!^^^^^");
        String username = getUserName(webSocket);
        logger.info("**************user info***********" + username);

        final Channel msgChannel = notificationReceiver.createChannel();
        String queue = bindQueue(username, msgChannel);
        consumeMessage(msgChannel, queue, webSocket);

        webSocket.resource().addEventListener(new WebSocketEventListenerAdapter() {
            @Override
            public void onDisconnect(AtmosphereResourceEvent event) {
                if (event.isCancelled()) {
                    logger.error("unexpectedly disconnected",
                                 event.getResource().uuid());
                } else if (event.isClosedByClient()) {
                    logger.error("client closed the connection",
                                 event.getResource().uuid());
                }
                try {
                    if (msgChannel != null) {
                        msgChannel.abort();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("^^^^^^^^^^^ exception aborting channel! ^^^^^^^^");

                }
            }
        });

    }


    @Override
    public void onClose(WebSocket webSocket) {
        logger.info("^^^^^^^^^connection closed!^^^^^");
    }

    @Override
    public void onError(WebSocket webSocket, WebSocketProcessor.WebSocketException t) {
        logger.error("^^^^^^^^^websocket connection error!^^^^^");
    }


    protected final String getUserName(WebSocket webSocket) {
        return CasUtils.attributePrincipalFromServletRequest(webSocket.resource().getRequest())
                       .getName();
    }

    protected void consumeMessage(Channel msgChannel,String queue, final WebSocket webSocket) {
        Consumer consumer = new DefaultConsumer(msgChannel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                logger.info("New message to consume: " + message);
                webSocket.write(message);
            }
        };

        notificationReceiver.consumeMessage(msgChannel, consumer, queue);
    }

}
