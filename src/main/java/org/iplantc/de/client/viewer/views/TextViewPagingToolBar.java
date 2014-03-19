package org.iplantc.de.client.viewer.views;

import org.iplantc.de.client.viewer.events.SaveFileEvent;
import org.iplantc.de.resources.client.IplantResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;

public class TextViewPagingToolBar extends AbstractPagingToolbar {

    private CheckBox cbxWrap;
    private TextButton saveBtn;
    private boolean editing;
    private Status editStatus;
    private AbstractFileViewer view;

    public TextViewPagingToolBar(AbstractFileViewer view, boolean editing) {
        super(view.getFileSize());
        this.view = view;
        this.editing = editing;
        cbxWrap = new CheckBox();
        cbxWrap.setBoxLabel(org.iplantc.de.resources.client.messages.I18N.DISPLAY.wrap());
        add(cbxWrap);
        saveBtn = new TextButton(org.iplantc.de.resources.client.messages.I18N.DISPLAY.save(), IplantResources.RESOURCES.save());
        add(saveBtn);
        saveBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                TextViewPagingToolBar.this.fireEvent(new SaveFileEvent());

            }
        });
        saveBtn.setEnabled(editing);
        editStatus = new Status(GWT.<StatusAppearance> create(BoxStatusAppearance.class));
        editStatus.setWidth(100);
        setEditingStatus(editing);
        add(editStatus);

    }

    void setEditingStatus(boolean editing) {
        if (editing) {
            editStatus.setText("Editable");
        } else {
            editStatus.setText("Not Editable");
        }
    }

    public void setEditing(boolean editing) {
        saveBtn.setEnabled(editing);
        setEditingStatus(editing);
    }

    public void addWrapCbxChangeHandler(ValueChangeHandler<Boolean> changeHandler) {
        cbxWrap.addValueChangeHandler(changeHandler);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

    public void setWordWrap(boolean wrap) {
        cbxWrap.setValue(wrap);
    }

    @Override
    public void onFirst() {
        view.loadData();

    }

    @Override
    public void onLast() {
        view.loadData();
    }

    @Override
    public void onPrev() {
        view.loadData();

    }

    @Override
    public void onNext() {
        view.loadData();

    }

    @Override
    public void onPageSizeChange() {
        view.loadData();
    }

    @Override
    public void onPageSelect() {
        view.loadData();

    }

    /**
     * @return the editing
     */
    public boolean isEditing() {
        return editing;
    }

}
