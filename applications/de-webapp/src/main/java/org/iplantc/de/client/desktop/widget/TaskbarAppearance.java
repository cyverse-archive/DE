package org.iplantc.de.client.desktop.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import com.sencha.gxt.core.client.resources.StyleInjectorHelper;
import com.sencha.gxt.theme.base.client.toolbar.ToolBarBaseAppearance;

public class TaskbarAppearance extends ToolBarBaseAppearance {

    public interface TaskBarResources extends ClientBundle {
        @Source({"TaskBar.css"})
        TaskBarStyle style();

    }

    public interface TaskBarStyle extends ToolBarBaseStyle, CssResource {

    }

    private final TaskBarStyle style;

    private final TaskBarResources resources;

    public TaskbarAppearance() {
        this.resources = GWT.<TaskBarResources> create(TaskBarResources.class);
        this.style = this.resources.style();

        StyleInjectorHelper.ensureInjected(style, true);
    }

    @Override
    public String toolBarClassName() {
        return style.toolBar();
    }

}
