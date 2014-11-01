package org.iplantc.de.fileViewers.client.views;

import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;

public class TextViewToolBar extends AbstractToolBar {

    private final CheckBox cbxWrap;

    private final AbstractFileViewer view;

    private CheckBox lineNumberCbx;

    // for markdown preview
    private TextButton previewMDBtn;

    public TextViewToolBar(AbstractFileViewer view, boolean editing, boolean preview) {
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
        if (preview) {
            add(new SeparatorToolItem());
            previewMDBtn = new TextButton("Preview Markdown");
            add(previewMDBtn);
        }

        add(new FillToolItem());
        add(editStatus);
        setEditingStatus(editing);
    }

    public void addLineNumberCbxChangeHandler(ValueChangeHandler<Boolean> changeHandler) {
        lineNumberCbx.addValueChangeHandler(changeHandler);
    }

    public void addPreviewHandler(SelectHandler handler) {
        if (previewMDBtn != null) {
            previewMDBtn.addSelectHandler(handler);
        }
    }

    public void addWrapCbxChangeHandler(ValueChangeHandler<Boolean> changeHandler) {
        cbxWrap.addValueChangeHandler(changeHandler);
    }

    public boolean isWrapText() {
        return cbxWrap.getValue();
    }

    @Override
    public void setEditing(boolean editing) {
        super.setEditing(editing);
        setEditingStatus(editing);
    }

    protected void setEditingStatus(boolean editing) {
        if (editing) {
            editStatus.setText("Editable");
        } else {
            editStatus.setText("Not Editable");
        }
    }

    private void buildLineNumberButton() {
        lineNumberCbx = new CheckBox();
        lineNumberCbx.setBoxLabel("Line Numbers");
    }

}
