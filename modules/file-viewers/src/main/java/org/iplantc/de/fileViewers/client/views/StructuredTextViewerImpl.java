package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.fileViewers.client.events.AddRowSelectedEvent;
import org.iplantc.de.fileViewers.client.events.DeleteRowSelectedEvent;
import org.iplantc.de.fileViewers.client.events.HeaderRowCheckboxChangedEvent;
import org.iplantc.de.fileViewers.client.events.SkipRowsCountValueChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * FIXME JDS Need to extract this into a ui.xml
 *
 * @author sriram, jstroot
 */
public class StructuredTextViewerImpl extends AbstractStructuredTextViewer {

    @UiTemplate("AbstractStructuredTextViewer.ui.xml")
    interface StructuredTextViewerUiBinder extends UiBinder<Widget, StructuredTextViewerImpl> {}

    private static final StructuredTextViewerUiBinder BINDER = GWT.create(StructuredTextViewerUiBinder.class);

    Logger LOG = Logger.getLogger(StructuredTextViewerImpl.class.getName());

    private int columns;
    private boolean dirty;
    private boolean hasHeader;
    private Splittable headerRow;
    private GridInlineEditing<Splittable> rowEditing;
    private List<Splittable> skippedRows;
    private Widget widget;

    @UiField(provided = true)
    StructuredTextViewToolBar toolbar;

    public StructuredTextViewerImpl(final File file,
                                    final String infoType,
                                    final boolean editing,
                                    final Integer columns,
                                    final FileViewer.Presenter presenter) {
        super(file, infoType, editing, presenter);
        toolbar = new StructuredTextViewToolBar(this, editing);
        widget = BINDER.createAndBindUi(this);
        if (columns != null) {
            // FIXME This initialization should be done in the presenter
            LOG.info("Columns: " + columns);
//            initGrid(columns);
            setEditing(true);
//            addRow();
        }
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEvent.FileSavedEventHandler handler) {
        return asWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public Widget asWidget() {
        return widget;
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        asWidget().fireEvent(event);
    }

    @Override
    public String getViewName() {
        if (file != null) {
            return "Tabular View: " + file.getName();
        } else {
            return "Tabular View";
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void mask(String loadingMask) {

    }

    @Override
    public void unmask() {
//        container.unmask();
    }

    @UiHandler("toolbar")
    void onAddRowSelected(AddRowSelectedEvent event) {
        Splittable obj = StringQuoter.createSplittable();
        for (int i = 0; i < columns; i++) {
            StringQuoter.create("col" + i).assign(obj, String.valueOf(i));
        }
        if (rowEditing != null) {
            rowEditing.cancelEditing();
            listStore.add(obj);
            int row = listStore.indexOf(obj);
            setDirty(true);
            rowEditing.startEditing(new GridCell(row, 1));
        }
    }

    @UiHandler("toolbar")
    void onDeleteRowSelected(DeleteRowSelectedEvent event) {
        List<Splittable> selectedRows = grid.getSelectionModel().getSelectedItems();
        if (selectedRows != null && selectedRows.size() > 0) {
            for (Splittable obj : selectedRows) {
                listStore.remove(obj);
            }
            setDirty(true);
        }
    }

    @UiHandler("toolbar")
    void onHeaderRowCheckboxValueChange(HeaderRowCheckboxChangedEvent event) {
        hasHeader = event.getValue();
        defineColumnHeader();
    }

    @UiHandler("toolbar")
    void onSkipRowsCountValueChanged(SkipRowsCountValueChangeEvent event) {
        int val = event.getValue();
        if (val > 0 && val < listStore.size() + ((skippedRows != null) ? skippedRows.size() : 0)) {
            if (skippedRows == null) {
                skippedRows = new ArrayList<>();
                // increment
                skippedRows.addAll(listStore.subList(0, val));
            } else if (skippedRows.size() > val) {
                // reduction
                for (int j = skippedRows.size() - 1; j >= val; j--) {
                    listStore.add(0, skippedRows.remove(j));
                }
            } else if (skippedRows.size() < val) {
                // increment
                skippedRows.addAll(listStore.subList(0, val - skippedRows.size()));
            } else {
                // same size
                skippedRows.clear();
                skippedRows.addAll(listStore.subList(0, val));
            }
            for (Splittable obj : skippedRows) {
                listStore.remove(obj);
            }

        } else {
            if (val == 0 && skippedRows != null && skippedRows.size() > 0) {
                // add back the skipped rows.
                for (int j = 0; j < skippedRows.size(); j++) {
                    listStore.add(j, skippedRows.get(j));
                }
            }
            skippedRows = null;
        }

        grid.getView().refresh(true);

    }

    void setDirty(Boolean dirty) {
        this.dirty = dirty;
        if (presenter.isDirty() == dirty) {
            return;
        }
        presenter.setViewDirtyState(dirty, this);
    }

    private void defineColumnHeader() {
        ColumnModel<Splittable> cm = grid.getColumnModel();
        List<ColumnConfig<Splittable, ?>> configs = cm.getColumns();
        Splittable firstRow = listStore.get(0);
        int index = 0;
        for (ColumnConfig<Splittable, ?> conf : configs) {
            if (cm.indexOf(conf) != 0) { // col 0 is numberer
                Splittable splittable = firstRow.get(String.valueOf(index));
                Splittable string = (splittable != null) ? splittable : null;
                if (hasHeader) {
                    conf.setHeader((string != null) ? string.asString() : index + "");
                } else {
                    conf.setHeader(index + "");
                }
                index++;
            }

        }

        // converted first row to header. so remove first row
        if (hasHeader) {
            listStore.remove(firstRow);
            headerRow = firstRow;
        } else {
            if (headerRow != null) {
                // if it was removed prev, add the back row at 1st position
                listStore.add(0, headerRow);
                headerRow = null;
            }
        }

        grid.reconfigure(listStore, cm);
        grid.getView().refresh(true);
    }


    @SuppressWarnings("unchecked")
    private void setEditing(boolean editing) {
        if (grid == null) {
            return;
        }

        toolbar.setEditing(editing);

        if (editing) {
            if (rowEditing == null) {
                // Initialize row editing
                grid.setToolTip("Double click to edit...");
                rowEditing = new GridInlineEditing<>(grid);
                rowEditing.setClicksToEdit(ClicksToEdit.TWO);
                rowEditing.addCompleteEditHandler(new CompleteEditHandler<Splittable>() {

                    @Override
                    public void onCompleteEdit(CompleteEditEvent<Splittable> event) {
                        dirty = true;
                        listStore.commitChanges();
                    }
                });

                List<ColumnConfig<Splittable, ?>> cols = grid.getColumnModel().getColumns();
                for (ColumnConfig<Splittable, ?> cc : cols) {
                    TextField field = new TextField();
                    field.setClearValueOnParseError(false);
                    rowEditing.addEditor((ColumnConfig<Splittable, String>) cc, field);
                }
            }
        } else {
            rowEditing = null;
            grid.removeToolTip();
        }
    }
}
