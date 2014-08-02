package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.Header;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * @author jstroot
 * FIXME REFACTOR Rename to AbstractIplantWindow
 */
public abstract class IplantWindowBase extends Window implements IPlantWindowInterface {
    private class HeaderDoubleClickHandler implements DoubleClickHandler {

        @Override
        public void onDoubleClick(DoubleClickEvent event) {
            if (!isMaximized()) {
                maximize();
            } else {
                restore();
            }
        }
    }

    private class MaximizeRestoreHandler implements MaximizeHandler, RestoreEvent.RestoreHandler {
        @Override
        public void onMaximize(MaximizeEvent event) {
            replaceMaximizeIcon();
        }

        @Override
        public void onRestore(RestoreEvent event) {
            replaceRestoreIcon();
        }
    }

    public interface IplantWindowAppearance {

        IconButton.IconConfig closeBtnConfig();

        String closeBtnToolTip();

        IconButton.IconConfig layoutBtnConfig();

        String layoutBtnToolTip();

        IconButton.IconConfig maximizeBtnConfig();

        String maximizeBtnToolTip();

        IconButton.IconConfig minimizeBtnConfig();

        String minimizeBtnToolTip();

        IconButton.IconConfig restoreBtnConfig();

        String restoreBtnToolTip();

        /**
         * sets header text style and sets header icon
         * @param header
         */
        void setHeaderStyle(Header header);

        String snapLeftMenuItem();

        String snapRightMenuItem();
    }
    interface WindowStateFactory extends AutoBeanFactory {
        AutoBean<WindowState> windowState();
    }

    protected WindowConfig config;

    protected boolean isMaximizable;
    protected boolean maximized;
    protected boolean minimized;
    ToolButton btnRestore;
    private final WindowStateFactory wsf = GWT.create(WindowStateFactory.class);
    private ToolButton btnClose;
    private ToolButton btnLayout;
    private ToolButton btnMaximize;
    private ToolButton btnMinimize;
    private IplantWindowAppearance windowAppearance;

    /**
     * Constructs an instance of the window.
     *
     * @param tag a unique identifier for the window.
     */
    protected IplantWindowBase(String tag) {
        this(tag, false);
    }

    protected IplantWindowBase(final String tag,
                               final WindowConfig config) {
        this(tag, true, config);
    }

    protected IplantWindowBase(String tag,
                               boolean isMaximizable,
                               WindowConfig config) {
        this(tag, isMaximizable);
        this.config = config;
    }

    public IplantWindowBase(String tag,
                            boolean isMaximizable) {
        // Let normal window appearance go through
        this.isMaximizable = isMaximizable;
        windowAppearance = GWT.create(IplantWindowAppearance.class);
        setStateId(tag);
        // Turn off default window buttons.
        setMaximizable(false);
        setMinimizable(false);
        setClosable(false);

        setShadow(false);
        setBodyBorder(false);
        setBorders(false);

        windowAppearance.setHeaderStyle(getHeader());

        // Add Layout, minimize, and close buttons
        btnLayout = createLayoutButton();
        btnMinimize = createMinimizeButton();
        btnClose = createCloseButton();
        getHeader().addTool(btnLayout);
        getHeader().addTool(btnMinimize);

        if (isMaximizable) {
            btnMaximize = createMaximizeButton();
            // SRI: if a window is maximizable, then it is restorable.
            btnRestore = createRestoreButton();
            getHeader().addTool(btnMaximize);

            getHeader().addDomHandler(new HeaderDoubleClickHandler(), DoubleClickEvent.getType());
        }
        getHeader().addTool(btnClose);

        final MaximizeRestoreHandler maximizeRestoreHandler = new MaximizeRestoreHandler();
        addRestoreHandler(maximizeRestoreHandler);
        addMaximizeHandler(maximizeRestoreHandler);
    }

    @Override
    public boolean isMaximized() {
        return maximized;
    }

    void setMaximized(boolean maximize) {
        if (isMaximizable) {
            this.maximized = maximize;

            if (maximize) {
                maximize();
                minimized = false;
            } else {
                restore();
            }
        }
    }

    @Override
    public boolean isMinimized() {
        return minimized;
    }

    @Override
    public void minimize() {
        super.minimize();
        minimized = true;
        hide();
    }

    @Override
    public <C extends WindowConfig> void update(C config) {
    }

    protected <C extends WindowConfig> WindowState createWindowState(C config) {
        WindowState ws = wsf.windowState().as();
        ws.setConfigType(config.getWindowType());
        ws.setMaximized(isMaximized());
        ws.setMinimized(!isVisible());
        ws.setWinLeft(getAbsoluteLeft());
        ws.setWinTop(getAbsoluteTop());
        ws.setWidth(getElement().getWidth(true));
        ws.setHeight(getElement().getHeight(true));
        Splittable configSplittable = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(config));
        ws.setWindowConfig(configSplittable);
        return ws;
    }

    protected void doHide() {
        hide();
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        btnMaximize.ensureDebugId(baseID + DeModule.Ids.WIN_MAX_BTN);
        btnMinimize.ensureDebugId(baseID + DeModule.Ids.WIN_MIN_BTN);
        btnRestore.ensureDebugId(baseID + DeModule.Ids.WIN_RESTORE_BTN);
        btnClose.ensureDebugId(baseID + DeModule.Ids.WIN_CLOSE_BTN);
        btnLayout.ensureDebugId(baseID + DeModule.Ids.WIN_LAYOUT_BTN);
    }

    @Override
    protected void onShow() {
        super.onShow();
        minimized = false;
    }

    private ToolButton createCloseButton() {
        final ToolButton newCloseBtn = new ToolButton(windowAppearance.closeBtnConfig());
        newCloseBtn.setToolTip(windowAppearance.closeBtnToolTip());
        newCloseBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doHide();
            }
        });
        return newCloseBtn;
    }

    private ToolButton createLayoutButton() {
        final ToolButton layoutBtn = new ToolButton(windowAppearance.layoutBtnConfig());
        // Remove tool tip, it gets in the way of the menu.
//        layoutBtn.setToolTip(windowAppearance.layoutBtnToolTip());
        final Menu m = new Menu();
        MenuItem left = new MenuItem(windowAppearance.snapLeftMenuItem());
        MenuItem right = new MenuItem(windowAppearance.snapRightMenuItem());
        left.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                // TODO JDS Implement snap left
            }
        });
        right.addSelectionHandler(new SelectionHandler<Item>() {
            @Override
            public void onSelection(SelectionEvent<Item> event) {
                // TODO JDS Implement snap right
            }
        });

        m.add(left);
        m.add(right);

        layoutBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                m.showAt(layoutBtn.getAbsoluteLeft() + 10, layoutBtn.getAbsoluteTop() + 15);
            }
        });

        return layoutBtn;
    }

    private ToolButton createMaximizeButton() {
        final ToolButton maxBtn = new ToolButton(windowAppearance.maximizeBtnConfig());
        maxBtn.setToolTip(windowAppearance.maximizeBtnToolTip());

        maxBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                setMaximized(true);
            }
        });

        return maxBtn;
    }

    private ToolButton createMinimizeButton() {
        final ToolButton newMinBtn = new ToolButton(windowAppearance.minimizeBtnConfig());
        newMinBtn.setToolTip(windowAppearance.minimizeBtnToolTip());

        newMinBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                minimize();
            }
        });

        return newMinBtn;
    }

    private ToolButton createRestoreButton() {
        final ToolButton btnRestore = new ToolButton(windowAppearance.restoreBtnConfig());
        btnRestore.setToolTip(windowAppearance.restoreBtnToolTip());

        btnRestore.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                setMaximized(false);
            }
        });

        return btnRestore;
    }


    /**
     * Replaces the maximize icon with the restore icon.
     * <p/>
     * The restore icon is only visible to the user when a window is in maximized state.
     */
    private void replaceMaximizeIcon() {
        final int indexOf = getHeader().getTools().indexOf(btnMaximize);
        getHeader().removeTool(btnMaximize);
        getHeader().insertTool(btnRestore, indexOf);
    }

    /**
     * Replaces the restore icon with the maximize icon.
     */
    private void replaceRestoreIcon() {
        final int indexOf = getHeader().getTools().indexOf(btnRestore);
        getHeader().removeTool(btnRestore);
        getHeader().insertTool(btnMaximize, indexOf);
    }

}
