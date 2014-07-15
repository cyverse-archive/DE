package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.newDesktop.util.WindowFactory;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;

/**
 * Window manager for the DE desktop.
 *
 * Accepts window configs
 * @author jstroot
 */
public class DesktopWindowManager {

    public void show(final WindowConfig config, final boolean updateExistingWindow) {

        // Creates window if a window matching the given config isn't found.

        Window window = getWindow(config);
        if(window != null){
            if(updateExistingWindow && (window instanceof IPlantWindowInterface)){
                ((IPlantWindowInterface)window).update(config);
            }

        } else {
            // Create window
            window = (Window) WindowFactory.build(config);
        }

        if(WindowManager.get().getActive() != null){
            final Point position = ((Window)WindowManager.get().getActive()).getElement().getPosition(true);
            position.setX(position.getX() + 10);
            position.setY(position.getY() + 20);

            final Point adjustedPosition = getAdjustedPosition(position, window);
            window.setPagePosition(adjustedPosition.getX(), adjustedPosition.getY());
        }

        window.show();

    }

    Window getWindow(final WindowConfig config){
        String windowId = constructWindowId(config);
        for(Widget w : WindowManager.get().getWindows()){
            String currentId = ((Window)w).getStateId();
            if(windowId.equals(currentId)){
                return (Window) w;
            }
        }
        return null;
    }

    /**
     * Adjusts the given position to account for the given window's size.
     *
     * @param position the desired position, which will be adjusted if necessary.
     * @param window adjusted position is based off of this window's size.
     */
    Point getAdjustedPosition(Point position, Window window) {
        return null;
    }


    String constructWindowId(WindowConfig config) {
        String windowType = config.getWindowType().toString();
        String tag = config.getTag();
        return !Strings.isNullOrEmpty(tag) ? windowType + "_" + tag : windowType;
    }
}
