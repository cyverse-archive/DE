package org.iplantc.de.client.utils;

import org.iplantc.de.client.desktop.widget.TaskButton;
import org.iplantc.de.client.factories.WindowFactory;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

import com.google.common.collect.Lists;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent.MinimizeHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages window widgets in the web "desktop" environment. FIXME JDS There is a lot of unnecessary
 * redundancy in this class. More use should be made of {@link IplantWindowManager}
 */
public class DEWindowManager extends IplantWindowManager {
    private IPlantWindowInterface activeWindow;
    private final FastMap<IPlantWindowInterface> windows = new FastMap<IPlantWindowInterface>();
    private Point lastWindowPosition;
    private final ActivateHandler<Window> activateHandler;
    private final DeactivateHandler<Window> deactivateHandler;
    private final HideHandler hideHandler;
    private final MinimizeHandler minimizeHandler;
    private final ShowHandler showHandler;
    private final Map<String, TaskButton> taskButtons;

    /**
     * Instantiate from a window listener.
     * 
     * @param listener window listener.
     */
    public DEWindowManager(ActivateHandler<Window> activateHandler,
            DeactivateHandler<Window> deactivateHandler, HideHandler hideHandler,
            MinimizeHandler minimizeHandler, ShowHandler showHandler) {
        this.activateHandler = activateHandler;
        this.deactivateHandler = deactivateHandler;
        this.hideHandler = hideHandler;
        this.minimizeHandler = minimizeHandler;
        this.showHandler = showHandler;
        taskButtons = new HashMap<String, TaskButton>();
    }

    /**
     * Bring a window to the foreground.
     * 
     * @param window window to set as active.
     */
    public void setActiveWindow(IPlantWindowInterface window) {
        activeWindow = window;
        if (window != null) {
            bringToFront(window.asWidget());
        }
    }

    /**
     * Retrieve the active window.
     * 
     * @return the active window.
     */
    public IPlantWindowInterface getActiveWindow() {
        return activeWindow;
    }

    public <C extends org.iplantc.de.commons.client.views.window.configs.WindowConfig> IPlantWindowInterface add(
            C config) {
        IPlantWindowInterface window = WindowFactory.build(config);

        if (window == null)
            return null;
        String windowStateId = WindowFactory.constructWindowId(config);
        window.setStateId(windowStateId);
        getDEWindows().put(windowStateId, window);
        window.addActivateHandler(activateHandler);
        window.addDeactivateHandler(deactivateHandler);
        window.addHideHandler(hideHandler);
        window.addMinimizeHandler(minimizeHandler);
        window.addShowHandler(showHandler);
        register(window.asWidget());
        positionNextWindow(window);
        return window;
    }

    private void positionNextWindow(IPlantWindowInterface window) {
        if (lastWindowPosition != null) {
            lastWindowPosition.setX(lastWindowPosition.getX() + 10);
            lastWindowPosition.setY(lastWindowPosition.getY() + 20);

            lastWindowPosition = window.adjustPositionForView(lastWindowPosition);

            window.setPagePosition(lastWindowPosition.getX(), lastWindowPosition.getY());
        }
    }

    /**
     * Retrieve a window by tag.
     * 
     * @param tag unique tag for window to retrieve.
     * @return null on failure. Requested window on success.
     */
    public IPlantWindowInterface getWindow(String tag) {
        return getDEWindows().get(tag);
    }

    public <C extends org.iplantc.de.commons.client.views.window.configs.WindowConfig> IPlantWindowInterface getWindow(
            C config) {
        String windowId = WindowFactory.constructWindowId(config);
        return getDEWindows().get(windowId);
    }

    /**
     * Remove a managed window.
     * 
     * @param tag tag of the window to remove.
     */
    public void remove(String tag) {
        IPlantWindowInterface win = getDEWindows().remove(tag);
        unregister(win.asWidget());
        if (getDEWindows().size() == 0) {
            lastWindowPosition = null;
        }
    }

    /**
     * get the no.of open windows in the app
     * 
     * @return
     */
    public int getCount() {
        return getDEWindows().size();
    }

    public void show(IPlantWindowInterface window) {
        if ((window == null) || !getDEWindows().containsValue(window)) {
            return;
        }

        window.show();
        window.toFront();
        if (getCount() == 1) {
            lastWindowPosition = window.getPosition3(true);
        }
    }
    
    public <C extends WindowConfig> void updateAndShow(IPlantWindowInterface window, C config){
        if ((window == null) || !getDEWindows().containsValue(window)) {
            return;
        }
        
        window.update(config);
        show(window);
    }

    /**
     * Set the task button associated with the window
     * 
     * @param tag window tag
     * @param btn taskbutton
     */
    public void setTaskButton(String tag, TaskButton btn) {
        taskButtons.put(tag, btn);
    }

    /**
     * get the task button associated with a window tag
     * 
     * @param tag window tag
     * @return the task button
     */
    public TaskButton getTaskButton(String tag) {
        return taskButtons.get(tag);
    }

    /**
     * @return the windows
     */
    public FastMap<IPlantWindowInterface> getDEWindows() {
        return windows;
    }

    /**
     * get window state for all active windows
     * 
     * @return a list of WindowState objects
     */
    public List<WindowState> getActiveWindowStates() {
        List<WindowState> windowStates = Lists.newArrayList();
        for (IPlantWindowInterface win : windows.values()) {
            windowStates.add(win.getWindowState());
        }
        return windowStates;
    }

    /**
     * A convenience method to get a list of active windows.
     * 
     * @return a list of active IPlant windows
     */
    public List<IPlantWindowInterface> getIplantWindows() {
        List<IPlantWindowInterface> windows = Lists.newArrayList();
        for (Widget w : super.getWindows()) {
            if (w instanceof IPlantWindowInterface) {
                windows.add((IPlantWindowInterface)w);
            }
        }
        return windows;
    }
}
