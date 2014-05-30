package org.iplantc.de.client.viewer.views;

import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.theme.gray.client.toolbar.GrayPagingToolBarAppearance;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public abstract class AbstractToolBar extends ToolBar {

    GrayPagingToolBarAppearance appearance = new GrayPagingToolBarAppearance();

    protected TextButton saveBtn;
    protected TextButton refreshBtn;
    final Status editStatus;
    protected boolean editing;

    public AbstractToolBar(boolean editing) {
        this.editing = editing;
        saveBtn = new TextButton(I18N.DISPLAY.save(), IplantResources.RESOURCES.save());
        refreshBtn = new TextButton(I18N.DISPLAY.refresh(), IplantResources.RESOURCES.refresh());
        add(saveBtn);
        add(refreshBtn);
        editStatus = new Status(GWT.<StatusAppearance> create(BoxStatusAppearance.class));
        editStatus.setWidth(100);
        addSaveHandler();
        addRefreshHandler();
    }


    public void setEditing(boolean editing) {
        saveBtn.setEnabled(editing);
    }

    private void addSaveHandler() {
        saveBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                save();

            }
        });

    }

    private void addRefreshHandler() {
        refreshBtn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                refresh();
                
            }
        });
    }


    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

    public abstract void save();

    public abstract void refresh();

    public void cleanup() {
        // do nothing intentionally
    }

}
