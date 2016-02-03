package org.iplantc.de.fileViewers.client.views;

import static org.iplantc.de.client.models.viewer.InfoType.BED;
import static org.iplantc.de.client.models.viewer.InfoType.BOWTIE;
import static org.iplantc.de.client.models.viewer.InfoType.CSV;
import static org.iplantc.de.client.models.viewer.InfoType.GFF;
import static org.iplantc.de.client.models.viewer.InfoType.GTF;
import static org.iplantc.de.client.models.viewer.InfoType.HT_ANALYSIS_PATH_LIST;
import static org.iplantc.de.client.models.viewer.InfoType.TSV;
import static org.iplantc.de.client.models.viewer.InfoType.VCF;
import static org.iplantc.de.client.services.FileEditorServiceFacade.COMMA_DELIMITER;
import static org.iplantc.de.client.services.FileEditorServiceFacade.SPACE_DELIMITER;
import static org.iplantc.de.client.services.FileEditorServiceFacade.TAB_DELIMITER;

import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.events.LineNumberCheckboxChangeEvent;
import org.iplantc.de.fileViewers.client.events.RefreshSelectedEvent;
import org.iplantc.de.fileViewers.client.events.SaveSelectedEvent;
import org.iplantc.de.fileViewers.client.events.ViewerPagingToolbarUpdatedEvent;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.sencha.gxt.widget.core.client.grid.RowNumberer;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;

import java.util.List;
import java.util.logging.Logger;

/**
 * An abstract class which performs general layout and functionality for structured viewers.
 * All structured viewers consist of a top-level toolbar, a grid, and a paging toolbar. Implementing
 * classes are responsible for providing (either manually or via {@link UiFactory}) the {@link #toolbar}.
 *
 * This class defines how the {@link Grid} and {@link ColumnModel} are initialized via the
 * {@link #setData(Object)} method.
 *
 * Handling updates from the paging toolbar.
 * Handling of save and refresh selected events from top toolbar.
 *
 *
 * @author jstroot
 */
public abstract class AbstractStructuredTextViewer extends AbstractFileViewer {
    public interface AbstractStructuredTextViewerAppearance {

    }

    static final class StructuredTextValueProvider implements ValueProvider<Splittable, String> {

        private final Integer columnIndex;

        StructuredTextValueProvider(final int columnIndex){
            this.columnIndex = columnIndex;
        }

        @Override
        public String getValue(Splittable object) {
            if(object.isUndefined(columnIndex.toString())){
                return "";
            }
            Splittable value = object.get(columnIndex.toString());
            return value.asString();
        }

        @Override
        public void setValue(Splittable object, String value) {
            String val = Strings.nullToEmpty(value);
            StringQuoter.create(val).assign(object, columnIndex.toString());
        }

        @Override
        public String getPath() {
            return columnIndex.toString();
        }
    }

    static class StructuredTextModelKeyProvider implements ModelKeyProvider<Splittable> {
        private int index;
        @Override
        public String getKey(Splittable item) {
            return String.valueOf(index++);
        }
    }

    @UiField GridView gridView;
    @UiField ColumnModel columnModel;
    @UiField ListStore<Splittable> listStore;
    @UiField ViewerPagingToolBar pagingToolBar;
    @UiField Grid<Splittable> grid;
    @UiField RowNumberer<Splittable> rowNumberer = new RowNumberer<>();
    @UiField GridFilters<Splittable> gridFilters = new GridFilters<>();

    final boolean editing;
    protected final FileViewer.Presenter presenter;

    protected Logger LOG;
    protected boolean dirty;

    public AbstractStructuredTextViewer(final File file,
                                        final String infoType,
                                        final boolean editing,
                                        final FileViewer.Presenter presenter) {
        super(file, infoType);
        this.editing = editing;

        this.presenter = presenter;
        initLogger();
    }

    protected Logger initLogger(){
        if(LOG == null){
            LOG = Logger.getLogger(this.getClass().getName());
        }
        return LOG;
    }

    String getSeparator() {
        InfoType fromTypeString = InfoType.fromTypeString(infoType);
        if (CSV.equals(fromTypeString)
            || HT_ANALYSIS_PATH_LIST.equals(fromTypeString)) {
            return COMMA_DELIMITER;
        } else if (TSV.equals(fromTypeString)
                       || VCF.equals(fromTypeString)
                       || GFF.equals(fromTypeString)
                       || BED.equals(fromTypeString)
                       || GTF.equals(fromTypeString)
                       || BOWTIE.equals(fromTypeString)) {
            return TAB_DELIMITER;
        } else {
            return SPACE_DELIMITER;
        }
    }

    @UiFactory ListStore<Splittable> createListStore(){
        return new ListStore<>(new StructuredTextModelKeyProvider());
    }

    @UiFactory ViewerPagingToolBar createPagingToolBar(){
        return new ViewerPagingToolBar(getFileSize());
    }

    @UiFactory ColumnModel<Splittable> createColumnModel() {
        return doFactoryCreateColumnModel();
    }

    ColumnModel<Splittable> doFactoryCreateColumnModel() {
        return new ColumnModel<>(Lists.<ColumnConfig<Splittable, ?>>newArrayList());
    }

    @UiHandler("toolbar") void onSaveSelected(SaveSelectedEvent event){
        doSave();
    }

    void doSave() {
        listStore.commitChanges();
        presenter.saveFile(this);
    }

    @UiHandler("toolbar") void onRefreshSelected(RefreshSelectedEvent event){
        presenter.loadStructuredData(pagingToolBar.getPageNumber(),
                                     pagingToolBar.getPageSize(),
                                     getSeparator());
    }

    @UiHandler("pagingToolBar") void onPagingToolbarChanged(ViewerPagingToolbarUpdatedEvent event) {
        presenter.loadStructuredData(event.getPageNumber(),
                                     event.getPageSize(),
                                     getSeparator());
    }

    @UiHandler("toolbar") void onLineNumberCheckboxValueChangeEvent(LineNumberCheckboxChangeEvent event){
        rowNumberer.setHidden(!event.getValue());
        grid.getView().refresh(true);
    }

    @Override
    public void refresh() {
        presenter.loadStructuredData(pagingToolBar.getPageNumber(),
                                     pagingToolBar.getPageSize(),
                                     getSeparator());
    }

    @Override
    public void setData(Object data){
        Preconditions.checkNotNull(data);
        // Only accept StructuredText types
        if(!(data instanceof StructuredText)){
            return;
        }
        StructuredText structuredText = (StructuredText)data;
        Preconditions.checkArgument(structuredText.getData().isIndexed(), "Structured text does not contain indexed data!");

        loadStructuredData(structuredText);

        // Create new column model, clear previous grid filters
        gridFilters.removeAll();

        columnModel = createColumnModel(structuredText);
        gridFilters.initPlugin(grid);
        grid.reconfigure(listStore, columnModel);
        grid.getView().refresh(true);
        setEditing(editing);
        setDirty(false);
    }

    void setEditing(boolean editing) {


    }

    void loadStructuredData(StructuredText structuredText){
        // Update ListStore
        listStore.clear();
        for (int i = 0; i < structuredText.getData().size(); i++){
            listStore.add(structuredText.getData().get(i));
        }
    }


    ColumnModel<Splittable> createColumnModel(final StructuredText structuredText){
        List<ColumnConfig<Splittable, ?>> configs = Lists.newArrayList();

        // Add RowNumberer first
        configs.add(rowNumberer);
        int columns = Integer.valueOf(structuredText.getMaxColumns());

        for(int c = 0; c < columns; c++){
            StructuredTextValueProvider valueProvider = new StructuredTextValueProvider(c);
            ColumnConfig<Splittable, String> col = new ColumnConfig<>(valueProvider);
            col.setHeader(String.valueOf(c));

            StringFilter<Splittable> stringFilter = new StringFilter<>(valueProvider);
            gridFilters.addFilter(stringFilter);
            configs.add(col);
        }
        return new ColumnModel<>(configs);
    }

    @Override
    public String getEditorContent() {
        StringBuilder sw = new StringBuilder();

        Joiner joiner = Joiner.on(getSeparator()).skipNulls();
        String NEW_LINE = "\n";
        for(Splittable split : listStore.getAll()){
            joiner.appendTo(sw, splittableToStringList(split));
            sw.append(NEW_LINE);
        }

        LOG.fine("Editor Content: " + sw.toString());
        return sw.toString();
    }

    List<String> splittableToStringList(Splittable split){
        List<String> ret = Lists.newArrayList();
        for(String key : split.getPropertyKeys()){
            ret.add(split.get(key).asString());
        }
        return ret;
    }

    @Override
    public boolean isDirty() {
        LOG.fine("is dirty : " + dirty);
        return dirty;
    }

    void setDirty(boolean dirty){
        doSetDirty(dirty);
    }

    void doSetDirty(boolean dirty) {
        LOG.fine("set dirty : " + dirty);
        this.dirty = dirty;
        presenter.setViewDirtyState(dirty, this);
    }

}
