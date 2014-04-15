/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.analysis.client.events.OpenAppForRelaunchEvent;
import org.iplantc.de.analysis.client.events.OpenFolderEvent;
import org.iplantc.de.apps.client.events.CreateNewAppEvent;
import org.iplantc.de.apps.client.events.CreateNewWorkflowEvent;
import org.iplantc.de.apps.client.events.EditAppEvent;
import org.iplantc.de.apps.client.events.EditWorkflowEvent;
import org.iplantc.de.apps.client.events.RunAppEvent;
import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.desktop.layout.CascadeDesktopLayout;
import org.iplantc.de.client.desktop.layout.CenterDesktopLayout;
import org.iplantc.de.client.desktop.layout.DesktopLayout;
import org.iplantc.de.client.desktop.layout.DesktopLayout.RequestType;
import org.iplantc.de.client.desktop.layout.DesktopLayoutType;
import org.iplantc.de.client.desktop.layout.TileDesktopLayout;
import org.iplantc.de.client.events.EventBus;
import org.iplantc.de.client.events.ShowAboutWindowEvent;
import org.iplantc.de.client.events.ShowSystemMessagesEvent;
import org.iplantc.de.client.events.WindowCloseRequestEvent;
import org.iplantc.de.client.events.WindowLayoutRequestEvent;
import org.iplantc.de.client.events.WindowLayoutRequestEvent.WindowLayoutRequestEventHandler;
import org.iplantc.de.client.events.WindowShowRequestEvent;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.client.utils.DEWindowManager;
import org.iplantc.de.client.utils.ShortcutManager;
import org.iplantc.de.client.utils.builders.DefaultDesktopBuilder;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.client.views.windows.configs.ConfigFactory;
import org.iplantc.de.client.views.windows.configs.WindowConfig;
import org.iplantc.de.diskResource.client.events.CreateNewFileEvent;
import org.iplantc.de.diskResource.client.events.RequestBulkDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestBulkUploadEvent;
import org.iplantc.de.diskResource.client.events.RequestImportFromUrlEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleDownloadEvent;
import org.iplantc.de.diskResource.client.events.RequestSimpleUploadEvent;
import org.iplantc.de.diskResource.client.events.ShowFilePreviewEvent;

import com.google.common.collect.Lists;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.shared.FastMap;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer.VBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;
import com.sencha.gxt.widget.core.client.event.ActivateEvent;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.ActivateHandler;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.DeactivateHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent.MinimizeHandler;
import com.sencha.gxt.widget.core.client.event.ShowEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent.ShowHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A desktop represents a desktop like application which contains a task bar, start menu, and shortcuts.
 * <p/>
 * Rather than adding content directly to the root panel, content should be wrapped in windows. Windows
 * can be opened via shortcuts and the start menu.
 * 
 * FIXME JDS Need move functional/non-ui stuff from this class to its p
 * 
 * @see TaskBar
 * @see Shortcut
 */
public class Desktop implements IsWidget {

    /**
     * The default desktop layout type.
     */
    private static final DesktopLayoutType DEFAULT_DESKTOP_LAYOUT_TYPE = DesktopLayoutType.CENTER;

    private VBoxLayoutContainer desktop;
    private TaskBar taskBar;
    private List<Shortcut> shortcuts;
    private WindowHandler handler;
    private IPlantWindowInterface activeWindow;

    private List<HandlerRegistration> eventHandlers = new ArrayList<HandlerRegistration>();

    /**
     * @return the activeWindow
     */
    public IPlantWindowInterface getActiveWindow() {
        return activeWindow;
    }

    private VerticalLayoutContainer desktopContainer;
    private Viewport desktopViewport;
    private DesktopLayout desktopLayout;
    private FastMap<DesktopLayout> desktopLayouts;
    private DEWindowManager windowManager;
    private final EventBus eventBus;

    /**
     * Creates a new Desktop window.
     */
    public Desktop(final DeResources resources, EventBus eventBus) {
        this.eventBus = eventBus;
        initShortcuts();
        initEventHandlers(eventBus);

        initWindowEventHandlers(eventBus);

    }

    /**
     * Adds a shortcut to the desktop.
     * 
     * @param shortcut the shortcut to add
     */
    private void addShortcut(Shortcut shortcut) {
        getShortcuts().add(shortcut);
        getDesktop().add(shortcut, new BoxLayoutData(new Margins(5, 5, 5, 25)));

    }

    /**
     * Initialize handlers related to launching windows
     * 
     * @param eventbus
     */
    private void initWindowEventHandlers(final EventBus eventbus) {
        // Launching Tito and App windows
        ShowWindowEventHandler showWindowHandler = new ShowWindowEventHandler(this);
        CloseActiveWindowEventHandler closeActiveWindowHandler = new CloseActiveWindowEventHandler(this);
        eventHandlers.add(eventbus.addHandler(EditAppEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(CreateNewAppEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(CreateNewWorkflowEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(EditWorkflowEvent.TYPE, showWindowHandler));

        // Launching File Preview windows
        eventHandlers.add(eventbus.addHandler(ShowFilePreviewEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(CreateNewFileEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(ShowAboutWindowEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(WindowShowRequestEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(RunAppEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventBus.addHandler(ShowSystemMessagesEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventbus.addHandler(WindowCloseRequestEvent.TYPE, closeActiveWindowHandler));
        eventHandlers.add(eventbus.addHandler(WindowLayoutRequestEvent.TYPE,
                new WindowLayoutRequestEventHandlerImpl()));
        eventHandlers.add(eventBus.addHandler(OpenAppForRelaunchEvent.TYPE, showWindowHandler));
        eventHandlers.add(eventBus.addHandler(OpenFolderEvent.TYPE, showWindowHandler));
    }

    private void initEventHandlers(final EventBus eventbus) {

        eventHandlers.add(eventbus.addHandler(RequestBulkDownloadEvent.TYPE,
                new DesktopFileTransferEventHandler(this)));
        eventHandlers.add(eventbus.addHandler(RequestBulkUploadEvent.TYPE,
                new DesktopFileTransferEventHandler(this)));
        eventHandlers.add(eventbus.addHandler(RequestImportFromUrlEvent.TYPE,
                new DesktopFileTransferEventHandler(this)));
        eventHandlers.add(eventbus.addHandler(RequestSimpleDownloadEvent.TYPE,
                new DesktopFileTransferEventHandler(this)));
        eventHandlers.add(eventbus.addHandler(RequestSimpleUploadEvent.TYPE,
                new DesktopFileTransferEventHandler(this)));

    }

    public void cleanUp() {
        EventBus eventBus = EventBus.getInstance();
        for (HandlerRegistration hr : eventHandlers) {
            eventBus.removeHandler(hr);
        }
    }

    protected <C extends WindowConfig> void showWindow(final C config) {
        showWindow(config, false);
    }

    protected <C extends WindowConfig> void showWindow(final C config, boolean update) {
        IPlantWindowInterface window = getWindowManager().getWindow(config);
        if (window == null) {
            window = getWindowManager().add(config);
            getWindowManager().show(window);
        } else if (update) {
            getWindowManager().updateAndShow(window, config);
        } else {
            getWindowManager().show(window);
        }

    }

    @Override
    public Widget asWidget() {
        return getDesktopViewport();
    }

    public Viewport getViewPort() {
        return desktopViewport;
    }

    /**
     * Returns the container of the "desktop", which is the area that contains the shortcuts (i.e. minus
     * the task bar).
     * 
     * @return the desktop layout container
     */
    public VBoxLayoutContainer getDesktop() {
        if (desktop == null) {
            desktop = new VBoxLayoutContainer();
            desktop.addStyleName("x-desktop");
            desktop.setPadding(new Padding(5));
            desktop.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCHMAX);
        }
        return desktop;
    }

    /**
     * Returns a list of the desktop's shortcuts.
     * 
     * @return the shortcuts
     */
    public List<Shortcut> getShortcuts() {
        if (shortcuts == null) {
            shortcuts = new ArrayList<Shortcut>();
        }
        return shortcuts;

    }

    /**
     * Returns the desktop's task bar.
     * 
     * @return the task bar
     */
    public TaskBar getTaskBar() {
        if (taskBar == null) {
            taskBar = new TaskBar();
        }

        return taskBar;
    }

    private class WindowLayoutRequestEventHandlerImpl implements WindowLayoutRequestEventHandler {

        @Override
        public void onWindowLayoutRequest(WindowLayoutRequestEvent event) {
            layout(event.getType());
            setDesktopLayoutType(event.getType());
        }

    }

    /**
     * Returns a list of the desktop's windows.
     * 
     * @return the windows
     */
    public DEWindowManager getWindowManager() {
        if (windowManager == null) {
            windowManager = new DEWindowManager(getHandler(), getHandler(), getHandler(), getHandler(),
                    getHandler());
        }
        return windowManager;
    }

    /**
     * Arranges the windows on the desktop using the requested layout manager.
     * 
     * @param desktopLayoutType the type of layout manager to use
     */
    public void layout(DesktopLayoutType desktopLayoutType) {
        layout(getDesktopLayout(desktopLayoutType), null, RequestType.LAYOUT);
    }

    /**
     * Minimizes the window.
     * 
     * @param window the window to minimize
     */
    private void minimizeWindow(IPlantWindowInterface window) {
        window.setMinimized(true);
        window.hide();
    }

    /**
     * Removes a shortcut from the desktop.
     * 
     * @param shortcut the shortcut to remove
     */
    public void removeShortcut(Shortcut shortcut) {
        getShortcuts().remove(shortcut);
        getDesktop().remove(shortcut);
    }

    /**
     * Sets the type of layout manager to use when new windows are added to the desktop, or the desktop
     * size changes.
     * 
     * @param desktopLayoutType the type of layout manager
     */
    public void setDesktopLayoutType(DesktopLayoutType desktopLayoutType) {
        desktopLayout = getDesktopLayout(desktopLayoutType);
    }

    private void initShortcuts() {
        ShortcutManager mgr = new ShortcutManager(new DefaultDesktopBuilder(), eventBus);

        List<Shortcut> shortcuts = mgr.getShortcuts();

        for (Shortcut shortcut : shortcuts) {
            addShortcut(shortcut);
        }
    }

    private DesktopLayout createDesktopLayout(DesktopLayoutType desktopLayoutType) {
        DesktopLayout desktopLayout;
        switch (desktopLayoutType) {
            case CASCADE:
                desktopLayout = new CascadeDesktopLayout();
                break;
            case CENTER:
                desktopLayout = new CenterDesktopLayout();
                break;
            case TILE:
                desktopLayout = new TileDesktopLayout();
                break;
            default:
                throw new IllegalArgumentException("Unsupported desktopLayoutType" + desktopLayoutType);
        }
        return desktopLayout;
    }

    private VerticalLayoutContainer getDesktopContainer() {
        if (desktopContainer == null) {
            desktopContainer = new VerticalLayoutContainer() {
                @Override
                public void onResize() {
                    super.onResize();
                    layout(null, RequestType.RESIZE);
                }

                @Override
                protected void doLayout() {
                    super.doLayout();
                    layout(null, RequestType.LAYOUT);
                }
            };
            desktopContainer.add(getDesktop(), new VerticalLayoutData(-1, 1));
            // desktopContainer.add(buildFooterPanel(), new VerticalLayoutData(1, 20));
            desktopContainer.add(getTaskBar(), new VerticalLayoutData(1, -1));
        }
        return desktopContainer;
    }

    private DesktopLayout getDesktopLayout() {
        if (desktopLayout == null) {
            desktopLayout = getDesktopLayout(DEFAULT_DESKTOP_LAYOUT_TYPE);
        }
        return desktopLayout;
    }

    private DesktopLayout getDesktopLayout(DesktopLayoutType desktopLayoutType) {
        DesktopLayout desktopLayout = getDesktopLayouts().get(desktopLayoutType.name());
        if (desktopLayout == null) {
            desktopLayout = createDesktopLayout(desktopLayoutType);
            getDesktopLayouts().put(desktopLayoutType.name(), desktopLayout);
        }
        return desktopLayout;
    }

    private FastMap<DesktopLayout> getDesktopLayouts() {
        if (desktopLayouts == null) {
            desktopLayouts = new FastMap<DesktopLayout>();
        }
        return desktopLayouts;
    }

    private Viewport getDesktopViewport() {
        if (desktopViewport == null) {
            desktopViewport = new Viewport();
            desktopViewport.add(getDesktopContainer());
        }
        return desktopViewport;
    }

    private WindowHandler getHandler() {
        if (handler == null) {
            handler = new WindowHandler();
        }
        return handler;
    }

    void hideWindow(IPlantWindowInterface window) {
        if (window.isMinimized()) {
            markInactive(window);
            return;
        }
        if (activeWindow == window) {
            activeWindow = null;
        }
        taskBar.removeTaskButton(getWindowManager().getTaskButton(window.getStateId()));
        windowManager.remove(window.getStateId());
        layout(window, RequestType.HIDE);
    }

    private void layout(DesktopLayout desktopLayout, IPlantWindowInterface window,
            RequestType requestType) {
        DEWindowManager winMgr = getWindowManager();
        List<IPlantWindowInterface> windows_list = winMgr.getIplantWindows();
        if (windows_list != null && windows_list.size() > 0) {
            VBoxLayoutContainer layout = getDesktop();
            desktopLayout.layoutDesktop(window, requestType, layout.getElement(),
                    winMgr.getIplantWindows(), layout.getOffsetWidth(), layout.getOffsetHeight());
        }
    }

    private void layout(IPlantWindowInterface window, RequestType requestType) {
        layout(getDesktopLayout(), window, requestType);
    }

    private void markActive(IPlantWindowInterface window) {
        if (activeWindow != null && activeWindow != window) {
            markInactive(activeWindow);
        }
        TaskButton taskButton = getWindowManager().getTaskButton(window.getStateId());
        taskBar.setActiveButton(taskButton);
        activeWindow = window;
        taskButton.setValue(true);
        window.setMinimized(false);
    }

    private void markInactive(IPlantWindowInterface window) {
        if (window == activeWindow) {
            activeWindow = null;
            TaskButton taskButton = getWindowManager().getTaskButton(window.getStateId());
            taskButton.setValue(false);
        }
    }

    private void showWindow(IPlantWindowInterface window) {
        TaskButton taskButton = getWindowManager().getTaskButton(window.getStateId());
        window.setMinimized(false);
        if (taskButton != null && taskBar.getButtons().contains(taskButton)) {
            layout(window, RequestType.SHOW);
            return;
        }
        taskButton = taskBar.addTaskButton(window);
        getWindowManager().setTaskButton(window.getStateId(), taskButton);
        getWindowManager().bringToFront(window.asWidget());
    }

    private class WindowHandler implements ActivateHandler<Window>, DeactivateHandler<Window>,
            MinimizeHandler, HideHandler, ShowHandler {

        @Override
        public void onActivate(ActivateEvent<Window> event) {
            markActive((IPlantWindowInterface)event.getSource());
        }

        @Override
        public void onDeactivate(DeactivateEvent<Window> event) {
            markInactive((IPlantWindowInterface)event.getSource());
        }

        @Override
        public void onHide(HideEvent event) {
            hideWindow((IPlantWindowInterface)event.getSource());
        }

        @Override
        public void onMinimize(MinimizeEvent event) {
            minimizeWindow((IPlantWindowInterface)event.getSource());
        }

        @Override
        public void onShow(ShowEvent event) {
            showWindow((IPlantWindowInterface)event.getSource());
        }

    }

    public List<WindowState> getWindowStates() {
        return getWindowManager().getActiveWindowStates();
    }

    public List<WindowState> getOrderedWindowStates() {
        List<WindowState> windowStates = Lists.newArrayList();
        for (Widget w : getWindowManager().getStack()) {
            windowStates.add(((IPlantWindowInterface)w).getWindowState());
        }
        return windowStates;
    }

    public void restoreWindow(WindowState ws) {
        WindowConfig config = ConfigFactory.getConfig(ws);
        IPlantWindowInterface window = getWindowManager().getWindow(config);
        if (window == null) {
            window = getWindowManager().add(config);
            window.setPixelSize(ws.getWidth(), ws.getHeight());
            window.setPagePosition(ws.getWinLeft(), ws.getWinTop());
        }
        showWindow(config);
    }

}
