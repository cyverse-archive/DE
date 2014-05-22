package org.iplantc.de.client.viewer.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.Status.BoxStatusAppearance;
import com.sencha.gxt.widget.core.client.Status.StatusAppearance;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;

public class TextViewPagingToolBar extends AbstractPagingToolbar {

    private final CheckBox cbxWrap;
    final Status editStatus;
    private final AbstractFileViewer view;

    public TextViewPagingToolBar(AbstractFileViewer view, boolean editing) {
        super(view.getFileSize(), editing);
        this.view = view;
        this.editing = editing;
        cbxWrap = new CheckBox();
        cbxWrap.setBoxLabel(org.iplantc.de.resources.client.messages.I18N.DISPLAY.wrap());
        add(cbxWrap);

        saveBtn.setEnabled(editing);
        editStatus = new Status(GWT.<StatusAppearance> create(BoxStatusAppearance.class));
        editStatus.setWidth(100);
        setEditingStatus(editing);
        add(editStatus);
        addSaveHandler();

    }

    private void addSaveHandler() {
        saveBtn.addSelectHandler(new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                view.save();
                
            }
        });
    }

    protected void setEditingStatus(boolean editing) {
        if (editing) {
            editStatus.setText("Editable");
        } else {
            editStatus.setText("Not Editable");
        }
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

    @Override
    public void setEditing(boolean editing) {
        super.setEditing(editing);
        setEditingStatus(editing);
    }

}
