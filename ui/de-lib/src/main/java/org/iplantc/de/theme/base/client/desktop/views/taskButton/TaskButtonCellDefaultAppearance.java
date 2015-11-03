/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.theme.base.client.desktop.views.taskButton;

import org.iplantc.de.desktop.client.views.widgets.TaskButtonCell.TaskButtonCellAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance;
import com.sencha.gxt.theme.base.client.frame.TableFrame;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameResources;

public class TaskButtonCellDefaultAppearance<C> extends ButtonCellDefaultAppearance<C> implements
                                                                                       TaskButtonCellAppearance<C> {

    public interface TaskButtonCellResources extends ButtonCellResources, ClientBundle {
        @Source({"com/sencha/gxt/theme/base/client/button/ButtonCell.css", "org/iplantc/de/theme/base/client/desktop/views/taskButton/TaskButtonCell.css"})
        @Override
        TaskButtonCellStyle style();

        @ClientBundle.Source("org/iplantc/de/theme/base/client/desktop/views/taskButton/whitelogo_sm.png")
        ImageResource whiteLogo();
    }

    public interface TaskButtonCellStyle extends ButtonCellStyle {
    }

    public TaskButtonCellDefaultAppearance() {
        super(GWT.<ButtonCellResources>create(TaskButtonCellResources.class),
              GWT.<ButtonCellTemplates>create(ButtonCellTemplates.class),
              new TableFrame(GWT.<TableFrameResources>create(TaskButtonTableFrameResources.class)));
    }

    @Override
    public int getHeight() {
        return 28;
    }

    @Override
    public ImageResource getIcon() {
        return ((TaskButtonCellResources) resources).whiteLogo();
    }

    @Override
    public int getMaxTextLength() {
        return 26;
    }
}
