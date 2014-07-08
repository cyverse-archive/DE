package org.iplantc.de.client.viewer.views;

import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;

public class TextViewToolBar extends AbstractToolBar {

    private final CheckBox cbxWrap;

    private final AbstractFileViewer view;

    private CheckBox lineNumberCbx;

    public TextViewToolBar(AbstractFileViewer view, boolean editing) {
        super(editing);
        this.view = view;
        this.editing = editing;
        add(new SeparatorToolItem());
        cbxWrap = new CheckBox();
        cbxWrap.setBoxLabel(org.iplantc.de.resources.client.messages.I18N.DISPLAY.wrap());
        add(cbxWrap);
        saveBtn.setEnabled(editing);
        setEditingStatus(editing);
        buildLineNumberButton();
        add(lineNumberCbx);
        add(new FillToolItem());
        add(editStatus);
        setEditingStatus(editing);

    }

    private void buildLineNumberButton() {
        lineNumberCbx = new CheckBox();
        lineNumberCbx.setBoxLabel("Line Numbers");
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

    public void addLineNumberCbxChangeHandleer(ValueChangeHandler<Boolean> changeHandler) {
        lineNumberCbx.addValueChangeHandler(changeHandler);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

    public void setWordWrap(boolean wrap) {
        cbxWrap.setValue(wrap);
    }

    @Override
    public void setEditing(boolean editing) {
        super.setEditing(editing);
        setEditingStatus(editing);
    }

    @Override
    public void save() {
        view.save();

    }

    @Override
    public void refresh() {
        view.refresh();

    }

}
