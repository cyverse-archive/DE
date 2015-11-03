package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.fileViewers.client.events.AddRowSelectedEvent;
import org.iplantc.de.fileViewers.client.events.DeleteRowSelectedEvent;
import org.iplantc.de.fileViewers.client.events.HeaderRowCheckboxChangedEvent;
import org.iplantc.de.fileViewers.client.events.SkipRowsCountValueChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;

import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.IntegerField;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * @author jstroot
 */
public class StructuredTextViewToolBar extends AbstractToolBar {

    public interface StructureTextViewerToolbarAppearance extends AbstractToolBarAppearance {
        ImageResource addRowButtonIcon();

        String addRowButtonTooltip();

        String cbxHeaderRowsLabel();

        ImageResource deleteRowButtonIcon();

        String deleteRowButtonTooltip();

        String skipRowsCountWidth();

        String skipRowsLabelText();
    }

    interface StructuredTextViewTooBarUiBinder extends UiBinder<ToolBar, StructuredTextViewToolBar> { }

    @UiField TextButton addRowBtn;
    @UiField(provided = true) StructureTextViewerToolbarAppearance appearance;
    @UiField CheckBox cbxHeaderRows;
    @UiField TextButton deleteRowBtn;
    @UiField IntegerField skipRowsCount;
    @UiField LabelToolItem skipRowsLabel;
    private static final StructuredTextViewTooBarUiBinder BINDER = GWT.create(StructuredTextViewTooBarUiBinder.class);

    StructuredTextViewToolBar(final boolean editing,
                              final StructureTextViewerToolbarAppearance appearance) {
        super(editing, appearance);
        this.appearance = appearance;
        initWidget(BINDER.createAndBindUi(this));
        skipRowsCount.setValue(0);
        setEditing(editing);
    }

    public StructuredTextViewToolBar(final boolean editing) {
        this(editing,
             GWT.<StructureTextViewerToolbarAppearance>create(StructureTextViewerToolbarAppearance.class));
    }

    public HandlerRegistration addAddRowSelectedEventHandler(AddRowSelectedEvent.AddRowSelectedEventHandler handler) {
        return addHandler(handler, AddRowSelectedEvent.TYPE);
    }

    public HandlerRegistration addDeleteRowSelectedEventHandler(DeleteRowSelectedEvent.DeleteRowSelectedEventHandler handler) {
        return addHandler(handler, DeleteRowSelectedEvent.TYPE);
    }

    public HandlerRegistration addHeaderRowCheckbowChangeEventHandler(HeaderRowCheckboxChangedEvent.HeaderRowCheckboxChangedEventHandler handler){
        return addHandler(handler, HeaderRowCheckboxChangedEvent.TYPE);
    }

    public HandlerRegistration addSkipRowsCountValueChangeEventHandler(SkipRowsCountValueChangeEvent.SkipRowsCountValueChangeEventHandler handler){
        return addHandler(handler, SkipRowsCountValueChangeEvent.TYPE);
    }

    @Override
    public void setEditing(boolean editing) {
        super.setEditing(editing);
        if (editing) {
            enableAdd();
        } else {
            disableAdd();
        }
    }

    void disableAdd() {
        addRowBtn.disable();
    }

    void enableAdd() {
        addRowBtn.enable();
    }

    int getSkipRowCount() {
        if (skipRowsCount.getValue() == null) {
            return 0;
        } else {
            return skipRowsCount.getValue();
        }
    }

    @UiHandler("addRowBtn")
    void onAddRowBtnSelected(SelectEvent event) {
        fireEvent(new AddRowSelectedEvent());
    }

    @UiHandler("deleteRowBtn")
    void onDeleteRowBtnSelected(SelectEvent event) {
        fireEvent(new DeleteRowSelectedEvent());
    }

    @UiHandler("cbxHeaderRows")
    void onHeaderRowCheckboxChanged(ValueChangeEvent<Boolean> event) {
        Boolean hasHeader = event.getValue();
        skipRowsCount.setEnabled(!hasHeader);
        fireEvent(new HeaderRowCheckboxChangedEvent(event.getValue()));
    }

    @UiHandler("skipRowsCount")
    void onSkipRowsCountValueChange(ValueChangeEvent<Integer> event) {
        skipRowsCount.setValue(getSkipRowCount());
        fireEvent(new SkipRowsCountValueChangeEvent(getSkipRowCount()));
    }

}
