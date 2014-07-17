package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.newDesktop.util.WindowFactory;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.commons.client.views.window.configs.ConfigAutoBeanFactory;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.event.RegisterEvent;
import com.sencha.gxt.widget.core.client.event.UnregisterEvent;

/**
 * Window manager for the DE desktop.
 *
 * Accepts window configs
 * @author jstroot
 */
public class DesktopWindowManager implements UnregisterEvent.UnregisterHandler<Widget>, RegisterEvent.RegisterHandler<Widget> {

    private final WindowManager windowManager;
    private final ConfigAutoBeanFactory factory;

    @Inject
    public DesktopWindowManager(final WindowManager windowManager,
                                final ConfigAutoBeanFactory factory) {
        this.windowManager = windowManager;
        this.factory = factory;
        windowManager.addRegisterHandler(this);
        windowManager.addUnregisterHandler(this);
    }

    @Override
    public void onRegister(RegisterEvent<Widget> event) {

    }

    @Override
    public void onUnregister(UnregisterEvent<Widget> event) {

    }

    public void show(final WindowState windowState){
        final Window window = getOrCreateWindow(getConfig(windowState));
        checkNotNull(window);
        window.setPixelSize(windowState.getWidth(), windowState.getHeight());
        window.setPagePosition(windowState.getWinLeft(), windowState.getWinTop());
        window.show();
    }

    WindowConfig getConfig(final WindowState windowState){
        return ConfigFactory.getConfig(windowState);
    }

    public void show(final WindowConfig config, final boolean updateExistingWindow) {

        // Creates window if a window matching the given config isn't found.

        Window window = getOrCreateWindow(config);
        checkNotNull(window);
        if(updateExistingWindow && (window instanceof IPlantWindowInterface)){
            ((IPlantWindowInterface)window).update(config);
        }

        if(windowManager.getActive() != null){
            final Point position = ((Window)windowManager.getActive()).getElement().getPosition(true);
            position.setX(position.getX() + 10);
            position.setY(position.getY() + 20);

            final Point adjustedPosition = getAdjustedPosition(position, window);
            window.setPagePosition(adjustedPosition.getX(), adjustedPosition.getY());
        }

        window.show();
    }

    /**
     * Retrieves existing windows which correspond to the given config. If no existing window can be
     * found, this method will attempt to create one.
     * @param config an object used to uniquely identify a window.
     * @return the window corresponding to the given config, null is the window could not be found.
     */
    Window getOrCreateWindow(final WindowConfig config){
        String windowId = constructWindowId(config);
        for(Widget w : windowManager.getWindows()){
            String currentId = ((Window)w).getStateId();
            if(windowId.equals(currentId)){
                return (Window) w;
            }
        }
        return (Window) WindowFactory.build(config);
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
