package org.iplantc.de.fileViewers.client.views;

import static org.iplantc.de.client.models.viewer.InfoType.*;
import static org.iplantc.de.client.services.FileEditorServiceFacade.*;
import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.client.models.viewer.StructuredTextAutoBeanFactory;
import org.iplantc.de.client.services.FileEditorServiceFacade;
import org.iplantc.de.client.util.JsonUtil;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.fileViewers.client.events.ViewerPagingToolbarUpdatedEvent;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.resources.client.messages.IplantErrorStrings;

import com.google.common.base.Joiner;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CellSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * FIXME JDS Need to extract this into a ui.xml
 * @author sriram, jstroot
 */
public class StructuredTextViewerImpl extends StructuredTextViewer implements ViewerPagingToolbarUpdatedEvent.ViewerPagingToolbarUpdatedEventHandler {

    private class ReadCsvChunkCallback implements AsyncCallback<String> {

        private final Grid<JSONObject> grid;
        private final ViewerPagingToolBar pagingToolBar;
        private final StructuredTextAutoBeanFactory structuredTextAutoBeanFactory;
        private final Component maskable;
        private final String fileName;
        private final IplantErrorStrings errorStrings1;

        ReadCsvChunkCallback(final Grid<JSONObject> grid,
                             final ViewerPagingToolBar pagingToolBar,
                             final StructuredTextAutoBeanFactory structuredTextAutoBeanFactory,
                             final Component maskable,
                             final String fileName,
                             final IplantErrorStrings errorStrings1){
            this.grid = grid;
            this.pagingToolBar = pagingToolBar;
            this.structuredTextAutoBeanFactory = structuredTextAutoBeanFactory;
            this.maskable = maskable;
            this.fileName = fileName;
            this.errorStrings1 = errorStrings1;
        }

        @Override
        public void onSuccess(String result) {
            StructuredText textBean = decodeStructuredText(result);
            if (grid == null) {
                initGrid(Integer.parseInt(textBean.getMaxColumns()));
            }
            Splittable sp = StringQuoter.split(result);
            setData(sp);
            setEditing(pagingToolBar.getTotalPages() == 1);
            maskable.unmask();
        }

        StructuredText decodeStructuredText(final String result){

            AutoBean<StructuredText> bean = AutoBeanCodex.decode(structuredTextAutoBeanFactory,
                                                                 StructuredText.class,
                                                                 result);
            return bean.as();
        }

        @Override
        public void onFailure(Throwable caught) {
            ErrorHandler.post(errorStrings1.unableToRetrieveFileData(fileName),
                              caught);
            maskable.unmask();
        }
    }

    private final class StructuredTextValueProvider implements ValueProvider<JSONObject, String> {
        private final int index;

        private StructuredTextValueProvider(int index) {
            this.index = index;
        }

        @Override
        public String getPath() {
            return index + "";
        }

        @Override
        public String getValue(JSONObject object) {
            JSONValue val = object.get(index + "");
            if (val != null) {
                return val.isString().stringValue();
            } else {
                return "";
            }
        }

        @Override
        public void setValue(JSONObject object, String value) {
            if (value == null) {
                value = "";
            }
            object.put(index + "", new JSONString(value));
        }
    }

    Logger LOG = Logger.getLogger(StructuredTextViewerImpl.class.getName());

    private final IplantDisplayStrings displayStrings;
    private final IplantErrorStrings errorStrings;
    private final StructuredTextAutoBeanFactory factory;
    private final FileEditorServiceFacade fileEditorService;

    private final Folder parentFolder;
    private final FileViewer.Presenter presenter;

    private VerticalLayoutContainer center;
    private BorderLayoutContainer container;
    private Grid<JSONObject> grid;
    private ContentPanel north;
    private RowNumberer<JSONObject> numberer;
    private ViewerPagingToolBar pagingToolbar;
    private GridInlineEditing<JSONObject> rowEditing;
    private ContentPanel south;
    private StructuredTextViewToolBar toolbar;
    private ListStore<JSONObject> store;

    private int columns;
    private boolean dirty;
    private boolean hasHeader;
    private JSONObject headerRow;
    private List<JSONObject> skippedRows;

    public StructuredTextViewerImpl(final File file,
                                    final String infoType,
                                    final Integer columns,
                                    final Folder parentFolder,
                                    final FileViewer.Presenter presenter,
                                    final IplantDisplayStrings displayStrings,
                                    final IplantErrorStrings errorStrings,
                                    final StructuredTextAutoBeanFactory factory,
                                    final FileEditorServiceFacade fileEditorService) {
        super(file, infoType);
        this.parentFolder = parentFolder;
        this.presenter = presenter;
        this.displayStrings = displayStrings;
        this.errorStrings = errorStrings;
        this.factory = factory;
        this.fileEditorService = fileEditorService;
        initLayout();
        initToolbar();
        initPagingToolbar();
//        loadData();
        if (columns != null) {
            LOG.info("Columns: " + columns);
            initGrid(columns);
            setEditing(true);
            addRow();
        }
        addLineNumberHandler();
    }


    @Override
    public HandlerRegistration addFileSavedEventHandler(final FileSavedEvent.FileSavedEventHandler handler) {
        return asWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public void addRow() {
        JSONObject obj = new JSONObject();
        for (int i = 0; i < columns; i++) {
            obj.put(i + "", new JSONString("col" + i));
        }
        if (rowEditing != null) {
            rowEditing.cancelEditing();
            getStore().add(obj);
            int row = getStore().indexOf(obj);
            setDirty(true);
            rowEditing.startEditing(new GridCell(row, 1));
        }
    }

    @Override
    public Widget asWidget() {
        return container;
    }

    @Override
    public void deleteRow() {
        List<JSONObject> selectedRows = grid.getSelectionModel().getSelectedItems();
        if (selectedRows != null && selectedRows.size() > 0) {
            for (JSONObject obj : selectedRows) {
                getStore().remove(obj);
            }
            setDirty(true);
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        container.fireEvent(event);
    }

    @Override
    public String getViewName() {
        if (file != null) {
            return "Tabular View: " + file.getName();
        } else {
            return "Tabular View";
        }
    }

//    @Override
//    public boolean isDirty() {
//        return dirty;
//    }

    @Override
    public void mask(String loadingMask) {
        container.mask(loadingMask);
    }

    @Override
    public void unmask() {
        container.unmask();
    }

    @Override
    public void onViewerPagingToolbarUpdated(ViewerPagingToolbarUpdatedEvent event) {
        // Tell presenter to do it
//        loadData();
        presenter.loadStructuredData(event.getPageNumber(),
                                     event.getPageSize(),
                                     getSeparator());
    }

//    @Override
    void setDirty(Boolean dirty) {
        this.dirty = dirty;
        if (presenter.isDirty() == dirty) {
            return;
        }
        presenter.setViewDirtyState(dirty);
    }

    /*void loadData() {
        if (file == null) {
            return;
        }
        container.mask(displayStrings.loadingMask());
        fileEditorService.readCsvChunk(file,
                                       getSeparator(),
                                       pagingToolbar.getPageNumber(),
                                       pagingToolbar.getPageSize(),
                                       new ReadCsvChunkCallback(grid,
                                                                pagingToolbar,
                                                                factory,
                                                                container,
                                                                file.getName(),
                                                                errorStrings));
    }*/

    @Override
    public void loadDataWithHeader(boolean header) {
        hasHeader = header;
        defineColumnHeader();
    }

    @Override
    public void refresh() {
//        loadData();
        presenter.loadStructuredData(pagingToolbar.getPageNumber(),
                                     (int) pagingToolbar.getPageSize(),
                                     getSeparator());
    }

    /*@Override
    public void save() {
        // FIXME this should occur in a handler of the toolbar
        store.commitChanges();
        presenter.saveFile();
        if (file == null) {
            final SaveAsDialog saveDialog = new SaveAsDialog(parentFolder);
            SaveAsDialogOkSelectHandler okSelectHandler = new SaveAsDialogOkSelectHandler(container,
                                                                                          saveDialog,
                                                                                          displayStrings.savingMask(),
                                                                                          getEditorContent(),
                                                                                          fileEditorService);
            SaveAsDialogCancelSelectHandler cancelSelectHandler = new SaveAsDialogCancelSelectHandler(container,
                                                                                                      saveDialog);
            saveDialog.addOkButtonSelectHandler(okSelectHandler);
            saveDialog.addCancelButtonSelectHandler(cancelSelectHandler);
            saveDialog.show();
            saveDialog.toFront();
        } else {
            container.mask(displayStrings.savingMask());
            fileEditorService.uploadTextAsFile(file.getPath(),
                                               getEditorContent(),
                                               false,
                                               new FileSaveCallback(file.getPath(),
                                                                    false,
                                                                    container));
        }
    }*/

    @Override
    public void setData(Object data) {
        // FIXME initGrid here
        // FIXME
        Splittable textData = (Splittable) data;
        JSONObject obj = JsonUtil.getObject(textData.getPayload());
        JSONArray arr = obj.get("csv").isArray();

        if (arr != null && arr.size() > 0) {
            store.clear();
            for (int i = 0; i < arr.size(); i++) {
                store.add(arr.get(i).isObject());
            }
        }

        if (pagingToolbar.getPageNumber() == 1) {
            skipRows(toolbar.getSkipRowCount());
            if (hasHeader) {
                if (headerRow == null) {
                    defineColumnHeader();
                } else {
                    // just remove the first row bcos header is set
                    store.remove(0);
                }
            }
        }

    }

    @Override
    public void skipRows(int val) {
        if (val > 0 && val < store.size() + ((skippedRows != null) ? skippedRows.size() : 0)) {
            if (skippedRows == null) {
                skippedRows = new ArrayList<>();
                // increment
                skippedRows.addAll(store.subList(0, val));
            } else if (skippedRows.size() > val) {
                // reduction
                for (int j = skippedRows.size() - 1; j >= val; j--) {
                    store.add(0, skippedRows.remove(j));
                }
            } else if (skippedRows.size() < val) {
                // increment
                skippedRows.addAll(store.subList(0, val - skippedRows.size()));
            } else {
                // same size
                skippedRows.clear();
                skippedRows.addAll(store.subList(0, val));
            }
            for (JSONObject obj : skippedRows) {
                store.remove(obj);
            }

        } else {
            if (val == 0 && skippedRows != null && skippedRows.size() > 0) {
                // add back the skipped rows.
                for (int j = 0; j < skippedRows.size(); j++) {
                    store.add(j, skippedRows.get(j));
                }
            }
            skippedRows = null;
        }

        grid.getView().refresh(true);

    }

    private void addLineNumberHandler() {
        toolbar.addLineNumberCbxChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                numberer.setHidden(!event.getValue());
                grid.getView().refresh(false);
            }

        });
    }

    private void defineColumnHeader() {
        ColumnModel<JSONObject> cm = grid.getColumnModel();
        List<ColumnConfig<JSONObject, ?>> configs = cm.getColumns();
        JSONObject firstRow = store.get(0);
        int index = 0;
        for (ColumnConfig<JSONObject, ?> conf : configs) {
            if (cm.indexOf(conf) != 0) { // col 0 is numberer
                JSONString string = (firstRow.get(index + "") != null) ? firstRow.get(index + "")
                                                                                 .isString() : null;
                if (hasHeader) {
                    conf.setHeader((string != null) ? string.stringValue() : index + "");
                } else {
                    conf.setHeader(index + "");
                }
                index++;
            }

        }

        // converted first row to header. so remove first row
        if (hasHeader) {
            store.remove(firstRow);
            headerRow = firstRow;
        } else {
            if (headerRow != null) {
                // if it was removed prev, add the back row at 1st position
                store.add(0, headerRow);
                headerRow = null;
            }
        }

        grid.reconfigure(store, cm);
        grid.getView().refresh(true);
    }

    private MarginData getCenterData() {
        return new MarginData();
    }

    private String getEditorContent() {
        StringBuilder sw = new StringBuilder();
        Joiner joiner = Joiner.on(getSeparator()).skipNulls();
        String NEW_LINE = "\n";
        if (skippedRows != null && skippedRows.size() > 0) {
            for (JSONObject skipr : skippedRows) {
                joiner.appendTo(sw, jsonToStringList(skipr));
                sw.append(NEW_LINE);
            }
        }
        if (headerRow != null) {
            joiner.appendTo(sw, jsonToStringList(headerRow));
            sw.append(NEW_LINE);
        }
        for (JSONObject obj : getStore().getAll()) {
            joiner.appendTo(sw, jsonToStringList(obj));
            sw.append(NEW_LINE);
        }
        return sw.toString();
    }

    private BorderLayoutData getNorthData() {
        BorderLayoutData northData = new BorderLayoutData(30);
        northData.setMargins(new Margins(5));
        northData.setCollapsible(false);
        northData.setSplit(false);
        return northData;
    }

    private String getSeparator() {
        if (CSV.toString().equalsIgnoreCase(infoType)) {
            return COMMA_DELIMITER;
        } else if (TSV.toString().equalsIgnoreCase(infoType)
                    || VCF.toString().equalsIgnoreCase(infoType)
                    || GFF.toString().equalsIgnoreCase(infoType)) {
            return TAB_DELIMITER;
        } else {
            return SPACE_DELIMITER;
        }
    }

    private BorderLayoutData getSouthData() {
        BorderLayoutData southData = new BorderLayoutData(30);
        southData.setMargins(new Margins(5));
        southData.setCollapsible(false);
        southData.setSplit(false);
        return southData;
    }

    private ListStore<JSONObject> getStore() {
        if (store == null) {
            store = new ListStore<>(new ModelKeyProvider<JSONObject>() {

                private int index;

                @Override
                public String getKey(JSONObject item) {
                    return index++ + "";
                }

            });
        }

        return store;
    }

    private void initGrid(int columns) {
        List<ColumnConfig<JSONObject, ?>> configs = new ArrayList<>();
        numberer = new RowNumberer<>();
        numberer.setHidden(true);
        configs.add(numberer);
        GridFilters<JSONObject> filters = new GridFilters<>();
        if (columns > 0) {
            this.columns = columns;
            for (int i = 0; i < columns; i++) {
                StructuredTextValueProvider valueProvider = new StructuredTextValueProvider(i);
                ColumnConfig<JSONObject, String> col = new ColumnConfig<>(valueProvider);
                col.setHeader(i + "");
                StringFilter<JSONObject> strFilter = new StringFilter<>(valueProvider);
                filters.addFilter(strFilter);
                configs.add(col);
            }
        }

        grid = new Grid<>(getStore(), new ColumnModel<>(configs));
        grid.setSelectionModel(new CellSelectionModel<JSONObject>());
        grid.getView().setStripeRows(true);
        grid.getView().setTrackMouseOver(true);
        filters.initPlugin(grid);
        filters.setLocal(true);
        grid.setHeight(center.getOffsetHeight(true));
        center.add(grid, new VerticalLayoutData(1, 1, new Margins(0)));
    }

    private void initLayout() {
        container = new BorderLayoutContainer();
        north = new ContentPanel();
        north.setCollapsible(false);
        north.setHeaderVisible(false);

        south = new ContentPanel();
        south.setCollapsible(false);
        south.setHeaderVisible(false);

        center = new VerticalLayoutContainer();
        center.setScrollMode(ScrollMode.AUTO);

        container.setNorthWidget(north, getNorthData());
        container.setCenterWidget(center, getCenterData());
        container.setSouthWidget(south, getSouthData());
    }

    private void initPagingToolbar() {
        pagingToolbar = new ViewerPagingToolBar(getFileSize());
        pagingToolbar.addPagingToolbarChangedHandler(this);
        south.add(pagingToolbar);
    }

    private void initToolbar() {
        toolbar = new StructuredTextViewToolBar(this, false);
        toolbar.addRefreshHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                presenter.loadStructuredData(pagingToolbar.getPageNumber(),
                                             (int) pagingToolbar.getPageSize(),
                                             getSeparator());
            }
        });
        toolbar.addSaveHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                store.commitChanges();
                presenter.saveFile(StructuredTextViewerImpl.this, getEditorContent());
            }
        });
        north.add(toolbar);
    }

    private List<String> jsonToStringList(JSONObject obj) {
        List<String> strList = new ArrayList<>();
        for (String key : obj.keySet()) {
            JSONValue val = obj.get(key);
            if (val != null && val.isString() != null) {
                strList.add(val.isString().stringValue());
            }
        }

        return strList;
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
                rowEditing.addCompleteEditHandler(new CompleteEditHandler<JSONObject>() {

                    @Override
                    public void onCompleteEdit(CompleteEditEvent<JSONObject> event) {
                        dirty = true;
                        store.commitChanges();
                    }
                });

                List<ColumnConfig<JSONObject, ?>> cols = grid.getColumnModel().getColumns();
                for (ColumnConfig<JSONObject, ?> cc : cols) {
                    TextField field = new TextField();
                    field.setClearValueOnParseError(false);
                    rowEditing.addEditor((ColumnConfig<JSONObject, String>) cc, field);
                }
            }
        } else {
            rowEditing = null;
            grid.removeToolTip();
        }
    }
}
