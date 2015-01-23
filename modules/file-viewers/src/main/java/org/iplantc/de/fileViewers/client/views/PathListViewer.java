package org.iplantc.de.fileViewers.client.views;

import org.iplantc.de.client.events.FileSavedEvent;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.viewer.InfoType;
import org.iplantc.de.client.models.viewer.StructuredText;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.info.ErrorAnnouncementConfig;
import org.iplantc.de.commons.client.info.IplantAnnouncer;
import org.iplantc.de.fileViewers.client.FileViewer;
import org.iplantc.de.fileViewers.client.events.DeleteSelectedPathsSelectedEvent;
import org.iplantc.de.resources.client.messages.I18N;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.Splittable;
import com.google.web.bindery.autobean.shared.impl.StringQuoter;

import static com.sencha.gxt.dnd.core.client.DND.Feedback.INSERT;
import static com.sencha.gxt.dnd.core.client.DND.Operation.MOVE;

import com.sencha.gxt.core.shared.event.CancellableEvent;
import com.sencha.gxt.data.shared.event.StoreAddEvent;
import com.sencha.gxt.data.shared.event.StoreRemoveEvent;
import com.sencha.gxt.dnd.core.client.DndDragEnterEvent;
import com.sencha.gxt.dnd.core.client.DndDragMoveEvent;
import com.sencha.gxt.dnd.core.client.DndDropEvent;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
import com.sencha.gxt.dnd.core.client.StatusProxy;
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
            StringBuilder splChars = checkForSplChar(grid.getStore().getAll());
            if (splChars.length() > 0) {
                LOG.fine("splChars:" + splChars + ":");
                IplantAnnouncer.getInstance()
                               .schedule(new ErrorAnnouncementConfig(appearance.analysisFailureWarning(appearance.warnedDiskResourceNameChars()),
                                                                     true,
                                                                     5000));
            }

        }

        @Override
        protected void onDragEnter(DndDragEnterEvent event) {
            handleDropStatus(event.getDragSource().getData(),
                             event,
                             event.getStatusProxy());
        }

        @Override
        protected void onDragMove(DndDragMoveEvent event) {
            super.onDragMove(event);
            handleDropStatus(event.getDragSource().getData(),
                             event,
                             event.getStatusProxy());
        }

        @Override
        protected List<Object> prepareDropData(Object data, boolean convertTreeStoreModel) {
            // If drop data does not look like it came from DiskResource window
            if (!hasCorrectData(data)) {
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

        void handleDropStatus(Object o,
                              CancellableEvent cancellableEvent,
                              StatusProxy statusProxy) {

            if(!hasCorrectData(o)){
                cancellableEvent.setCancelled(true);
                statusProxy.setStatus(false);
            }
            // TODO Check to see if any items are pathlists. If they are, prevent drop
            Iterable<DiskResource> iterable = (Iterable<DiskResource>) o;
            for(DiskResource dr : iterable){
                InfoType infoType1 = InfoType.fromTypeString(dr.getInfoType());
                if(InfoType.HT_ANALYSIS_PATH_LIST.equals(infoType1)){
                    cancellableEvent.setCancelled(true);
                    statusProxy.update(appearance.preventPathListDrop());
                    statusProxy.setStatus(false);
                    return;
                }
            }
            cancellableEvent.setCancelled(false);
            statusProxy.setStatus(true);
        }

        boolean hasCorrectData(Object data){

            boolean isCollection = data instanceof Collection<?>;
            boolean isEmpty = ((Collection<?>) data).isEmpty();
            boolean hasDiskResources = ((Collection<?>) data).iterator().next() instanceof DiskResource;
            return isCollection
                       && !isEmpty
                       && hasDiskResources;
        }

        private StringBuilder checkForSplChar(List<Splittable> idSet) {
            char[] restrictedChars = (appearance.warnedDiskResourceNameChars()).toCharArray(); //$NON-NLS-1$
            StringBuilder restrictedFound = new StringBuilder();

            for (Splittable path : idSet) {
                String diskResourceId = path.get(0).asString();
                LOG.fine("filename=" + diskResourceId);
                for (char restricted : restrictedChars) {
                    for (char next : diskResourceId.toCharArray()) {
                        if (next == restricted && next != '/') {
                            restrictedFound.append(restricted);
                        }
                    }
                }
                LOG.fine("DiskresourceUtil:" + diskResourceUtil.toString());
                LOG.fine("restricted chars found 1 =" + restrictedFound);
                // validate '/' only on label
                for (char next : diskResourceUtil.parseNameFromPath(diskResourceId).toCharArray()) {
                    if (next == '/') {
                        restrictedFound.append('/');
                    }
                }

            }
            LOG.fine("restricted chars found 2=" + restrictedFound);
            return restrictedFound;
        }

    }

    interface FileListViewerUiBinder extends UiBinder<BorderLayoutContainer, PathListViewer> {
    }

    public interface PathListViewerAppearance extends AbstractStructuredTextViewerAppearance {
        String analysisFailureWarning(String warnedNameCharacters);

        String columnHeaderText();

        String pathListViewName(String name);

        String preventPathListDrop();

        String warnedDiskResourceNameChars();
    }

    @UiField(provided = true) PathListViewerToolbar toolbar;

    Logger LOG = Logger.getLogger(PathListViewer.class.getName());
    private static FileListViewerUiBinder ourUiBinder = GWT.create(FileListViewerUiBinder.class);
    private final PathListViewerAppearance appearance = GWT.create(PathListViewerAppearance.class);
    private final File file;

    private final DiskResourceUtil diskResourceUtil;

    public PathListViewer(final File file,
                          final String infoType,
                          final boolean editing,
                          final FileViewer.Presenter presenter,
                          final DiskResourceUtil diskResourceUtil) {
        super(file, infoType, editing, presenter);
        this.diskResourceUtil = diskResourceUtil;
        if (file != null) {
            Preconditions.checkArgument(InfoType.HT_ANALYSIS_PATH_LIST.toString().equals(infoType));
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

        grid.getView().setEmptyText("Drag and drop file(s) and folder(s)...");

        PathListViewerGridDropTarget diskResourceDropTarget = new PathListViewerGridDropTarget(grid);
        diskResourceDropTarget.setOperation(MOVE);
        diskResourceDropTarget.setFeedback(INSERT);
        diskResourceDropTarget.setAllowSelfAsSource(true);

        if (file != null) {
            presenter.loadStructuredData(pagingToolBar.getPageNumber(),
                                         pagingToolBar.getPageSize(),
                                         getSeparator());
        }
    }

    @Override
    public HandlerRegistration addFileSavedEventHandler(FileSavedEvent.FileSavedEventHandler handler) {
        return addHandler(handler, FileSavedEvent.TYPE);
    }

    @Override
    public String getEditorContent() {
        String pathListFileIdentifier = presenter.getPathListFileIdentifier() + "\n";
        return pathListFileIdentifier + super.getEditorContent();
    }

    @Override
    public String getViewName(String fileName) {
        return fileName == null
                   ? appearance.pathListViewName(String.valueOf(Math.random()))
                   : appearance.pathListViewName(fileName);
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

        return new ColumnModel<>(configs);
    }

    @Override
    void doSetDirty(boolean dirty) {
        super.doSetDirty(dirty);
        if(dirty){
            toolbar.setSaveEnabled(true);
        }
    }

    @Override
    void doSave() {
        if(listStore.size() == 0){
            toolbar.setSaveEnabled(false);
            return;
        }
        super.doSave();
    }

    @Override
    void loadStructuredData(StructuredText structuredText) {
        // Update ListStore
        listStore.clear();
        // Skip first row
        for (int i = 1; i < structuredText.getData().size(); i++) {
            listStore.add(structuredText.getData().get(i));
        }
    }

    @UiHandler("toolbar")
    void onDeleteSelectedItemClicked(DeleteSelectedPathsSelectedEvent event) {
        List<Splittable> selectedItems = grid.getSelectionModel().getSelectedItems();
        for (Splittable item : selectedItems) {
            listStore.remove(item);
        }
    }

}
