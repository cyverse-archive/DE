package org.iplantc.de.client.viewer.views;

import org.iplantc.de.resources.client.messages.I18N;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;

public class StructuredTextViewPagingToolBar extends AbstractPagingToolbar {

    private AbstractTextViewer view;

    private CheckBox cbxHeaderRows;
    private NumberField<Integer> skipRowsCount;

    private LabelToolItem skipRowsLabel;
    private LabelToolItem cbxHeaderLabel;

    public StructuredTextViewPagingToolBar(AbstractTextViewer view) {
        super(view.getFileSize());
        this.view = view;
        addSkipRowsFields();
        addHeaderRowChkBox();
    }

    private void addSkipRowsFields() {
        skipRowsLabel = new LabelToolItem(I18N.DISPLAY.fileViewerSkipLines());
        add(skipRowsLabel);

        skipRowsCount = new NumberField<Integer>(new NumberPropertyEditor.IntegerPropertyEditor());
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

    public int getSkipRowCount() {
        if (skipRowsCount.getValue() == null) {
            return 0;
        } else {
            return skipRowsCount.getValue();
        }
    }

    private void addHeaderRowChkBox() {
        cbxHeaderRows = new CheckBox();

        cbxHeaderLabel = new LabelToolItem(I18N.DISPLAY.fileViewerHeaderRow());
        cbxHeaderRows.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                Boolean hasHeader = event.getValue();
                view.loadDataWithHeader(hasHeader);
                skipRowsCount.setEnabled(!hasHeader);
            }
        });
        add(new FillToolItem());
        add(cbxHeaderLabel);
        add(cbxHeaderRows);
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

}
