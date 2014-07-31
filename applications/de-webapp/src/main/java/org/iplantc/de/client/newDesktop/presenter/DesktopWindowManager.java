package org.iplantc.de.client.newDesktop.presenter;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.client.newDesktop.views.widgets.TaskBar;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.client.views.windows.util.WindowFactory;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;

import java.util.List;

/**
 * Window manager for the DE desktop.
 * <p/>
 * Accepts window configs
 *
 * @author jstroot
 */
public class DesktopWindowManager {

    private final WindowFactory windowFactory;
    private final WindowManager windowManager;
    private Element desktopContainer;
    private TaskBar taskBar;

    @Inject
    public DesktopWindowManager(final WindowManager windowManager,
                                final WindowFactory windowFactory) {
        this.windowManager = windowManager;
        this.windowFactory = windowFactory;
    }

    public void closeActiveWindow() {
        final List<Widget> reverse = Lists.reverse(windowManager.getStack());
        for (Widget w : reverse) {
            if (w instanceof IPlantWindowInterface) {
                ((Window) w).hide();
            }
        }
    }

    public void show(final WindowState windowState) {
        final Window window = getOrCreateWindow(getConfig(windowState));
        checkNotNull(window);
        window.setPixelSize(windowState.getWidth(), windowState.getHeight());
        window.setPagePosition(windowState.getWinLeft(), windowState.getWinTop());
        window.show();
    }

    /**
     * Shows the last focused instance of a window of the give type, or creates a new window.
     * This method also provides a 'cycling' affect, where windows of the given WindowType are in
     * focus (on top of the stack) are sent to the back so the next WindowType can be focused.
     * <p/>
     * This method works on the assumption that the {@code Window.getStateId()} begins with the
     * string value of its corresponding {@code WindowType}.
     *
     * @param windowType the window type to be shown
     */
    public void show(WindowType windowType) {
        // Look for existing window type, then show it
        final List<Widget> reverse = Lists.reverse(windowManager.getStack());
        for (Widget w : reverse) {
            Window window = (Window) w;
            if (Strings.nullToEmpty(window.getStateId()).startsWith(windowType.toString())) {
                if (window.isVisible() && (reverse.indexOf(w) == 0) && (reverse.size() > 1)) {
                    // If the window is visible, and on top
                    windowManager.sendToBack(window);
                    continue;
                }
                window.show();
                windowManager.bringToFront(window);
                return;
            }
        }

        // If window type could not be found, create and show one
        show(getDefaultConfig(windowType), false);
    }

    public void show(final WindowConfig config, final boolean updateExistingWindow) {
        // Creates window if a window matching the given config isn't found.
        Window window = getOrCreateWindow(config);
        checkNotNull(window);
        if (updateExistingWindow && (window instanceof IPlantWindowInterface)) {
            ((IPlantWindowInterface) window).update(config);
        }

        if (!window.isVisible() && (windowManager.getActive() != null)) {
            final Point position = ((Window) windowManager.getActive()).getElement().getPosition(true);
            position.setX(position.getX() + 10);
            position.setY(position.getY() + 20);

            final Point adjustedPosition = getAdjustedPosition(position, window);
            window.setPagePosition(adjustedPosition.getX(), adjustedPosition.getY());
        }

        window.show();
        windowManager.bringToFront(window);
    }

    String constructWindowId(WindowConfig config) {
        String windowType = config.getWindowType().toString();
        String tag = config.getTag();
        return !Strings.isNullOrEmpty(tag) ? windowType + "_" + tag : windowType;
    }

    /**
     * Adjusts the given position to account for the given window's size.
     *
     * @param position the desired position, which will be adjusted if necessary.
     * @param window   adjusted position is based off of this window's size.
     */
    Point getAdjustedPosition(Point position, Window window) {
        return position;
    }

    WindowConfig getConfig(final WindowState windowState) {
        return ConfigFactory.getConfig(windowState);
    }

    WindowConfig getDefaultConfig(WindowType windowType) {
        return ConfigFactory.getDefaultConfig(windowType);
    }

    /**
     * Retrieves existing windows which correspond to the given config. If no existing window can be
     * found, this method will attempt to create one.
     * <p/>
     * Method also constructs and set a unique id to the window.
     *
     * @param config an object used to uniquely identify a window.
     * @return the window corresponding to the given config, null is the window could not be found.
     */
    Window getOrCreateWindow(final WindowConfig config) {
        String windowId = constructWindowId(config);
        for (Widget w : windowManager.getWindows()) {
            String currentId = ((Window) w).getStateId();
            if (windowId.equals(currentId)) {
                return (Window) w;
            }
        }
        final Window window = (Window) windowFactory.build(config);
        window.setStateId(constructWindowId(config));
        if (desktopContainer != null) {
            window.setContainer(desktopContainer);
        }
        return window;
    }

    void setDesktopContainer(Element desktopContainer) {
        this.desktopContainer = desktopContainer;
    }

    void setTaskBar(TaskBar taskBar){
        this.taskBar = taskBar;
    }
}
