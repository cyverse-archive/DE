/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.widget;

import org.iplantc.de.client.Constants;
import org.iplantc.de.client.events.WindowHeadingUpdatedEvent;
import org.iplantc.de.client.events.WindowHeadingUpdatedEvent.WindowHeadingUpdatedEventHandler;
import org.iplantc.de.client.views.windows.IPlantWindowInterface;

import com.google.common.base.Strings;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.LinkedList;
import java.util.List;

/**
 * Displays the start menu button followed by a list of open windows.
 */
public class TaskBar extends ToolBar {

    private final int buttonWidth = 168;
    private final int minButtonWidth = 118;
    private final boolean resizeButtons = true;

    /**
     * Creates a task bar.
     */
    public TaskBar() {
        super(new TaskbarAppearance());
        setHeight(Constants.CLIENT.deTaskBarHeight());
        setSpacing(-1);
        setPadding(new Padding(0));

    }

    /**
     * Adds a button.
     * 
     * @param win the window
     * @return the new task button
     */
    public TaskButton addTaskButton(final IPlantWindowInterface win) {
        final TaskButton taskButton = new TaskButton(win);
        add(taskButton, new BoxLayoutData(new Margins(0, 3, 0, 0)));
        autoSize();
        doLayout();
        setActiveButton(taskButton);
        win.asWidget().addHandler(new WindowHeadingUpdatedEventHandler() {

            @Override
            public void onWindowHeadingUpdated(WindowHeadingUpdatedEvent event) {
                if (Strings.isNullOrEmpty(event.getWindowTitle())) {
                    taskButton.setText(win.getTitle());
                } else {
                    taskButton.setText(event.getWindowTitle());
                }
            }
        }, WindowHeadingUpdatedEvent.TYPE);
        return taskButton;
    }

    /**
     * Returns the bar's buttons.
     * 
     * @return the buttons
     */
    public List<TaskButton> getButtons() {
        List<TaskButton> buttons = new LinkedList<TaskButton>();
        for (Widget widget : getChildren()) {
            if (widget instanceof TaskButton) {
                buttons.add((TaskButton)widget);
            }
        }
        return buttons;
    }

    /**
     * Removes a button.
     * 
     * @param btn the button to remove
     */
    public void removeTaskButton(TaskButton btn) {
        remove(btn);
        autoSize();
        doLayout();
    }

    /**
     * Sets the active button.
     * 
     * @param btn the button
     */
    public void setActiveButton(TaskButton btn) {
        // TODO: Provide implementation, v2 did not provide full support
    }

    private void autoSize() {
        List<TaskButton> buttons = getButtons();
        int count = buttons.size();
        int aw = getOffsetWidth();

        if (!resizeButtons || count < 1) {
            return;
        }

        int each = (int)Math.max(Math.min(Math.floor((aw - 4) / count), buttonWidth), minButtonWidth);

        for (TaskButton taskButton : buttons) {
            taskButton.setWidth(each);
        }
    }

}
