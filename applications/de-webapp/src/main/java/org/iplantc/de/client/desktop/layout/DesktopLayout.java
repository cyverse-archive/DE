/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.layout;

import org.iplantc.de.client.views.windows.IPlantWindowInterface;

import com.google.gwt.dom.client.Element;


public interface DesktopLayout {

    public enum RequestType {
        OPEN, HIDE, SHOW, RESIZE, LAYOUT
    }

    static final int PREFERRED_WIDTH = 600;
    static final int PREFERRED_HEIGHT = 375;
    static final int PREFERRED_MAX_WIDTH_PCT = 80;

    static final int PREFERRED_MAX_HEIGHT_PCT = 80;

    DesktopLayoutType getDesktopLayoutType();

    /**
     * Requests a layout of the desktop as indicated by the specified values.
     * 
     * @param requestWindow the window that was responsible for the request, or null if the request is
     *            not window specific
     * @param requestType the type of layout request
     * @param element the desktop element to be used for positioning
     * @param windows a list of all windows on the desktop
     * @param width the desktop width
     * @param height the desktop height
     */
    void layoutDesktop(IPlantWindowInterface requestWindow, RequestType requestType, Element element,
            Iterable<IPlantWindowInterface> windows, int width, int height);

}
