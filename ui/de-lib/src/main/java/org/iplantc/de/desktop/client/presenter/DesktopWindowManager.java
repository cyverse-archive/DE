package org.iplantc.de.desktop.client.presenter;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.models.WindowType;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.commons.client.views.window.configs.ConfigFactory;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.desktop.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.desktop.client.views.windows.util.WindowFactory;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;

import java.util.List;
import java.util.Stack;

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

    @Inject
    DesktopWindowManager(final WindowManager windowManager,
                         final WindowFactory windowFactory) {
        this.windowManager = windowManager;
        this.windowFactory = windowFactory;
    }

    public void closeActiveWindow() {
        final List<Widget> reverse = Lists.reverse(windowManager.getStack());
        for (Widget w : reverse) {
            if (w instanceof Window) {
                ((Window) w).hide();
                return;
            }
        }
    }

    public void show(final WindowState windowState) {
        final WindowConfig config = getConfig(windowState);
        String windowId = constructWindowId(config);
        for (Widget w : windowManager.getWindows()) {
            String currentId = ((Window) w).getStateId();
            if (windowId.equals(currentId)) {
                ((IPlantWindowInterface) w).asWindow().show();
                return;
            }
        }
        getOrCreateWindow(config).get(new AsyncCallback<IPlantWindowInterface>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(IPlantWindowInterface window) {

                window.setPixelSize(windowState.getWidth(), windowState.getHeight());
                window.setPagePosition(windowState.getWinLeft(), windowState.getWinTop());
                if (desktopContainer != null) {
                    window.setContainer(desktopContainer);
                }
                window.show(config, constructWindowId(config), true);
            }
        });
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
        Stack<Window> multiWindowStack = new Stack<>();
        // Look for existing window type, then show it
        boolean wasLast = false;
        for (Widget w : windowManager.getStack()) {
            Window window = (Window) w;
            if (Strings.nullToEmpty(window.getStateId()).startsWith(windowType.toString())) {
                multiWindowStack.push(window);
                wasLast = true;
            } else {
                wasLast = false;
            }
        }
        if(!multiWindowStack.isEmpty()){
            Window toFront;
            if((multiWindowStack.size() == 1) || !wasLast){
                toFront = multiWindowStack.pop();
            } else {
                toFront = multiWindowStack.get(0);
            }
            toFront.show();
            windowManager.bringToFront(toFront);
        } else {
            // If window type could not be found, create and show one
            show(getDefaultConfig(windowType), false);
        }

    }

    public void show(final WindowConfig config, final boolean updateExistingWindow) {
        // Creates window if a window matching the given config isn't found.
        String windowId = constructWindowId(config);
        for (Widget w : windowManager.getWindows()) {
            String currentId = ((Window) w).getStateId();
            if (windowId.equals(currentId)) {
                if(updateExistingWindow){
                    ((IPlantWindowInterface) w).update(config);
                } else {
                    // Window already exists, so no need to call other SHOW(config, "", bool) method
                    ((IPlantWindowInterface) w).asWindow().show();
                }
                return;
            }
        }
        getOrCreateWindow(config).get(new AsyncCallback<IPlantWindowInterface>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(IPlantWindowInterface window) {
                if (!window.isVisible() && (windowManager.getActive() != null)) {
                    final Point position = ((Window) windowManager.getActive()).getElement().getPosition(true);
                    position.setX(position.getX() + 10);
                    position.setY(position.getY() + 20);

                    final Point adjustedPosition = getAdjustedPosition(position);
                    window.setPagePosition(adjustedPosition.getX(), adjustedPosition.getY());
                }
                if (desktopContainer != null) {
                    window.setContainer(desktopContainer);
                }

                window.show(config, constructWindowId(config), true);
                moveOutOfBoundsWindow(window);
                windowManager.bringToFront(window.asWindow());
            }
        });
    }

    private void moveOutOfBoundsWindow(IPlantWindowInterface window) {
        final int desktopContainerBottom = desktopContainer.getAbsoluteBottom();
        final int desktopContainerRight = desktopContainer.getAbsoluteRight();
        final int windowRight = window.asWindow().getElement().getAbsoluteRight();
        final int windowBottom = window.asWindow().getElement().getAbsoluteBottom();
        if (windowRight > desktopContainerRight) {
            window.setPagePosition(0, window.getWindowState().getWinTop());
        }
        if (windowBottom > desktopContainerBottom) {
            window.setPagePosition(window.getWindowState().getWinLeft(), 0);
        }
    }

    String constructWindowId(WindowConfig config) {
        String windowType = config.getWindowType().toString();
        String tag = config.getTag();
        return !Strings.isNullOrEmpty(tag) ? windowType + "_" + tag : windowType;
    }

    /**
     * Adjusts the given position to account for the given window's size.
     *  @param position the desired position, which will be adjusted if necessary.
     *
     */
    Point getAdjustedPosition(Point position) {
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
    AsyncProviderWrapper<? extends IPlantWindowInterface> getOrCreateWindow(final WindowConfig config) {
        return windowFactory.build(config);
    }

    void setDesktopContainer(Element desktopContainer) {
        this.desktopContainer = desktopContainer;
    }

}
