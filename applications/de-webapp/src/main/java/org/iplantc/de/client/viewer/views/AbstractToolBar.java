package org.iplantc.de.client.viewer.views;

import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.gray.client.toolbar.GrayPagingToolBarAppearance;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public abstract class AbstractToolBar extends ToolBar {

    GrayPagingToolBarAppearance appearance = new GrayPagingToolBarAppearance();

    protected TextButton saveBtn;
    final Status editStatus;
    protected boolean editing;

    public AbstractToolBar(boolean editing) {
        this.editing = editing;
        saveBtn = new TextButton(I18N.DISPLAY.save(), IplantResources.RESOURCES.save());
        add(saveBtn);
        editStatus = new Status(GWT.<StatusAppearance> create(BoxStatusAppearance.class));
        editStatus.setWidth(100);
    }


    public void setEditing(boolean editing) {
        saveBtn.setEnabled(editing);
    }

    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

}
