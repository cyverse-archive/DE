package org.iplantc.de.desktop.client.views.widgets;


import org.iplantc.de.desktop.client.events.WindowHeadingUpdatedEvent;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.LinkedList;
import java.util.List;

/**
 * Displays a list {@link TaskButton}s for each open iPlant window.
 */
public class TaskBar extends ToolBar {

    private static class HeadingUpdatedEventHandler implements WindowHeadingUpdatedEvent.WindowHeadingUpdatedEventHandler {

        private final TaskButton taskButton;
        private final Window win;

        public HeadingUpdatedEventHandler(TaskButton taskButton, Window win) {
            this.taskButton = taskButton;
            this.win = win;
        }

        @Override
        public void onWindowHeadingUpdated(WindowHeadingUpdatedEvent event) {
            if (Strings.isNullOrEmpty(event.getWindowTitle())) {
                taskButton.setText(win.getHeader().getText());
            } else {
                taskButton.setText(event.getWindowTitle());
            }
        }
    }

    public interface TaskBarAppearance extends ToolBarAppearance {
        BoxLayoutData getButtonLayoutData();

        double getButtonWidth();

        int getHeight();

        double getMinButtonWidth();

        boolean isResizeButtons();
    }

    private final TaskBarAppearance tbAppearance;

    /**
     * Creates a task bar.
     */
    public TaskBar() {
        super(GWT.<TaskBarAppearance>create(TaskBarAppearance.class));
        this.tbAppearance = (TaskBarAppearance) getAppearance();
        setHeight(((TaskBarAppearance) getAppearance()).getHeight());
        setSpacing(-1);
        setPadding(new Padding(0));
    }

    /**
     * Adds a button.
     *
     * @param win the window
     * @return the new task button
     */
    public TaskButton addTaskButton(final Window win) {
        final TaskButton taskButton = new TaskButton(win);
        add(taskButton, tbAppearance.getButtonLayoutData());
        autoSize();
        doLayout();
        win.asWidget().addHandler(new HeadingUpdatedEventHandler(taskButton, win), WindowHeadingUpdatedEvent.TYPE);
        return taskButton;
    }

    /**
     * Returns the bar's buttons.
     *
     * @return the buttons
     */
    public List<TaskButton> getButtons() {
        List<TaskButton> buttons = new LinkedList<>();
        for (Widget widget : getChildren()) {
            if (widget instanceof TaskButton) {
                buttons.add((TaskButton) widget);
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

    private void autoSize() {
        List<TaskButton> buttons = getButtons();
        int count = buttons.size();
        int aw = getOffsetWidth();

        if (!tbAppearance.isResizeButtons() || count < 1) {
            return;
        }

        int each = (int) Math.max(Math.min(Math.floor((aw - 4) / count), tbAppearance.getButtonWidth()), tbAppearance.getMinButtonWidth());

        for (TaskButton taskButton : buttons) {
            taskButton.setWidth(each);
        }
    }

}
