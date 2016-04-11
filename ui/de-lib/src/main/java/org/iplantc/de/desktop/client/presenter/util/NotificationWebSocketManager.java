package org.iplantc.de.desktop.client.presenter.util;

import org.iplantc.de.client.DEClientConstants;

import com.google.gwt.core.client.GWT;
import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;


/**
 * Created by sriram on 4/5/16.
 */
public class NotificationWebSocketManager extends WebSocketManager{


    private static NotificationWebSocketManager instance;

    private String socketUrl = getProtocol() + Window.Location.getHost()
                               + deClientConstants.notificationWS();

   private NotificationWebSocketManager() {
        GWT.log("socket url-->" + socketUrl);
        ws = new Websocket(socketUrl);
    }

    public static NotificationWebSocketManager getInstace() {
        if(instance == null) {
            instance = new NotificationWebSocketManager();
        }

        return instance;
    }
}
