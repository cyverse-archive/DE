package org.iplantc.de.server.websocket;

import com.rabbitmq.client.Channel;

import org.atmosphere.config.service.WebSocketHandlerService;
import org.atmosphere.util.SimpleBroadcaster;

/**
 * Created by sriram on 4/8/16.
 */
@WebSocketHandlerService(path = "/de/websocket/system-messages", broadcaster = SimpleBroadcaster.class,
                         atmosphereConfig = {
                                 "org.atmosphere.websocket.WebSocketProtocol=org.atmosphere.websocket.protocol.SimpleHttpProtocol" })
public class SystemMessageHandler extends MessageHandler {

   @Override
    public String bindQueue(String username, Channel msgChannel) {
        return notificationReceiver.bind(msgChannel,
                                         ReceiveNotificationsDirect.SYSTEM_MESSAGE_ROUTING_KEY);
    }
}
