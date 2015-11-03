package org.iplantc.de.desktop.client.views.windows;

import org.iplantc.de.client.models.IsMinimizable;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.IsWidget;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.ActivateEvent.HasActivateHandlers;
import com.sencha.gxt.widget.core.client.event.DeactivateEvent.HasDeactivateHandlers;
import com.sencha.gxt.widget.core.client.event.HideEvent.HasHideHandlers;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent.HasMinimizeHandlers;
import com.sencha.gxt.widget.core.client.event.ShowEvent.HasShowHandlers;

/**
 * This interface is intended to be used by the DE Desktop presenter for all primary iPlant windows.
 * 
 * FIXME REFACTOR Rename this file to "IPlantWindow" and rename "IPlantWindow" -> "IPlantWindowImpl"
 * 
 * @author jstroot
 * 
 */
public interface IPlantWindowInterface extends HasActivateHandlers<Window>,
                                               HasDeactivateHandlers<Window>,
                                               HasMinimizeHandlers,
                                               HasHideHandlers,
                                               HasShowHandlers,
                                               IsWidget,
                                               IsMinimizable {

    Window asWindow();

    boolean isVisible();

    void setContainer(Element desktopContainer);

    void setPagePosition(int winLeft, int winTop);

    void setPixelSize(int width, int height);

    <C extends WindowConfig> void show(C windowConfig, String tag, boolean isMaximizable);
    
    <C extends WindowConfig> void update(C config);

    WindowState getWindowState();
    
}
