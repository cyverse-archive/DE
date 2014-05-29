/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.layout;

import org.iplantc.de.client.views.windows.IPlantWindowInterface;

import com.google.gwt.dom.client.Element;

import com.sencha.gxt.core.client.util.Point;

import java.util.Random;

public class CenterDesktopLayout extends LimitedDesktopLayout implements DesktopLayout {

    private static final int MINIMUM = 50;
    private static final int VARIANCE = 50;

    private int left;
    private int top;

    private final Random random = new Random();

    @Override
    public DesktopLayoutType getDesktopLayoutType() {
        return DesktopLayoutType.CENTER;
    }

    @Override
    public void layoutDesktop(IPlantWindowInterface requestWindow, RequestType requestType,
            Element element, Iterable<IPlantWindowInterface> windows, int containerWidth,
            int containerHeight) {

        if (requestType == RequestType.LAYOUT || requestType == RequestType.RESIZE) {
            left = MINIMUM;
            top = MINIMUM;
        }

        super.layoutDesktop(requestWindow, requestType, element, windows, containerWidth,
                containerHeight);
    }

    @Override
    protected void layoutWindow(IPlantWindowInterface window, int containerWidth, int containerHeight,
            int width, int height) {

        int offset = window.getHeaderOffSetHeight();

        if (((left + VARIANCE + width) > containerWidth)
                || ((top + VARIANCE + height) > containerHeight)) {
            left = MINIMUM;
            top = MINIMUM;
        }

        left += offset + random.nextInt(VARIANCE);
        top += offset + random.nextInt(VARIANCE);

        boolean maximized = window.isMaximized();
        window.setMaximized(false);

        Point position = window.adjustPositionForView(new Point(left, top));
        window.setPosition(position.getX(), position.getY());

        window.setMaximized(maximized);
    }

}
