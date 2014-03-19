/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.layout;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;

import com.google.gwt.user.client.Element;

import com.sencha.gxt.core.client.util.Point;

public class CascadeDesktopLayout extends LimitedDesktopLayout implements DesktopLayout {

    private Point nextWindowPosition;

    public CascadeDesktopLayout() {
        nextWindowPosition = new Point(0, Constants.CLIENT.deHeaderHeight());
    }

    @Override
    public DesktopLayoutType getDesktopLayoutType() {
        return DesktopLayoutType.CASCADE;
    }

    @Override
    public void layoutDesktop(IPlantWindowInterface requestWindow, RequestType requestType,
            Element element, Iterable<IPlantWindowInterface> windows, int containerWidth,
            int containerHeight) {

        if (requestType == RequestType.LAYOUT || requestType == RequestType.RESIZE) {
            nextWindowPosition.setX(0);
            nextWindowPosition.setY(Constants.CLIENT.deHeaderHeight());
        }

        super.layoutDesktop(requestWindow, requestType, element, windows, containerWidth,
                containerHeight);
    }

    @Override
    protected void layoutWindow(IPlantWindowInterface window, int containerWidth, int containerHeight,
            int width, int height) {

        if (nextWindowPosition.getX() + width > containerWidth) {
            nextWindowPosition.setX(0);
        }
        if (nextWindowPosition.getY() + height > containerHeight) {
            nextWindowPosition.setY(Constants.CLIENT.deHeaderHeight());
        }

        boolean maximized = window.isMaximized();
        window.setMaximized(false);

        nextWindowPosition = window.adjustPositionForView(nextWindowPosition);
        window.setPosition(nextWindowPosition.getX(), nextWindowPosition.getY());

        window.setMaximized(maximized);

        int headerOffset = window.getHeaderOffSetHeight();
        nextWindowPosition.setX(nextWindowPosition.getX() + headerOffset);
        nextWindowPosition.setY(nextWindowPosition.getY() + headerOffset);
    }

}
