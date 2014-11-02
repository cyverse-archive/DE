package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class StructuredTextViewToolBar extends AbstractToolBar {

    public interface StructureTextViewerToolbarAppearance extends AbstractToolBarAppearance {
        ImageResource addRowButtonIcon();
        String addRowButtonText();
        ImageResource deleteRowButtonIcon();
        String deleteRowButtonText();

        String skipRowsLabelText();

        String skipRowsCountWidth();

        String cbxHeaderRowsLabel();
    }


    interface StructuredTextViewTooBarUiBinder extends UiBinder<ToolBar, StructuredTextViewToolBar>{}

    private static final StructuredTextViewTooBarUiBinder BINDER = GWT.create(StructuredTextViewTooBarUiBinder.class);

    private final StructuredTextViewer view;
    @UiField
    TextButton addRowBtn;
    @UiField
    CheckBox cbxHeaderRows;
    @UiField
    TextButton deleteRowBtn;
    @UiField
    IntegerField skipRowsCount;
    @UiField
    LabelToolItem skipRowsLabel;
    @UiField(provided = true)
    StructureTextViewerToolbarAppearance appearance;


    StructuredTextViewToolBar(final StructuredTextViewer view,
                                     final boolean editing,
                                     final StructureTextViewerToolbarAppearance appearance) {
         super(editing, appearance);
        this.appearance = appearance;
        this.view = view;
        initWidget(BINDER.createAndBindUi(this));

    }
    public StructuredTextViewToolBar(final StructuredTextViewer view,
                                     final boolean editing) {
        this(view,
             editing,
             GWT.<StructureTextViewerToolbarAppearance>create(StructureTextViewerToolbarAppearance.class));
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
    public void setEditing(boolean editing) {
        super.setEditing(editing);
        if (editing) {
            enableAdd();
        } else {
            disableAdd();
        }
        setEditingStatus(editing);
    }

    private void setEditingStatus(boolean editing) {
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

}
