package org.iplantc.de.desktop.client.presenter.util;

import org.iplantc.de.client.DEClientConstants;

import com.sksamuel.gwt.websockets.Websocket;
import com.sksamuel.gwt.websockets.WebsocketListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;

/**
 * Created by sriram on 4/8/16.
 */
public class WebSocketManager {

    protected Websocket ws;

    static DEClientConstants deClientConstants =  GWT.create(DEClientConstants.class);

    public void openWebSocket(WebsocketListener wl) {
        ws.addListener(wl);
        ws.open();
    }

    public void closeWebSocket() {
        ws.close();
    }

    public static String getProtocol() {
        if(Window.Location.getProtocol().equalsIgnoreCase("HTTPS:")) {
            return deClientConstants.wssProtocol();
        }   else {
            return deClientConstants.wsProtocol();
        }
    }
}
