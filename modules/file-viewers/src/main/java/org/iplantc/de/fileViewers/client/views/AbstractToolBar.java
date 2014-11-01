package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 *
 */
public abstract class AbstractToolBar extends ToolBar {

    protected boolean editing;
    protected TextButton refreshBtn;
    protected TextButton saveBtn;
    final Status editStatus;

    public AbstractToolBar(boolean editing) {
        this.editing = editing;
        saveBtn = new TextButton(I18N.DISPLAY.save(), IplantResources.RESOURCES.save());
        refreshBtn = new TextButton(I18N.DISPLAY.refresh(), IplantResources.RESOURCES.refresh());
        add(saveBtn);
        add(refreshBtn);
        editStatus = new Status(GWT.<StatusAppearance>create(BoxStatusAppearance.class));
        editStatus.setWidth(100);
    }

    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

    public void setEditing(boolean editing) {
        saveBtn.setEnabled(editing);
    }

    public void addRefreshHandler(final SelectHandler handler) {
        refreshBtn.addSelectHandler(handler);
    }

    public void addSaveHandler(final SelectHandler handler) {
        saveBtn.addSelectHandler(handler);
    }

}
