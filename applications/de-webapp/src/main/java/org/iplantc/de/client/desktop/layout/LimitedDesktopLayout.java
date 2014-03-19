/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.layout;

import org.iplantc.de.client.desktop.layout.DesktopLayout.RequestType;
import org.iplantc.de.client.utils.IplantWindowManager;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;

import com.google.gwt.user.client.Element;

public abstract class LimitedDesktopLayout {

    public void layoutDesktop(IPlantWindowInterface requestWindow, RequestType requestType,
            Element element, Iterable<IPlantWindowInterface> windows, int containerWidth,
            int containerHeight) {

        int maxWidth = getPercent(containerWidth, DesktopLayout.PREFERRED_MAX_WIDTH_PCT);
        int maxHeight = getPercent(containerHeight, DesktopLayout.PREFERRED_MAX_HEIGHT_PCT);

        int width = DesktopLayout.PREFERRED_WIDTH;
        int height = DesktopLayout.PREFERRED_HEIGHT;

        width = Math.min(width, maxWidth);
        height = Math.min(height, maxHeight);

        switch (requestType) {
            case HIDE:
            case SHOW:
                // do nothing
                break;
            case LAYOUT:
            case RESIZE:
                for (IPlantWindowInterface window : windows) {
                    layoutWindow(window, containerWidth, containerHeight, width, height);
                    // TODO: Determine why z-order is still messed up on cascade
                    // TODO: sriram fix window manager
                    IplantWindowManager.get().bringToFront(window.asWidget());
                }
                break;
            case OPEN:
                layoutWindow(requestWindow, containerWidth, containerHeight, width, height);
                break;
        }

    }

    protected abstract void layoutWindow(IPlantWindowInterface window, int containerWidth,
            int containerHeight, int width, int height);

    private int getPercent(int value, int percent) {
        return (value * percent) / 100;
    }

}