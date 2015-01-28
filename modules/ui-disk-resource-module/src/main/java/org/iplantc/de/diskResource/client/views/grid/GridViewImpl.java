package org.iplantc.de.diskResource.client.views.grid;

import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceFavorite;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderPathSelectedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.share.DiskResourceModule;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridCheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;


/**
 * Created by jstroot on 1/26/15.
 * @author jstroot
 */
public class GridViewImpl extends ContentPanel implements GridView, BeforeLoadEvent.BeforeLoadHandler<FolderContentsLoadConfig>, SelectionChangedEvent.SelectionChangedHandler<DiskResource> {
    interface GridViewImplUiBinder extends UiBinder<VerticalLayoutContainer, GridViewImpl> {
    }

    private static GridViewImplUiBinder ourUiBinder = GWT.create(GridViewImplUiBinder.class);

    @UiField(provided = true) GridView.Appearance appearance;
    @UiField ToolBar pagingToolBar;
    @UiField Grid<DiskResource> grid;
    @UiField TextField pathField;
    @UiField(provided = true) ListStore<DiskResource> listStore;
    @UiField DiskResourceColumnModel cm;
    @UiField LiveGridView gridView;
    private final Status selectionStatus;
    private final LiveGridCheckBoxSelectionModel sm;
    private final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader;

    @Inject
    GridViewImpl(final GridView.Appearance appearance,
                 @Assisted final ListStore<DiskResource> listStore,
                 @Assisted final DiskResourceView.FolderContentsRpcProxy folderContentsRpcProxy) {
        this.appearance = appearance;
        this.listStore = listStore;
        this.sm = new LiveGridCheckBoxSelectionModel();
        this.selectionStatus = new Status();

        this.sm.addSelectionChangedHandler(this);


        // Setup Loader and init proxy
        gridLoader = new PagingLoader<>(folderContentsRpcProxy);
        gridLoader.setReuseLoadConfig(true);
        gridLoader.setRemoteSort(true);
        folderContentsRpcProxy.setHasSafeHtml(this.getHeader());
        gridLoader.addBeforeLoadHandler(this);

        VerticalLayoutContainer vlc = ourUiBinder.createAndBindUi(this);

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

        add(vlc);

        // Initialize Drag and Drop
        DropTarget gridDropTarget = new DropTarget(grid);
        gridDropTarget.setAllowSelfAsSource(true);
        gridDropTarget.setOperation(DND.Operation.COPY);
        // FIXME Migrate DiskResourceViewDndHandler
//        gridDropTarget.addDragEnterHandler(dndHandler);
//        gridDropTarget.addDragMoveHandler(dndHandler);
//        gridDropTarget.addDropHandler(dndHandler);

        DragSource gridDragSource = new DragSource(grid);
//        gridDragSource.addDragStartHandler(dndHandler);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                final FolderContentsLoadConfig loadConfig = new FolderContentsLoadConfig();
                loadConfig.setLimit(gridView.getCacheSize());
                gridLoader.useLoadConfig(loadConfig);
            }
        });
    }

    private void updateSelectionCount(int selectionCount) {
        selectionStatus.setText(selectionCount + " item(s)");
    }

    @Override
    public HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler handler) {
        return addHandler(handler, DiskResourceSelectionChangedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addFolderPathSelectedEventHandler(FolderPathSelectedEvent.FolderPathSelectedEventHandler handler) {
        return addHandler(handler, FolderPathSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addBeforeLoadHandler(BeforeLoadEvent.BeforeLoadHandler<FolderContentsLoadConfig> handler) {
        return gridLoader.addBeforeLoadHandler(handler);
    }

    @Override
    public DiskResourceColumnModel getColumnModel() {
        return cm;
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
    public void onFolderSelected(FolderSelectionEvent event) {
        final Folder selectedItem = event.getSelectedFolder();
        if(selectedItem == null
            || selectedItem.isFilter()) {
            return;
        }

        sm.clear();
        sm.setShowSelectAll(!(selectedItem instanceof DiskResourceQueryTemplate));

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

        if(selectedItem instanceof DiskResourceFavorite
            || selectedItem instanceof DiskResourceQueryTemplate) {
            reconfigureToSearchView();
        } else {
            reconfigureToListingView();
        }
    }

    @Override
    public void setSingleSelect() {
        grid.getSelectionModel().setSelectionMode(SINGLE);
        cm.setCheckboxColumnHidden(true);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        grid.ensureDebugId(baseID + DiskResourceModule.Ids.GRID);
        cm.ensureDebugId(baseID);
    }

    private void reconfigureToSearchView() {
        // display Path
        grid.getColumnModel().getColumn(2).setHidden(false);
        grid.getView().refresh(true);
    }

    private void reconfigureToListingView() {
        // hide Path.
        grid.getColumnModel().getColumn(2).setHidden(true);
        grid.getView().refresh(true);
    }

    @Override
    public void onBeforeLoad(BeforeLoadEvent<FolderContentsLoadConfig> event) {

    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
        updateSelectionCount(sm.getSelectedCount());

        fireEvent(new DiskResourceSelectionChangedEvent(event.getSelection()));
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

    @UiFactory
    DiskResourceColumnModel createColumnModel() {
        return new DiskResourceColumnModel(sm, appearance);
    }

    @UiHandler("pathField")
    void onPathFieldKeyPress(KeyPressEvent event){
        if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER
                && !Strings.isNullOrEmpty(pathField.getCurrentValue())) {
            String path = pathField.getCurrentValue();
            HasPath folderToSelect = CommonModelUtils.getInstance().createHasPathFromString(path);
            fireEvent(new FolderPathSelectedEvent(folderToSelect));
        }
    }

}