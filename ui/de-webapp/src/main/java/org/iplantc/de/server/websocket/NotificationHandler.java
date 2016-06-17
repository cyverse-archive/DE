package org.iplantc.de.server.websocket;

import org.atmosphere.config.service.WebSocketHandlerService;
import org.atmosphere.util.SimpleBroadcaster;

import com.rabbitmq.client.Channel;

/**
 * Created by sriram on 3/31/16.
 */


@WebSocketHandlerService(path = "/de/websocket/notifications", broadcaster = SimpleBroadcaster.class,
                         atmosphereConfig = {
                                 "org.atmosphere.websocket.WebSocketProtocol=org.atmosphere.websocket.protocol.SimpleHttpProtocol" })
public class NotificationHandler extends MessageHandler {

 
    @Override
    public String bindQueue(String username, Channel msgChannel) {
        return notificationReceiver.bind(msgChannel,
                                         ReceiveNotificationsDirect.NOTIFICATION_ROUTING_KEY
                                         + username);
    }




}
