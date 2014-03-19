package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.events.WindowCloseRequestEvent;
import org.iplantc.de.client.events.WindowCloseRequestEvent.WindowCloseRequestEventHandler;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;

/**
 * Close active window
 * 
 * @author sriram
 * 
 */
public class CloseActiveWindowEventHandler implements WindowCloseRequestEventHandler {

    private Desktop desktop;

    public CloseActiveWindowEventHandler(Desktop desktop) {
        this.desktop = desktop;
    }

    @Override
    public void onWindowCloseRequest(WindowCloseRequestEvent event) {
        IPlantWindowInterface activeWindow = desktop.getActiveWindow();
        if (activeWindow != null) {
            activeWindow.asWidget().removeFromParent();
            desktop.hideWindow(activeWindow);
        }
    }

}
