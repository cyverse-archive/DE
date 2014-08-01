package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.DeResources;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.theme.window.IPlantWindowAppearance;
import org.iplantc.de.shared.DeModule;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent.MaximizeHandler;
import com.sencha.gxt.widget.core.client.event.RestoreEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * @author jstroot
 */
public abstract class IplantWindowBase extends Window implements IPlantWindowInterface {
    interface WindowStateFactory extends AutoBeanFactory {
        AutoBean<WindowState> windowState();
    }

    protected WindowConfig config;

    protected boolean isMaximizable;
    protected boolean maximized;
    protected boolean minimized;
    ToolButton btnRestore;
    private final DeResources res = GWT.create(DeResources.class);
    private final WindowStateFactory wsf = GWT.create(WindowStateFactory.class);
    private ToolButton btnClose;
    private ToolButton btnLayout;
    private ToolButton btnMaximize;
    private ToolButton btnMinimize;

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
        super(GWT.<IPlantWindowAppearance>create(IPlantWindowAppearance.class));
        res.css().ensureInjected();
        setStateId(tag);
        this.isMaximizable = isMaximizable;

        // Add Layout button
        btnLayout = createLayoutButton();
        getHeader().addTool(btnLayout);

        // Add minimizable button
        btnMinimize = createMinimizeButton();
        getHeader().addTool(btnMinimize);

        if (isMaximizable) {
            btnMaximize = createMaximizeButton();
            // SRI: if a window is maximizable, then it is restorable.
            btnRestore = createRestoreButton();
            getHeader().addTool(btnMaximize);
            getHeader().sinkEvents(Event.ONDBLCLICK);
            getHeader().addHandler(createHeaderDblClickHandler(), DoubleClickEvent.getType());
        }
        // Add close button
        btnClose = createCloseButton();
        getHeader().addTool(btnClose);

        // Turn off default window buttons.
        setMaximizable(false);
        setMinimizable(false);
        setClosable(false);

        getHeader().setIcon(IplantResources.RESOURCES.iplantTiny());

        setShadow(false);
        setBodyBorder(false);
        setBorders(false);

        addHandler(new RestoreEvent.RestoreHandler() {
            @Override
            public void onRestore(RestoreEvent event) {
                replaceRestoreIcon();
            }
        }, RestoreEvent.getType());

        addMaximizeHandler(new MaximizeHandler() {
            @Override
            public void onMaximize(MaximizeEvent event) {
                replaceMaximizeIcon();
            }
        });
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
                btnRestore.removeStyleName(res.css().xToolRestorewindowHover());
            } else {
                restore();
                btnMaximize.removeStyleName(res.css().xToolMaximizewindowHover());
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
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
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
        final ToolButton newCloseBtn = new ToolButton(res.css().xToolClosewindow());
        newCloseBtn.sinkEvents(Event.ONMOUSEOUT);
        newCloseBtn.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.close());

        newCloseBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doHide();
            }
        });

        newCloseBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                newCloseBtn.removeStyleName(res.css().xToolClosewindowHover());
            }
        }, MouseOutEvent.getType());

        newCloseBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                newCloseBtn.addStyleName(res.css().xToolClosewindowHover());
            }
        }, MouseOverEvent.getType());

        return newCloseBtn;
    }

    private DoubleClickHandler createHeaderDblClickHandler() {
        DoubleClickHandler handler = new DoubleClickHandler() {

            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if (!isMaximized()) {
                    maximize();
                } else {
                    restore();
                }
            }
        };

        return handler;
    }

    private ToolButton createLayoutButton() {
        final ToolButton layoutBtn = new ToolButton(res.css().xToolLayoutwindow());
        layoutBtn.sinkEvents(Event.ONMOUSEOUT);
        layoutBtn.setToolTip("Layout");
        final Menu m = new Menu();
        // FIXME JDS Reimplement layout button which has position left/right.
//        m.add(buildCascadeLayoutMenuItem());
//        m.add(buildTileLayoutMenuItem());
        MenuItem left = new MenuItem("Left");
        MenuItem right = new MenuItem("Right");

        m.add(left);
        m.add(right);

        layoutBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                m.showAt(layoutBtn.getAbsoluteLeft() + 10, layoutBtn.getAbsoluteTop() + 15);
            }
        });

        layoutBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                layoutBtn.addStyleName(res.css().xToolLayoutwindowHover());
            }
        }, MouseOverEvent.getType());

        layoutBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                layoutBtn.removeStyleName(res.css().xToolLayoutwindowHover());
            }
        }, MouseOutEvent.getType());

        return layoutBtn;
    }

    private ToolButton createMaximizeButton() {
        final ToolButton maxBtn = new ToolButton(res.css().xToolMaximizewindow());
        maxBtn.sinkEvents(Event.ONMOUSEOUT);
        maxBtn.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.maximize());

        maxBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                setMaximized(true);
            }
        });

        maxBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                maxBtn.addStyleName(res.css().xToolMaximizewindowHover());
            }
        }, MouseOverEvent.getType());

        maxBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                maxBtn.removeStyleName(res.css().xToolMaximizewindowHover());
            }
        }, MouseOutEvent.getType());

        return maxBtn;
    }

    private ToolButton createMinimizeButton() {
        final ToolButton newMinBtn = new ToolButton(res.css().xToolMinimizewindow());
        newMinBtn.sinkEvents(Event.ONMOUSEOUT);
        newMinBtn.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.minimize());

        newMinBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                minimized = true;
                minimize();
                newMinBtn.removeStyleName(res.css().xToolMinimizewindowHover());
            }
        });

        newMinBtn.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                newMinBtn.addStyleName(res.css().xToolMinimizewindowHover());
            }
        }, MouseOverEvent.getType());

        newMinBtn.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                newMinBtn.removeStyleName(res.css().xToolMinimizewindowHover());
            }
        }, MouseOutEvent.getType());

        return newMinBtn;
    }

    private ToolButton createRestoreButton() {
        final ToolButton btnRestore = new ToolButton(res.css().xToolRestorewindow());
        btnRestore.sinkEvents(Event.ONMOUSEOUT);
        btnRestore.setToolTip(org.iplantc.de.resources.client.messages.I18N.DISPLAY.restore());

        btnRestore.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                setMaximized(false);
            }
        });

        btnRestore.addHandler(new MouseOverHandler() {
            @Override
            public void onMouseOver(MouseOverEvent event) {
                btnRestore.addStyleName(res.css().xToolRestorewindowHover());
            }
        }, MouseOverEvent.getType());

        btnRestore.addHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                btnRestore.removeStyleName(res.css().xToolRestorewindowHover());
            }
        }, MouseOutEvent.getType());

        return btnRestore;
    }

    private int findToolButtonIndex(String btnToolName) {
        int toolCount = getHeader().getToolCount();
        int index = -1;

        for (int i = 0; i < toolCount; i++) {
            Widget tool = getHeader().getTool(i);
            String fullStyle = tool.getStyleName();

            if (fullStyle.contains(btnToolName)) {
                index = i;
                break;
            }
        }

        return index;
    }

    /**
     * Replaces the maximize icon with the restore icon.
     * <p/>
     * The restore icon is only visible to the user when a window is in maximized state.
     */
    private void replaceMaximizeIcon() {
        int index = findToolButtonIndex(res.css().xToolMaximizewindow());
        if (index > -1) {
            getHeader().removeTool(btnMaximize);
            btnMaximize.removeFromParent();
            // re-insert restore button at same index of maximize button
            getHeader().insertTool(btnRestore, index);
        }
    }

    /**
     * Replaces the restore icon with the maximize icon.
     */
    private void replaceRestoreIcon() {
        int index = findToolButtonIndex(res.css().xToolRestorewindow());
        if (index > -1) {
            getHeader().removeTool(btnRestore);
            // re-insert maximize button at same index as restore button
            getHeader().insertTool(btnMaximize, index);
        }
    }

}
