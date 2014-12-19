package org.iplantc.de.theme.base.client.desktop.views.taskbar;

import org.iplantc.de.desktop.client.views.widgets.TaskBar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.theme.base.client.toolbar.ToolBarBaseAppearance;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer;

public class TaskBarBaseAppearance extends ToolBarBaseAppearance implements TaskBar.TaskBarAppearance {

    public interface TaskBarResources extends ClientBundle {
        @Source({"org/iplantc/de/theme/base/client/desktop/views/taskbar/TaskBar.css"})
        TaskBarStyle style();

    }

    public interface TaskBarStyle extends ToolBarBaseStyle, CssResource {

        String moreButton();

        String toolBar();

        @ClassName("x-toolbar-mark")
        String xToolbarMark();
    }

    private final TaskBarStyle style;

    private final TaskBarResources resources;

    public TaskBarBaseAppearance() {
        this.resources = GWT.create(TaskBarResources.class);
        this.style = this.resources.style();

        StyleInjectorHelper.ensureInjected(style, true);
    }

    @Override
    public BoxLayoutContainer.BoxLayoutData getButtonLayoutData() {
        return new BoxLayoutContainer.BoxLayoutData(new Margins(0,3,0,0));
    }

    @Override
    public double getButtonWidth() {
        return 168;
    }

    @Override
    public int getHeight() {
        return 31;
    }

    @Override
    public double getMinButtonWidth() {
        return 118;
    }

    @Override
    public boolean isResizeButtons() {
        return true;
    }

    @Override
    public String toolBarClassName() {
        return style.toolBar();
    }

}
