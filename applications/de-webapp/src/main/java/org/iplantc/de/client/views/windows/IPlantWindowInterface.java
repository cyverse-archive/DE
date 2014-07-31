package org.iplantc.de.client.views.windows;

import org.iplantc.de.client.models.IsMinimizable;
import org.iplantc.de.client.models.WindowState;
import org.iplantc.de.commons.client.views.window.configs.WindowConfig;

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

    void show();
    
    <C extends WindowConfig> void update(C config);

    WindowState getWindowState();
    
}
