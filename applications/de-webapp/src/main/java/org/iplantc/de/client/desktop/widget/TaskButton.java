/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.views.windows.IPlantWindowInterface;
import org.iplantc.de.resources.client.IplantResources;

import com.google.common.base.Strings;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.WindowManager;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Provides a task button that can be added to a task bar and indicates the current state of a desktop
 * application. Clicking on a task button activates the window associated with a desktop application. The
 * active desktop application is indicated by the "pressed" visual state of the task button.
 * 
 * @see TaskBar
 */
public class TaskButton extends ToggleButton {

    private static final int MAX_TEXT_LENGTH = 26;
    private final IPlantWindowInterface win;

    /**
     * Creates a task button for the specified window.
     * 
     * @param win a window containing a desktop application
     */
    public TaskButton(IPlantWindowInterface win) {
        super(new TaskButtonCell());
        ImageResource icon = IplantResources.RESOURCES.whitelogoSmall();
        String text = win.getTitle();
        if (text != null) {
            setText(Format.ellipse(text, MAX_TEXT_LENGTH));
        }
        setIcon(icon);
        setHeight(28);
        this.win = win;
        addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSelect(event);
            }
        });
    }

    @Override
    public void setText(String text) {
        if (Strings.nullToEmpty(text).length() > MAX_TEXT_LENGTH) {
            setToolTip(text);
        }
        super.setText(Format.ellipse(text, MAX_TEXT_LENGTH));
    }

    protected void doSelect(SelectEvent event) {
        if (!win.isVisible()) {
            win.show();
        } else if (win == WindowManager.get().getActive()) {
            win.minimize();
        } else {
            win.toFront();
        }
    }
}
