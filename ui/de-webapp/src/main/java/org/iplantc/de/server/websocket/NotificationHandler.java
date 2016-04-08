package org.iplantc.de.server.websocket;

import org.iplantc.de.server.util.CasUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import org.atmosphere.config.service.WebSocketHandlerService;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.util.SimpleBroadcaster;
import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.atmosphere.websocket.WebSocketHandlerAdapter;
import org.atmosphere.websocket.WebSocketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by sriram on 3/31/16.
 */


@WebSocketHandlerService(path = "/de/websocket/notifications", broadcaster = SimpleBroadcaster.class,
                         atmosphereConfig = {
                                 "org.atmosphere.websocket.WebSocketProtocol=org.atmosphere.websocket.protocol.SimpleHttpProtocol" })
public class NotificationHandler extends MessageHandler {

    private final Logger logger = LoggerFactory.getLogger(NotificationHandler.class);



    @Override
    public String bindQueue(String username, Channel msgChannel) {
        return notificationReceiver.bind(msgChannel,
                                         ReceiveNotificationsDirect.NOTIFICATION_ROUTING_KEY
                                         + username);
    }




}
