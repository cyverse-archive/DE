package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.events.DeleteSelectedPathsSelectedEvent;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import static com.sencha.gxt.dnd.core.client.DND.Feedback.INSERT;
import static com.sencha.gxt.dnd.core.client.DND.Operation.MOVE;

import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * -- DnD of files/folders to this widget
 * -- DnD re-ordering of items within this widget
 * -- Line numbers
 * <p/>
 * This view differs from the {@link TextViewerImpl} and {@link StructuredTextViewer} views by
 * integrating the toolbar with the view in lieu of implementing the toolbar in a separate class.
 * <p/>
 * This view is a list which will contain all available {@code FileSet} items, so a paging-style
 * view will not be supported.
 *
 * @author jstroot
 */
public class PathListViewer extends AbstractStructuredTextViewer implements StoreRemoveEvent.StoreRemoveHandler<Splittable>, StoreAddEvent.StoreAddHandler<Splittable> {

    /**
     * DiskResource view adds a set of <? extends DiskResource> as the drop data
     */
    public class PathListViewerGridDropTarget extends GridDropTarget<Splittable> {

        /**
         * For now, index is always 0. This means that we are only supporting drag drop onto one
         * file set column at this time.
         */
        final Integer index = 0;

        /**
         * Creates a drop target for the specified grid.
         *
         * @param grid the grid to enable as a drop target
         */
        public PathListViewerGridDropTarget(Grid<Splittable> grid) {
            super(grid);
        }

        @Override
        protected void onDragDrop(DndDropEvent e) {
            super.onDragDrop(e);
        }

        @Override
        protected void onDragEnter(DndDragEnterEvent e) {
            boolean validDropData = isValidData(e.getDragSource().getData());
            e.setCancelled(!validDropData);
            e.getStatusProxy().setStatus(validDropData);
        }

        @Override
        protected List<Object> prepareDropData(Object data, boolean convertTreeStoreModel) {
            // If drop data does not look like it came from DiskResource window
            if (!isValidData(data)) {
                List<Object> elements = super.prepareDropData(data, convertTreeStoreModel);
                if (elements == null) {
                    elements = Collections.emptyList();
                }
                return elements;
            }
            List<Object> dropData = Lists.newArrayList();
            Set<DiskResource> diskResources = Sets.newHashSet((Collection<DiskResource>) data);
            for (HasPath hasPath : diskResources) {
                Splittable newData = StringQuoter.createSplittable();
                StringQuoter.create(hasPath.getPath()).assign(newData, index.toString());
                dropData.add(newData);
            }

            return dropData;
        }

        /**
         * @param data DnD data to be validated
         * @return true if data came from a DiskResource view, false otherwise
         */
        boolean isValidData(Object data) {
            boolean isCollection = data instanceof Collection<?>;
            boolean isEmpty = ((Collection<?>) data).isEmpty();
            boolean hasDiskResources = ((Collection<?>) data).iterator().next() instanceof DiskResource;
            return isCollection
                       && !isEmpty
                       && hasDiskResources;
        }
    }

    interface FileListViewerUiBinder extends UiBinder<BorderLayoutContainer, PathListViewer> {
    }

    public interface PathListViewerAppearance extends AbstractStructuredTextViewerAppearance {
        String columnHeaderText();

        String pathListViewName(String name);
    }

    Logger LOG = Logger.getLogger(PathListViewer.class.getName());
    @UiField(provided = true)
    PathListViewerToolbar toolbar;
    private static FileListViewerUiBinder ourUiBinder = GWT.create(FileListViewerUiBinder.class);
    private final File file;
    private final PathListViewerAppearance appearance = GWT.create(PathListViewerAppearance.class);

    public PathListViewer(final File file,
                          final String infoType,
                          final boolean editing,
                          final FileViewer.Presenter presenter) {
        super(file, infoType, editing, presenter);
        if (file != null) {
            Preconditions.checkArgument(InfoType.PATH_LIST.toString().equals(file.getInfoType()));
            presenter.loadPathListData(pagingToolBar.getPageNumber(),
                                       pagingToolBar.getPageSize(),
                                       getSeparator());
        } else {
            Preconditions.checkArgument(editing, "New files must be editable");
        }
        this.toolbar = new PathListViewerToolbar(editing);
        this.file = file;

        initWidget(ourUiBinder.createAndBindUi(this));
        listStore.addStoreRemoveHandler(this);
        listStore.addStoreAddHandler(this);
        rowNumberer.initPlugin(grid);

        // Set up DnD for self re-ordering
        GridDragSource<Splittable> dragSource = new GridDragSource<>(grid);
        GridDropTarget<Splittable> dropTarget = new GridDropTarget<>(grid);
        dropTarget.setAllowSelfAsSource(true);
        dropTarget.setOperation(MOVE);
        dropTarget.setFeedback(INSERT);

        PathListViewerGridDropTarget diskResourceDropTarget = new PathListViewerGridDropTarget(grid);
        diskResourceDropTarget.setOperation(MOVE);
        diskResourceDropTarget.setFeedback(INSERT);
        diskResourceDropTarget.setAllowSelfAsSource(true);
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(FileSavedEvent.FileSavedEventHandler handler) {
        return getWidget().addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getEditorContent() {
        String pathListFileIdentifier = presenter.getPathListFileIdentifier() + "\n";
        return pathListFileIdentifier + super.getEditorContent();
    }

    @Override
    public String getViewName() {
        return file == null
                   ? appearance.pathListViewName(String.valueOf(Math.random()))
                   : appearance.pathListViewName(file.getName());
    }

    @Override
    public void onAdd(StoreAddEvent<Splittable> event) {
        setDirty(true);
    }

    @Override
    public void onRemove(StoreRemoveEvent<Splittable> event) {
        setDirty(true);
    }

    @Override
    ColumnModel<Splittable> createColumnModel(final StructuredText structuredText) {
        // Do nothing
        return columnModel;
    }

    @Override
    ColumnModel<Splittable> doFactoryCreateColumnModel() {
        List<ColumnConfig<Splittable, ?>> configs = Lists.newArrayList();
        StructuredTextValueProvider valueProvider = new StructuredTextValueProvider(0);
        ColumnConfig<Splittable, String> col = new ColumnConfig<>(valueProvider);
        col.setHeader(appearance.columnHeaderText());

        // Add RowNumberer first
        configs.add(rowNumberer);
        configs.add(col);

        ColumnModel<Splittable> splittableColumnModel = new ColumnModel<>(configs);

        return splittableColumnModel;
    }

    @UiHandler("toolbar")
    void onDeleteSelectedItemClicked(DeleteSelectedPathsSelectedEvent event) {
        List<Splittable> selectedItems = grid.getSelectionModel().getSelectedItems();
        for (Splittable item : selectedItems) {
            listStore.remove(item);
        }
    }

}