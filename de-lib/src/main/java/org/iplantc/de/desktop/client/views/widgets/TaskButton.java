/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.desktop.client.views.widgets;

import com.google.common.base.Strings;

import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.event.MaximizeEvent;
import com.sencha.gxt.widget.core.client.event.MinimizeEvent;
import com.sencha.gxt.widget.core.client.event.ShowEvent;

/**
 * Provides a task button that can be added to a task bar and indicates the current state of a desktop
 * application. Clicking on a task button activates the window associated with a desktop application. The
 * active desktop application is indicated by the "pressed" visual state of the task button.
 * 
 * @see TaskBar
 */
public class TaskButton extends ToggleButton implements MinimizeEvent.MinimizeHandler,
                                                        MaximizeEvent.MaximizeHandler,
                                                        ShowEvent.ShowHandler {

    private final Window win;

    /**
     * Creates a task button for the specified window.
     * 
     * @param win a window containing a desktop application
     */
    public TaskButton(Window win) {
        this(win, new TaskButtonCell(win));
    }

    TaskButton(Window win, TaskButtonCell cell){
        super(cell);
        setText(Strings.nullToEmpty(win.getHeader().getText()));
        setIcon(getCell().getAppearance().getIcon());
        setHeight(getCell().getAppearance().getHeight());
        this.win = win;
        setValue(true);
        win.addMinimizeHandler(this);
        win.addMaximizeHandler(this);
        win.addShowHandler(this);
    }


    @Override
    public TaskButtonCell getCell() {
        return (TaskButtonCell) super.getCell();
    }

    @Override
    public void onMaximize(MaximizeEvent event) {
        // Set NOT toggled
        setValue(false, false);
    }

    @Override
    public void onMinimize(MinimizeEvent event) {
        // Set toggled (button depressed)
        setValue(true, false);
    }

    @Override
    public void onShow(ShowEvent event) {
        setValue(false, false);
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (Strings.nullToEmpty(text).length() > getCell().getAppearance().getMaxTextLength()) {
            setToolTip(text);
        }
    }

    public Window getWindow(){
        return win;
    }
}
