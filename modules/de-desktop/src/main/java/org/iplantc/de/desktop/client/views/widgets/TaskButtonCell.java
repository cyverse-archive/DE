/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.desktop.client.views.widgets;

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.cell.core.client.form.ToggleButtonCell;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.WindowManager;

/**
 * Provides the unique appearance of a desktop task button cell. A cell is a lightweight representation
 * of a renderable object. A task button cell inherits many of the properties of a toggle button cell.
 * <p/>
 * For more information on the use of the appearance pattern, see <a
 * href='http://www.sencha.com/blog/ext-gwt-3-appearance-design'>Sencha GXT 3.0 Appearance Design</a>
 */
public class TaskButtonCell extends ToggleButtonCell {

    /**
     * Defines the appearance interface for a task button cell.
     * <p/>
     * The appearance interface defines the interaction between the widget and an appearance instance.
     * The concrete implementation of the appearance interface typically incorporates the external HTML
     * and CSS source using the {@link XTemplates} and {@link CssResource} interfaces.
     *
     * @param <T> the type that this Cell represents
     */
    public interface TaskButtonCellAppearance<T> extends ButtonCellAppearance<T> {
        int getHeight();

        ImageResource getIcon();

        int getMaxTextLength();
    }

    private final Window window;
    private final WindowManager windowManager;

    /**
     * Constructs a task button cell with the default appearance.
     * <p/>
     * The GWT module file contains a replace-with directive that maps the appearance interface
     * (specified as the argument to the create method) to a concrete implementation class, e.g.
     * {@link TaskButtonCellDefaultAppearance}. See {@code Desktop.gwt.xml} for more information.
     */
    public TaskButtonCell(final Window window) {
        this(GWT.<TaskButtonCellAppearance<Boolean>>create(TaskButtonCellAppearance.class), window, WindowManager.get());
    }

    /**
     * Creates a task button cell with the specified appearance.
     *
     * @param appearance the appearance of the task button cell
     */
    TaskButtonCell(TaskButtonCellAppearance<Boolean> appearance,
                   final Window window,
                   final WindowManager windowManager) {
        super(appearance);
        this.window = window;
        this.windowManager = windowManager;
    }

    @Override
    public TaskButtonCellAppearance<Boolean> getAppearance() {
        return (TaskButtonCellAppearance<Boolean>) super.getAppearance();
    }

    @Override
    public void setText(String text) {
        super.setText(Format.ellipse(text, getAppearance().getMaxTextLength()));
    }

    @Override
    protected void onClick(Context context, XElement p, Boolean value, NativeEvent event,
                           ValueUpdater<Boolean> valueUpdater) {
       boolean valuePassed = value;
        if(!window.isVisible()) {
            window.show();
        } else if (window == windowManager.getActive()){
            window.minimize();
        } else {
            window.toFront();
            // Force button state, to prevent button toggle.
            valuePassed = true;
        }
        callSuperOnClick(context, p, event, valueUpdater, valuePassed);
    }

    void callSuperOnClick(Context context, XElement p, NativeEvent event,
                          ValueUpdater<Boolean> valueUpdater, boolean valuePassed) {
        super.onClick(context, p, valuePassed, event, valueUpdater);
    }

}
