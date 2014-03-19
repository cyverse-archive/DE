/**
 * Sencha GXT 3.0.1 - Sencha for GWT Copyright(c) 2007-2012, Sencha, Inc. licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
package org.iplantc.de.client.desktop.theme.taskbutton;

import org.iplantc.de.client.desktop.widget.TaskButtonCell.TaskButtonCellAppearance;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

import com.sencha.gxt.theme.base.client.button.ButtonCellDefaultAppearance;
import com.sencha.gxt.theme.base.client.frame.TableFrame;
import com.sencha.gxt.theme.base.client.frame.TableFrame.TableFrameResources;

public class TaskButtonCellDefaultAppearance<C> extends ButtonCellDefaultAppearance<C> implements
        TaskButtonCellAppearance<C> {

    public interface TaskButtonCellResources extends ButtonCellResources, ClientBundle {

        @Source({"com/sencha/gxt/theme/base/client/button/ButtonCell.css", "TaskButtonCell.css"})
        @Override
        TaskButtonCellStyle style();
    }

    public interface TaskButtonCellStyle extends ButtonCellStyle {
    }

    public TaskButtonCellDefaultAppearance() {
        super(GWT.<ButtonCellResources> create(TaskButtonCellResources.class), GWT
                .<ButtonCellTemplates> create(ButtonCellTemplates.class), new TableFrame(
                GWT.<TableFrameResources> create(TaskButtonTableFrameResources.class)));
    }

}
