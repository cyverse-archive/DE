package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.CommonModelAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.events.AddRowSelectedEvent;
import org.iplantc.de.fileViewers.client.events.DeleteRowSelectedEvent;
import org.iplantc.de.fileViewers.client.events.HeaderRowCheckboxChangedEvent;
import org.iplantc.de.fileViewers.client.events.SkipRowsCountValueChangeEvent;

import com.google.common.base.Preconditions;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
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
 * @author sriram, jstroot
 */
public class StructuredTextViewer extends AbstractStructuredTextViewer {

    public interface StructuredTextViewerAppearance extends AbstractStructuredTextViewerAppearance {

        String createNewDefaultColumnValue(int column);

        String defaultViewName();

        String gridToolTip();

        String viewName(String fileName);
    }

    @UiTemplate("StructuredTextViewer.ui.xml")
    interface StructuredTextViewerUiBinder extends UiBinder<Widget, StructuredTextViewer> { }

    final StructuredTextViewerAppearance appearance = GWT.create(StructuredTextViewerAppearance.class);
    Logger LOG = Logger.getLogger(StructuredTextViewer.class.getName());

    @UiField(provided = true) StructuredTextViewToolBar toolbar;

    private static final StructuredTextViewerUiBinder BINDER = GWT.create(StructuredTextViewerUiBinder.class);
    private int columns;
    private boolean hasHeader;
    private Splittable headerRow;
    private GridInlineEditing<Splittable> rowEditing;
    private List<Splittable> skippedRows;

    public StructuredTextViewer(final File file,
                                final String infoType,
                                final boolean editing,
                                final Integer columns,
                                final FileViewer.Presenter presenter) {
        super(file, infoType, editing, presenter);
        toolbar = new StructuredTextViewToolBar(editing);
        initWidget(BINDER.createAndBindUi(this));
        if (file != null) {
            presenter.loadStructuredData(pagingToolBar.getPageNumber(),
                                         pagingToolBar.getPageSize(),
                                         getSeparator());
        } else {
            Preconditions.checkArgument(editing, "New files must be editable");
            Preconditions.checkNotNull(columns, "Columns can't be null if file is null");
            Preconditions.checkArgument(columns > 0, "Columns must be greater than 0.");
            this.columns = columns;
            LOG.info("Columns: " + columns);
            setData(createNewStructuredText(columns));
            setDirty(true);
        }
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEvent.FileSavedEventHandler handler) {
        return addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getViewName(String fileName) {
        if (fileName != null) {
            return appearance.viewName(fileName);
        } else {
            return appearance.defaultViewName();
        }
    }

    @Override
    public boolean isDirty() {
        LOG.fine("is dirty ?: " + dirty);
        return dirty;
    }

    @Override
    public void setData(Object data) {
        super.setData(data);
        // Need to subtract 1 from the column count to account for the row numberer column
        this.columns = columnModel.getColumnCount() - 1;
    }

    Splittable createNewRow(int columns) {
        Splittable obj = StringQuoter.createIndexed();
        for (int i = 0; i < columns; i++) {
            String newDefaultColumnValue = appearance.createNewDefaultColumnValue(i);
            StringQuoter.create(newDefaultColumnValue).assign(obj, String.valueOf(i));
        }
        return obj;
    }

    StructuredText createNewStructuredText(int columns) {
        Splittable ret = StringQuoter.createSplittable();
        StringQuoter.create(String.valueOf(columns)).assign(ret, StructuredText.COL_KEY);
        Splittable indexed = StringQuoter.createIndexed();
        createNewRow(columns).assign(indexed, 0);
        indexed.assign(ret, StructuredText.DATA_KEY);
        CommonModelAutoBeanFactory factory = GWT.create(CommonModelAutoBeanFactory.class);
        return AutoBeanCodex.decode(factory, StructuredText.class, ret).as();
    }

    @Override
    void doSave() {
        if (listStore.size() == 0) {
            toolbar.setSaveEnabled(false);
            return;
        }
        super.doSave();
    }

    @Override
    void doSetDirty(boolean dirty) {
        super.doSetDirty(dirty);
        if (dirty) {
            toolbar.setSaveEnabled(true);
        }
    }

    @UiHandler("toolbar")
    void onAddRowSelected(AddRowSelectedEvent event) {
        Splittable newRow = createNewRow(columns);
        if (rowEditing != null) {
            rowEditing.cancelEditing();
            listStore.add(newRow);
            int row = listStore.indexOf(newRow);
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

    @Override
    @SuppressWarnings("unchecked")
    void setEditing(boolean editing) {
        if (grid == null) {
            return;
        }

        toolbar.setEditing(editing);

        if (editing) {
            if (rowEditing == null) {
                // Initialize row editing
                grid.setToolTip(appearance.gridToolTip());
                rowEditing = new GridInlineEditing<>(grid);
                rowEditing.setClicksToEdit(ClicksToEdit.TWO);
                rowEditing.addCompleteEditHandler(new CompleteEditHandler<Splittable>() {

                    @Override
                    public void onCompleteEdit(CompleteEditEvent<Splittable> event) {
                        setDirty(true);
                        listStore.commitChanges();
                    }
                });

            }
            rowEditing.clearEditors();
            List<ColumnConfig<Splittable, ?>> cols = grid.getColumnModel().getColumns();
            for (ColumnConfig<Splittable, ?> cc : cols) {
                TextField field = new TextField();
                field.setClearValueOnParseError(false);
                rowEditing.addEditor((ColumnConfig<Splittable, String>) cc, field);
            }
        } else {
            rowEditing = null;
            grid.removeToolTip();
        }
    }

    private void defineColumnHeader() {
        ColumnModel<Splittable> cm = grid.getColumnModel();
        List<ColumnConfig<Splittable, ?>> configs = cm.getColumns();
        Splittable firstRow = listStore.get(0);
        int index = 0;
        for (ColumnConfig<Splittable, ?> conf : configs) {
            if (cm.indexOf(conf) != 0) { // col 0 is numberer
                if (hasHeader) {
                    Preconditions.checkNotNull(firstRow);
                    Splittable splittable = firstRow.get(String.valueOf(index));
                    Splittable string = (splittable != null) ? splittable : null;
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
    }
}
