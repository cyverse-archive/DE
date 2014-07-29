/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.newDesktop.views.widgets;

import org.iplantc.de.client.views.windows.IPlantWindowInterface;

import com.google.common.base.Strings;

import com.sencha.gxt.widget.core.client.button.ToggleButton;

/**
 * Provides a task button that can be added to a task bar and indicates the current state of a desktop
 * application. Clicking on a task button activates the window associated with a desktop application. The
 * active desktop application is indicated by the "pressed" visual state of the task button.
 * 
 * @see TaskBar
 */
public class TaskButton extends ToggleButton {

    private final IPlantWindowInterface win;

    /**
     * Creates a task button for the specified window.
     * 
     * @param win a window containing a desktop application
     */
    public TaskButton(IPlantWindowInterface win) {
        super(new TaskButtonCell(win));
        setText(win.getTitle());
        setIcon(getCell().getAppearance().getIcon());
        setHeight(getCell().getAppearance().getHeight());
        this.win = win;
    }

    @Override
    public TaskButtonCell getCell() {
        return (TaskButtonCell) super.getCell();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (Strings.nullToEmpty(text).length() > getCell().getAppearance().getMaxTextLength()) {
            setToolTip(text);
        }
    }

    public IPlantWindowInterface getWindow(){
        return win;
    }
}
