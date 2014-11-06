package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;

public class StructuredTextViewToolBar extends AbstractToolBar {

    private final StructuredTextViewer view;
    private TextButton addRowBtn;
    private CheckBox cbxHeaderRows;
    private TextButton deleteRowBtn;
    private CheckBox lineNumberCbx;
    private NumberField<Integer> skipRowsCount;
    private LabelToolItem skipRowsLabel;

    public StructuredTextViewToolBar(StructuredTextViewer view, boolean editing) {
        super(editing);
        this.view = view;
        this.editing = editing;
        add(new SeparatorToolItem());
        addAddRowBtn();
        addDeleteRowBtn();
        add(new SeparatorToolItem());
        buildLineNumberButton();
        add(lineNumberCbx);
        addSkipRowsFields();
        addHeaderRowChkBox();
        add(new FillToolItem());
        add(editStatus);
        setEditingStatus(editing);
    }

    public void addLineNumberCbxChangeHandler(ValueChangeHandler<Boolean> valueChangeHandler) {
        lineNumberCbx.addValueChangeHandler(valueChangeHandler);
    }

    public void disableAdd() {
        addRowBtn.disable();
    }

    public void enableAdd() {
        addRowBtn.enable();
    }

    public int getSkipRowCount() {
        if (skipRowsCount.getValue() == null) {
            return 0;
        } else {
            return skipRowsCount.getValue();
        }
    }

    @Override
    public void refresh() {
        view.refresh();
    }

    @Override
    public void save() {
        view.save();
    }

    @Override
    public void setEditing(boolean editing) {
        super.setEditing(editing);
        if (editing) {
            enableAdd();
        } else {
            disableAdd();
        }
        setEditingStatus(editing);
    }

    protected void setEditingStatus(boolean editing) {
        if (editing) {
            editStatus.setText("Editable");
        } else {
            editStatus.setText("Not Editable");
        }
    }

    private void addAddRowBtn() {
        addRowBtn = new TextButton("", IplantResources.RESOURCES.add());
        addRowBtn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                view.addRow();
            }
        });
        addRowBtn.setToolTip("Add Row");
        add(addRowBtn);
    }

    private void addDeleteRowBtn() {
        deleteRowBtn = new TextButton("", IplantResources.RESOURCES.delete());
        deleteRowBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                view.deleteRow();
            }
        });
        deleteRowBtn.setToolTip("Delete Row");
        add(deleteRowBtn);
    }

    private void addHeaderRowChkBox() {
        cbxHeaderRows = new CheckBox();
        cbxHeaderRows.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Boolean hasHeader = event.getValue();
                view.loadDataWithHeader(hasHeader);
                skipRowsCount.setEnabled(!hasHeader);
            }
        });
        cbxHeaderRows.setBoxLabel(I18N.DISPLAY.fileViewerHeaderRow());
        add(cbxHeaderRows);
    }

    private void addSkipRowsFields() {
        skipRowsLabel = new LabelToolItem(I18N.DISPLAY.fileViewerSkipLines());
        add(skipRowsLabel);

        skipRowsCount = new NumberField<>(new NumberPropertyEditor.IntegerPropertyEditor());
        skipRowsCount.setWidth(30);
        skipRowsCount.setValue(0);
        skipRowsCount.setAllowNegative(false);
        skipRowsCount.setAllowDecimals(false);
        skipRowsCount.addValueChangeHandler(new ValueChangeHandler<Integer>() {

            @Override
            public void onValueChange(ValueChangeEvent<Integer> event) {
                view.skipRows(getSkipRowCount());
                skipRowsCount.setValue(getSkipRowCount());
            }
        });
        add(skipRowsCount);
    }

    private void buildLineNumberButton() {
        lineNumberCbx = new CheckBox();
        lineNumberCbx.setBoxLabel("Line Numbers");
    }

}
