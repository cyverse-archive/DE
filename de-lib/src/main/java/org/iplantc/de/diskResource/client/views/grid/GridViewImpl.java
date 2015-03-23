package org.iplantc.de.diskResource.client.views.grid;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.events.DiskResourceNameSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourcePathSelectedEvent;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.events.search.SubmitDiskResourceQueryEvent;
import org.iplantc.de.diskResource.client.presenters.grid.proxy.FolderContentsLoadConfig;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import static com.sencha.gxt.core.client.Style.SelectionMode.SINGLE;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.BeforeLoadEvent;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.dnd.core.client.DND;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridCheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;


/**
 * Created by jstroot on 1/26/15.
 *
 * @author jstroot
 */
public class GridViewImpl extends ContentPanel implements GridView,
                                                          SelectionChangedEvent.SelectionChangedHandler<DiskResource> {
    interface GridViewImplUiBinder extends UiBinder<VerticalLayoutContainer, GridViewImpl> {
    }
    @UiField(provided = true) final GridView.Appearance appearance;
    @UiField(provided = true) final ListStore<DiskResource> listStore;
    @UiField ColumnModel<DiskResource> cm;
    @UiField Grid<DiskResource> grid;
    @UiField LiveGridView gridView;
    @UiField ToolBar pagingToolBar;
    @UiField TextField pathField;
    private static final GridViewImplUiBinder ourUiBinder = GWT.create(GridViewImplUiBinder.class);
    private final DiskResourceUtil diskResourceUtil;
    private final DiskResourceColumnModel drCm;
    private final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader;
    private final Status selectionStatus;
    private final LiveGridCheckBoxSelectionModel sm;

    @Inject
    GridViewImpl(final GridView.Appearance appearance,
                 final DiskResourceUtil diskResourceUtil,
                 @Assisted GridView.Presenter presenter,
                 @Assisted final ListStore<DiskResource> listStore,
                 @Assisted final FolderContentsRpcProxy folderContentsRpcProxy) {
        this.appearance = appearance;
        this.diskResourceUtil = diskResourceUtil;
        this.listStore = listStore;
        this.sm = new LiveGridCheckBoxSelectionModel();
        this.selectionStatus = new Status();
        setCollapsible(false);

        this.sm.addSelectionChangedHandler(this);

        // Setup Loader and init proxy
        gridLoader = new PagingLoader<>(folderContentsRpcProxy);
        gridLoader.setReuseLoadConfig(true);
        gridLoader.setRemoteSort(true);
        folderContentsRpcProxy.setHasSafeHtml(this.getHeader());

        VerticalLayoutContainer vlc = ourUiBinder.createAndBindUi(this);
        this.drCm = (DiskResourceColumnModel) cm;
        add(vlc);


        // Complete Grid setup post UIBinder create
        grid.setLoader(gridLoader);
        grid.setSelectionModel(sm);
        gridView.setAutoExpandColumn(cm.getColumn(1));

        // Initialize Paging toolbar
        LiveToolItem liveToolItem = new LiveToolItem(grid);
        liveToolItem.setWidth(appearance.liveToolItemWidth());
        selectionStatus.setWidth(appearance.selectionStatusItemWidth());

        pagingToolBar.add(liveToolItem);
        pagingToolBar.add(new FillToolItem());
        pagingToolBar.add(selectionStatus);
        appearance.setPagingToolBarStyle(pagingToolBar);
        updateSelectionCount(0);


        // Initialize Drag and Drop
        DropTarget gridDropTarget = new DropTarget(grid);
        gridDropTarget.setAllowSelfAsSource(true);
        gridDropTarget.setOperation(DND.Operation.COPY);
        GridViewDnDHandler dndHandler = new GridViewDnDHandler(diskResourceUtil,
                                                               presenter,
                                                               appearance);
        gridDropTarget.addDragEnterHandler(dndHandler);
        gridDropTarget.addDragMoveHandler(dndHandler);
        gridDropTarget.addDropHandler(dndHandler);

        DragSource gridDragSource = new DragSource(grid);
        gridDragSource.addDragStartHandler(dndHandler);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                final FolderContentsLoadConfig loadConfig = new FolderContentsLoadConfig();
                loadConfig.setLimit(gridView.getCacheSize());
                gridLoader.useLoadConfig(loadConfig);
            }
        });
    }

    //<editor-fold desc="Handler Registrations">
    @Override
    public HandlerRegistration addBeforeLoadHandler(BeforeLoadEvent.BeforeLoadHandler<FolderContentsLoadConfig> handler) {
        return gridLoader.addBeforeLoadHandler(handler);
    }

    @Override
    public HandlerRegistration addDiskResourceNameSelectedEventHandler(DiskResourceNameSelectedEvent.DiskResourceNameSelectedEventHandler handler) {
        return drCm.addDiskResourceNameSelectedEventHandler(handler);
    }

    @Override
    public HandlerRegistration addDiskResourcePathSelectedEventHandler(DiskResourcePathSelectedEvent.DiskResourcePathSelectedEventHandler handler) {
        return addHandler(handler, DiskResourcePathSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler handler) {
        return addHandler(handler, DiskResourceSelectionChangedEvent.TYPE);
    }


    //</editor-fold>

    //<editor-fold desc="Selection Event Handlers">
    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        final Folder selectedItem = event.getSelectedFolder();
        if (selectedItem == null
                || selectedItem.isFilter()) {
            return;
        }

        sm.clear();

        // Update pathField
        Scheduler.get().scheduleFinally(new Scheduler.ScheduledCommand() {

            @Override
            public void execute() {
                if (selectedItem instanceof DiskResourceQueryTemplate) {
                    pathField.clear();
                } else if (!selectedItem.getPath().equals(pathField.getCurrentValue())) {
                    pathField.setValue(selectedItem.getPath());
                }
            }
        });

        if (selectedItem instanceof DiskResourceFavorite
                || selectedItem instanceof DiskResourceQueryTemplate) {
            reconfigureToSearchView();
        } else {
            reconfigureToListingView();
        }
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
        updateSelectionCount(sm.getSelectedCount());

        fireEvent(new DiskResourceSelectionChangedEvent(event.getSelection()));
    }

    @Override
    public void doSubmitDiskResourceQuery(SubmitDiskResourceQueryEvent event) {
        Preconditions.checkNotNull(event.getQueryTemplate());
        reconfigureToSearchView();
    }
    //</editor-fold>

    @Override
    public Element findGridRow(Element eventTargetElement) {
        return gridView.findRow(eventTargetElement);
    }

    @Override
    public int findGridRowIndex(Element targetRow) {
        return gridView.findRowIndex(targetRow);
    }

    @Override
    public DiskResourceColumnModel getColumnModel() {
        return drCm;
    }

    @Override
    public PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> getGridLoader() {
        return gridLoader;
    }

    @Override
    public LiveGridCheckBoxSelectionModel getSelectionModel() {
        return sm;
    }

    @Override
    public void setSingleSelect() {
        grid.getSelectionModel().setSelectionMode(SINGLE);
        drCm.setCheckboxColumnHidden(true);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        grid.ensureDebugId(baseID);
        drCm.ensureDebugId(baseID);
    }

    @UiFactory
    ColumnModel<DiskResource> createColumnModel() {
        return new DiskResourceColumnModel(sm, appearance, diskResourceUtil);
    }

    @UiFactory
    LiveGridView<DiskResource> createLiveGridView() {
        // CORE-5723 KLUDGE for Firefox bug with LiveGridView row height calculation.
        // Always use a row height of 25 for now.

        final LiveGridView<DiskResource> liveGridView = new LiveGridView<DiskResource>() {
            @Override
            protected void insertRows(int firstRow, int lastRow, boolean isUpdate) {
                super.insertRows(firstRow, lastRow, isUpdate);

                setRowHeight(appearance.liveGridViewRowHeight());
            }
        };
        liveGridView.setCacheSize(500);
        return liveGridView;
    }

    @UiHandler("pathField")
    void onPathFieldKeyPress(KeyPressEvent event) {
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER
                && !Strings.isNullOrEmpty(pathField.getCurrentValue())) {
            String path = pathField.getCurrentValue();
            HasPath folderToSelect = CommonModelUtils.getInstance().createHasPathFromString(path);
            fireEvent(new DiskResourcePathSelectedEvent(folderToSelect));
        }
    }

    private void reconfigureToListingView() {
        sm.setShowSelectAll(true);
        // hide Path.
        grid.getColumnModel().getColumn(2).setHidden(true);
        grid.getView().refresh(true);
    }

    private void reconfigureToSearchView() {
        /* Search view does not support select all, otherwise, bulk download
         * logic would have to change.
         */
        sm.setShowSelectAll(false);
        // display Path
        grid.getColumnModel().getColumn(2).setHidden(false);
        grid.getView().refresh(true);
    }

    private void updateSelectionCount(int selectionCount) {
        selectionStatus.setText(selectionCount + " item(s)");
    }

}