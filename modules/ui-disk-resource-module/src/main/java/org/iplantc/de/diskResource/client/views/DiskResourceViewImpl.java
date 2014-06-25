package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.HasId;
import org.iplantc.de.client.models.HasPath;
import org.iplantc.de.client.models.UserInfo;
import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.DiskResourceAutoBeanFactory;
import org.iplantc.de.client.models.diskResources.DiskResourceInfo;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.search.DiskResourceQueryTemplate;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.util.CommonModelUtils;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.tags.presenter.IplantTagListPresenter;
import org.iplantc.de.commons.client.tags.resources.CustomIplantTagResources;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.client.events.FolderSelectionEvent;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsLoadConfig;
import org.iplantc.de.diskResource.client.presenters.proxy.FolderContentsRpcProxy;
import org.iplantc.de.diskResource.client.search.events.DeleteSavedSearchEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.DataCollapseStyle;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Util;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent;
import com.sencha.gxt.data.shared.event.StoreDataChangeEvent.StoreDataChangeHandler;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.data.shared.loader.TreeLoader;
import com.sencha.gxt.dnd.core.client.DND.Operation;
import com.sencha.gxt.dnd.core.client.DragSource;
import com.sencha.gxt.dnd.core.client.DropTarget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Status;
import com.sencha.gxt.widget.core.client.button.IconButton.IconConfig;
import com.sencha.gxt.widget.core.client.button.ToolButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.LiveGridCheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.LiveGridView;
import com.sencha.gxt.widget.core.client.grid.LiveToolItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.tree.Tree;
import com.sencha.gxt.widget.core.client.tree.Tree.TreeNode;
import com.sencha.gxt.widget.core.client.tree.TreeView;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DiskResourceViewImpl extends Composite implements DiskResourceView, SelectionHandler<Folder>, SelectionChangedHandler<DiskResource> {

    private final class PathFieldKeyPressHandlerImpl implements KeyPressHandler {
        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && !Strings.isNullOrEmpty(pathField.getCurrentValue())) {
                String path = pathField.getCurrentValue();
                HasPath folderToSelect = CommonModelUtils.createHasPathFromString(path);
                presenter.setSelectedFolderByPath(folderToSelect);
            }
        }
    }

    private final class RemoveInfoTypeClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            presenter.resetInfoType();
        }
    }

    private final class TreeStoreDataChangeHandlerImpl implements StoreDataChangeHandler<Folder> {
        private final Tree<Folder, Folder> tree;

        private TreeStoreDataChangeHandlerImpl(Tree<Folder, Folder> tree) {
            this.tree = tree;
        }

        @Override
        public void onDataChange(StoreDataChangeEvent<Folder> event) {
            Folder folder = event.getParent();
            if (folder != null && treeStore.getAllChildren(folder) != null) {
                for (Folder f : treeStore.getAllChildren(folder)) {
                    if (f.isFilter()) {
                        TreeNode<Folder> tn = tree.findNode(f);
                        tree.getView().getTextElement(tn).setInnerHTML("<span style='color:red;font-style:italic;'>" + f.getName() + "</span>");
                    }
                }
            }
        }
    }

    private final class CustomTreeView extends TreeView<Folder> {

        @Override
        public void onTextChange(TreeNode<Folder> node, SafeHtml text) {
            Element textEl = getTextElement(node);
            if (textEl != null) {
                Folder folder = node.getModel();
                if (!folder.isFilter()) {
                    textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : text.asString());
                } else {
                    textEl.setInnerHTML(Util.isEmptyString(text.asString()) ? "&#160;" : "<span style='color:red;font-style:italic;'>" + text.asString() + "</span>");
                }
            }
        }
    }

    @UiTemplate("DiskResourceView.ui.xml")
    interface DiskResourceViewUiBinder extends UiBinder<Widget, DiskResourceViewImpl> {
    }

    private static DiskResourceViewUiBinder BINDER = GWT.create(DiskResourceViewUiBinder.class);
    private final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader;

    private Presenter presenter;

    DiskResourceViewToolbar toolbar;

    @UiField
    BorderLayoutContainer con;

    @UiField
    ContentPanel westPanel;

    @UiField(provided = true)
    Tree<Folder, Folder> tree;
    private final UserInfo userInfo;
    private final IplantDisplayStrings displayStrings;

    @UiField(provided = true)
    final TreeStore<Folder> treeStore;

    @UiField
    VerticalLayoutContainer centerPanel;

    @UiField
    Grid<DiskResource> grid;

    @UiField
    ColumnModel<DiskResource> cm;

    @UiField
    ListStore<DiskResource> listStore;

    @UiField
    LiveGridView<DiskResource> gridView;

    @UiField
    VerticalLayoutContainer detailsPanel;

    @UiField
    BorderLayoutData westData;
    @UiField
    BorderLayoutData centerData;
    @UiField
    BorderLayoutData eastData;
    @UiField
    BorderLayoutData northData;
    @UiField
    BorderLayoutData southData;

    @UiField
    VerticalLayoutData centerLayoutData;

    @UiField
    ToolBar pagingToolBar;

    @UiField
    ContentPanel centerCp;

    @UiField
    TextField pathField;

    private TreeLoader<Folder> treeLoader;

    private final LiveGridCheckBoxSelectionModel sm;

    private Status selectionStatus;

    private final DiskResourceAutoBeanFactory drFactory;

    @Inject
    public DiskResourceViewImpl(final Tree<Folder, Folder> tree,
                                final FolderContentsRpcProxy folderRpcProxy,
                                final DiskResourceViewToolbar viewToolbar,
                                final DiskResourceAutoBeanFactory factory,
                                final UserInfo userInfo,
                                final IplantDisplayStrings displayStrings) {
        this.tree = tree;
        this.toolbar = viewToolbar;
        this.userInfo = userInfo;
        this.displayStrings = displayStrings;
        this.treeStore = tree.getStore();
        this.drFactory = factory;
        tree.setView(new CustomTreeView());

        sm = new LiveGridCheckBoxSelectionModel();

        initWidget(BINDER.createAndBindUi(this));

        detailsPanel.setScrollMode(ScrollMode.AUTO);

        folderRpcProxy.init(centerCp.getHeader());
        gridLoader = new PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>>(folderRpcProxy);
        gridLoader.setReuseLoadConfig(true);
        gridLoader.setRemoteSort(true);
        grid.setLoader(gridLoader);
        grid.setSelectionModel(sm);
        grid.getSelectionModel().addSelectionChangedHandler(this);
        gridView.setCacheSize(500);
        initLiveToolbar();

        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tree.getSelectionModel().addSelectionHandler(this);

        treeStore.addStoreDataChangeHandler(new TreeStoreDataChangeHandlerImpl(tree));

        // by default no details to show...
        resetDetailsPanel();
        setGridEmptyText();
        addTreeCollapseButton();
        pathField.addKeyPressHandler(new PathFieldKeyPressHandlerImpl());

        con.setNorthWidget(toolbar, northData);
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                final FolderContentsLoadConfig loadConfig = new FolderContentsLoadConfig();
                loadConfig.setLimit(gridView.getCacheSize());
                gridLoader.useLoadConfig(loadConfig);
            }
        });
    }

    @Override
    public HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler handler) {
        return asWidget().addHandler(handler, DiskResourceSelectionChangedEvent.TYPE);
    }

    @Override
    public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
        updateSelectionCount(sm.getSelectedCount());

        asWidget().fireEvent(new DiskResourceSelectionChangedEvent(event.getSelection()));
        if (event.getSelection().isEmpty()) {
            resetDetailsPanel();
        }
    }

    @Override
    public void onSelection(SelectionEvent<Folder> event) {
        if (event.getSelectedItem() == null) {
            return;
        }
        if (!asWidget().isAttached()) {
            return;
        }

        final Folder selectedItem = event.getSelectedItem();
        if (!selectedItem.isFilter()) {
            asWidget().fireEvent(new FolderSelectionEvent(selectedItem));
            Scheduler.get().scheduleFinally(new ScheduledCommand() {

                @Override
                public void execute() {
                    if (selectedItem instanceof DiskResourceQueryTemplate) {
                        pathField.clear();
                    } else if (!selectedItem.getPath().equals(pathField.getCurrentValue())) {
                        pathField.setValue(selectedItem.getPath());
                    }
                }
            });

        } else {
            // Do not allow user to select Filtered folders.
            tree.getSelectionModel().deselect(selectedItem);
        }
    }

    private void initLiveToolbar() {
        grid.setLoadMask(true);
        LiveToolItem toolItem = new LiveToolItem(grid);
        toolItem.setWidth(150);

        selectionStatus = new Status();
        selectionStatus.setWidth(100);
        updateSelectionCount(0);

        pagingToolBar.add(toolItem);
        pagingToolBar.add(new FillToolItem());
        pagingToolBar.add(selectionStatus);

        pagingToolBar.addStyleName(ThemeStyles.get().style().borderTop());
        pagingToolBar.getElement().getStyle().setProperty("borderBottom", "none");
    }

    private void updateSelectionCount(int selectionCount) {
        selectionStatus.setText(selectionCount + " item(s)");
    }

    @Override
    public void loadFolder(Folder folder) {
        sm.clear();
        sm.setShowSelectAll(!(folder instanceof DiskResourceQueryTemplate));
        grid.getView().getHeader().refresh();

        if (folder instanceof DiskResourceQueryTemplate){
            // If the given query has not been saved, we need to deselect everything
            DiskResourceQueryTemplate searchQuery = (DiskResourceQueryTemplate)folder;
            if (!searchQuery.isSaved()) {
                deSelectNavigationFolder();
            }
        }
        gridLoader.getLastLoadConfig().setFolder(folder);
        gridLoader.getLastLoadConfig().setOffset(0);
        gridLoader.load();
    }

    private void addTreeCollapseButton() {
        westPanel.setCollapsible(false);
        DataCollapseStyle style = IplantResources.RESOURCES.getDataCollapseStyle();
        style.ensureInjected();
        ToolButton tool = new ToolButton(new IconConfig(style.collapse(), style.collapseHover()));
        tool.setId("idTreeCollapse");
        tool.setToolTip(I18N.DISPLAY.collapseAll());
        tool.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                tree.collapseAll();
            }
        });
        westPanel.getHeader().removeTool(westPanel.getHeader().getTool(0));
        westPanel.getHeader().addTool(tool);
    }

    @UiFactory
    LiveGridView<DiskResource> createLiveGridView() {
        // CORE-5723 KLUDGE for Firefox bug with LiveGridView row height calculation.
        // Always use a row height of 25 for now.
        LiveGridView<DiskResource> liveGridView = new LiveGridView<DiskResource>() {

            @Override
            protected void insertRows(int firstRow, int lastRow, boolean isUpdate) {
                super.insertRows(firstRow, lastRow, isUpdate);

                setRowHeight(25);
            }
        };

        liveGridView.setAutoFill(true);
        liveGridView.setForceFit(true);

        return liveGridView;
    }

    @UiFactory
    ListStore<DiskResource> createListStore() {
        DiskResourceModelKeyProvider keyProvider = new DiskResourceModelKeyProvider();
        ListStore<DiskResource> listStore2 = new ListStore<DiskResource>(keyProvider);

        return listStore2;
    }

    @UiFactory
    public ValueProvider<Folder, String> createValueProvider() {
        return new ValueProvider<Folder, String>() {

            @Override
            public String getValue(Folder object) {
                return object.getName();
            }

            @Override
            public void setValue(Folder object, String value) {}

            @Override
            public String getPath() {
                return "name"; //$NON-NLS-1$
            }
        };
    }

    @UiFactory
    ColumnModel<DiskResource> createColumnModel() {
        return new DiskResourceColumnModel(sm, displayStrings);
    }

    private DiskResourceColumnModel getDiskResourceColumnModel() {
        return (DiskResourceColumnModel)cm;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
        addFolderSelectedEventHandler(presenter);
        addDiskResourceSelectionChangedEventHandler(presenter);

        ((DiskResourceColumnModel)cm).addDiskResourceNameSelectedEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageSharingEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageMetadataEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addShareByDataLinkEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageFavoritesEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageCommentsEventHandler(presenter);
        toolbar.init(presenter, this);
        initDragAndDrop();
    }

    private void initDragAndDrop() {
        DiskResourceViewDnDHandler dndHandler = new DiskResourceViewDnDHandler(this, presenter);

        DropTarget gridDropTarget = new DropTarget(grid);
        gridDropTarget.setAllowSelfAsSource(true);
        gridDropTarget.setOperation(Operation.COPY);
        gridDropTarget.addDragEnterHandler(dndHandler);
        gridDropTarget.addDragMoveHandler(dndHandler);
        gridDropTarget.addDropHandler(dndHandler);

        DragSource gridDragSource = new DragSource(grid);
        gridDragSource.addDragStartHandler(dndHandler);

        DropTarget treeDropTarget = new DropTarget(tree);
        treeDropTarget.setAllowSelfAsSource(true);
        treeDropTarget.setOperation(Operation.COPY);
        treeDropTarget.addDragEnterHandler(dndHandler);
        treeDropTarget.addDragMoveHandler(dndHandler);
        treeDropTarget.addDropHandler(dndHandler);

        DragSource treeDragSource = new DragSource(tree);
        treeDragSource.addDragStartHandler(dndHandler);

    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolbar.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.MENU_BAR);
        grid.ensureDebugId(baseID + DiskResourceModule.Ids.GRID);
        tree.ensureDebugId(baseID + DiskResourceModule.Ids.NAVIGATION);
        ((DiskResourceColumnModel)cm).ensureDebugId(baseID);
    }

    @Override
    public void setTreeLoader(TreeLoader<Folder> treeLoader) {
        tree.setLoader(treeLoader);
        this.treeLoader = treeLoader;
    }

    @Override
    public Folder getSelectedFolder() {
        return tree.getSelectionModel().getSelectedItem();
    }

    @Override
    public Set<DiskResource> getSelectedDiskResources() {
        return Sets.newHashSet(grid.getSelectionModel().getSelectedItems());
    }

    @Override
    public TreeStore<Folder> getTreeStore() {
        return treeStore;
    }

    @Override
    public ListStore<DiskResource> getListStore() {
        return listStore;
    }

    @Override
    public boolean isLoaded(Folder folder) {
        TreeNode<Folder> findNode = tree.findNode(folder);
        return findNode.isLoaded();
    }

    @Override
    public void setDiskResources(Set<DiskResource> folderChildren) {
        grid.getStore().clear();
        grid.getStore().addAll(folderChildren);
    }

    @Override
    public void setWestWidgetHidden(boolean hideWestWidget) {
        westData.setHidden(hideWestWidget);
    }

    @Override
    public void setCenterWidgetHidden(boolean hideCenterWidget) {
        // If we are hiding the center widget, update west data to fill
        // available space.
        if (hideCenterWidget) {
            westData.setSize(1);
        }
        centerData.setHidden(hideCenterWidget);
    }

    @Override
    public void setEastWidgetHidden(boolean hideEastWidget) {
        eastData.setHidden(hideEastWidget);
    }

    @Override
    public void setNorthWidgetHidden(boolean hideNorthWidget) {
        northData.setHidden(hideNorthWidget);
    }

    @Override
    public void setSouthWidget(IsWidget widget) {
        southData.setHidden(false);
        con.setSouthWidget(widget, southData);
    }

    @Override
    public void setSouthWidget(IsWidget widget, double size) {
        southData.setHidden(false);
        southData.setSize(size);
        con.setSouthWidget(widget, southData);
    }

    @Override
    public void setSelectedFolder(Folder folder) {
        final Folder findModelWithKey = treeStore.findModelWithKey(folder.getId());
        if (findModelWithKey != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {

                @Override
                public void execute() {
                    tree.getSelectionModel().setSelection(Lists.newArrayList(findModelWithKey));
                    tree.scrollIntoView(findModelWithKey);
                }
            });
        }
    }

    @Override
    public void setSelectedDiskResources(List<? extends HasId> diskResourcesToSelect) {
        List<DiskResource> resourcesToSelect = Lists.newArrayList();
        for (HasId hi : diskResourcesToSelect) {
            DiskResource findModelWithKey = listStore.findModelWithKey(hi.getId());
            if (findModelWithKey != null) {
                resourcesToSelect.add(findModelWithKey);
            }
        }
        grid.getSelectionModel().select(resourcesToSelect, false);
    }

    @Override
    public void addFolder(Folder parent, Folder newChild) {
        treeStore.add(parent, newChild);
        if (presenter.getSelectedFolder() != null) {
            listStore.add(newChild);
            gridView.refresh();
        } else {
            Folder request = drFactory.folder().as();
            request.setPath(userInfo.getHomePath());
            presenter.setSelectedFolderByPath(request);
        }

    }

    @Override
    public void updateStore(DiskResource item) {
        grid.getStore().update(item);
    }

    @Override
    public Folder getFolderById(String folderId) {
        // KLUDGE Until the services are able to use GUIDs for folder IDs, first
        // check for a root folder
        // whose path matches folderId, since a root folder may now also be
        // listed under another root
        // (such as the user's home folder listed under "Shared With Me").
        if (treeStore.getRootItems() != null) {
            for (Folder root : treeStore.getRootItems()) {
                if (root.getPath().equals(folderId)) {
                    return root;
                }
            }
        }

        return treeStore.findModelWithKey(folderId);
    }

    @Override
    public Folder getFolderByPath(String path) {
        if (treeStore.getRootItems() != null) {
            for (Folder root : treeStore.getRootItems()) {
                if (root.getPath().equals(path)) {
                    return root;
                }
            }
        }

        return treeStore.findModelWithKey(path);
    }

    @Override
    public Folder getParentFolder(Folder selectedFolder) {
        return treeStore.getParent(selectedFolder);
    }

    @Override
    public void expandFolder(Folder folder) {
        tree.setExpanded(folder, true);
    }

    @Override
    public void deSelectDiskResources() {
        grid.getSelectionModel().deselectAll();
    }

    @Override
    public void deSelectNavigationFolder() {
        tree.getSelectionModel().deselectAll();
    }

    @Override
    public void refreshFolder(Folder folder) {
        if (folder == null || treeStore.findModel(folder) == null) {
            return;
        }

        removeChildren(folder);
        treeLoader.load(folder);
    }

    @Override
    public void removeChildren(Folder folder) {
        if (folder == null || treeStore.findModel(folder) == null) {
            return;
        }

        treeStore.removeChildren(folder);
        folder.setFolders(null);
    }

    @Override
    public DiskResourceViewToolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void mask(String loadingMask) {
        con.mask(loadingMask);
    }

    @Override
    public void unmask() {
        con.unmask();
        grid.unmask();
    }

    private void setGridEmptyText() {
        gridView.setEmptyText(I18N.DISPLAY.noItemsToDisplay());
    }

    public void updateDiskResource(DiskResource originalDr, DiskResource newDr) {
        // Check each store for for existence of original disk resource
        Folder treeStoreModel = treeStore.findModelWithKey(originalDr.getId());
        if (treeStoreModel != null) {

            // Grab original disk resource's parent, then remove original from
            // tree store
            Folder parentFolder = treeStore.getParent(treeStoreModel);
            treeStore.remove(treeStoreModel);

            treeStoreModel.setId(newDr.getId());
            treeStoreModel.setName(newDr.getName());
            treeStore.add(parentFolder, treeStoreModel);
        }

        DiskResource listStoreModel = listStore.findModelWithKey(originalDr.getId());

        if (listStoreModel != null) {
            listStore.remove(listStoreModel);
            listStore.add(newDr);
        }

    }

    @Override
    public boolean isViewTree(IsWidget widget) {
        return widget.asWidget() == tree;
    }

    @Override
    public boolean isViewGrid(IsWidget widget) {
        return widget.asWidget() == grid;
    }

    @Override
    public TreeNode<Folder> findTreeNode(Element el) {
        return tree.findNode(el);
    }

    @Override
    public Element findGridRow(Element el) {
        return grid.getView().findRow(el);
    }

    @Override
    public int findRowIndex(Element targetRow) {
        return grid.getView().findRowIndex(targetRow);
    }

    @Override
    public void setSingleSelect() {
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // Hide the checkbox column
        getDiskResourceColumnModel().setCheckboxColumnHidden(true);
    }

    @Override
    public void resetDetailsPanel() {
        detailsPanel.clear();
        FieldLabel fl = new FieldLabel();
        fl.setLabelWidth(detailsPanel.getOffsetWidth(true) - 10);
        fl.setLabelSeparator(""); //$NON-NLS-1$
        fl.setHTML(getDetailAsHtml(I18N.DISPLAY.noDetails(), false));
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(2);
        hp.add(fl);
        detailsPanel.add(hp);
    }

    private String getDetailAsHtml(String detail, boolean bolded) {
        if (bolded) {
            return "<span style='font-size:10px;'><b>" + detail + "</b> </span>"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            return "<span style='font-size:10px;padding-left:2px;'>" + detail + "</span>"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * Parses a timestamp string into a formatted date string and adds it to
     * this panel.
     * 
     */
    private HorizontalPanel getDateLabel(String label, Date date) {
        String value = ""; //$NON-NLS-1$

        if (date != null) {
            DateTimeFormat formatter = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_MEDIUM);

            value = formatter.format(date);
        }

        return getStringLabel(label, value);

    }

    private HorizontalPanel getStringLabel(String label, String value) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(label, true));
        panel.add(fl);

        FieldLabel fv = new FieldLabel();
        fl.setWidth(100);
        fv.setLabelSeparator(""); //$NON-NLS-1$
        fv.setHTML(getDetailAsHtml(value + "", false)); //$NON-NLS-1$
        panel.add(fv);

        return panel;
    }

    private HorizontalPanel getDirFileCount(String label, int file_count, int dir_count) {
        return getStringLabel(label, file_count + " / " + dir_count); //$NON-NLS-1$
    }

    /**
     * Add permissions detail
     * 
     */
    private HorizontalPanel getPermissionsLabel(String label, PermissionValue p) {
        return getStringLabel(label, p.toString());
    }

    private HorizontalPanel buildRow() {
        HorizontalPanel panel = new HorizontalPanel();
        panel.setHeight("25px"); //$NON-NLS-1$
        panel.setSpacing(1);
        return panel;
    }

    @Override
    public void updateDetails(String path, DiskResourceInfo info) {
        detailsPanel.clear();
        List<DiskResource> selection = grid.getSelectionModel().getSelectedItems();
        // guard race condition
        if (selection != null && selection.size() == 1) {
            Iterator<DiskResource> it = selection.iterator();
            DiskResource next = it.next();
            if (next.getId().equals(path)) {
                detailsPanel.add(getDateLabel(I18N.DISPLAY.lastModified(), info.getModified()));
                detailsPanel.add(getDateLabel(I18N.DISPLAY.createdDate(), info.getCreated()));
                detailsPanel.add(getPermissionsLabel(I18N.DISPLAY.permissions(), info.getPermission()));
                if (!DiskResourceUtil.inTrash(next)) {
                    detailsPanel.add(getSharingLabel(I18N.DISPLAY.share(), info.getShareCount(), info.getPermission()));
                }
                if (info.getType().equalsIgnoreCase("file")) {
                    addFileDetails(info, !DiskResourceUtil.inTrash(next));

                } else {
                    addFolderDetails(info);
                }
            }

        }

        presenter.getTagsForSelectedResource();

    }

    @Override
    public void updateTags(List<IplantTag> tags) {
        CustomIplantTagResources r = com.google.gwt.core.shared.GWT.create(CustomIplantTagResources.class);
        detailsPanel.add(createSample(tags, "", r, true));
    }

    private Widget createSample(List<IplantTag> tags, String containerStyle, CustomIplantTagResources resources, boolean editable) {
        HorizontalPanel hp = new HorizontalPanel();
        // TagList
        SimplePanel boundaryBox = new SimplePanel();
        IplantTagListPresenter tagPresenter = createTagList(resources, editable, this.createOnFocusCmd(boundaryBox, containerStyle), this.createOnBlurCmd(boundaryBox, containerStyle));
        tagPresenter.buildTagCloudForSelectedResource(tags);
        boundaryBox.setWidget(tagPresenter.getTagListView());
        hp.add(boundaryBox);
        return hp;
    }

    private Command createOnFocusCmd(final SimplePanel boundaryBox, final String defaultStyle) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute("style", defaultStyle + " outline: -webkit-focus-ring-color auto 5px;");
            }
        };
    }

    private Command createOnBlurCmd(final SimplePanel boundaryBox, final String defaultStyle) {
        return new Command() {
            @Override
            public void execute() {
                boundaryBox.getElement().setAttribute("style", defaultStyle);
            }
        };
    }

    private IplantTagListPresenter createTagList(CustomIplantTagResources resources, boolean editable, Command onFocusCmd, Command onBlurCmd) {
        IplantTagListPresenter tagsPresenter;
        if (resources == null) {
            tagsPresenter = new IplantTagListPresenter(this);
        } else {
            tagsPresenter = new IplantTagListPresenter(this, resources);
        }
        tagsPresenter.setEditable(editable);
        tagsPresenter.setOnFocusCmd(onFocusCmd);
        tagsPresenter.setOnBlurCmd(onBlurCmd);

        return tagsPresenter;
    }

    private void addFolderDetails(DiskResourceInfo info) {
        detailsPanel.add(getDirFileCount(I18N.DISPLAY.files() + " / " + I18N.DISPLAY.folders(), //$NON-NLS-1$
                info.getFileCount(), info.getDirCount()));
    }

    private void addFileDetails(DiskResourceInfo info, boolean addViewerInfo) {
        detailsPanel.add(getStringLabel(I18N.DISPLAY.size(), DiskResourceUtil.formatFileSize(info.getSize() + ""))); //$NON-NLS-1$
        detailsPanel.add(getStringLabel("Type", info.getFileType()));
        detailsPanel.add(getInfoTypeLabel("Info-Type", info));
        if (addViewerInfo) {
            detailsPanel.add(getViewerInfo(info));
        }
    }

    private HorizontalPanel getViewerInfo(DiskResourceInfo info) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(displayStrings.sendTo(), true));
        panel.add(fl);
        IPlantAnchor link = null;
        String infoType = info.getInfoType();
        if (infoType != null && !infoType.isEmpty()) {
            JSONObject manifest = new JSONObject();
            manifest.put("info-type", new JSONString(infoType));
            if (DiskResourceUtil.isTreeTab(manifest)) {
                link = new IPlantAnchor(displayStrings.treeViewer(), 100, new TreeViewerInfoClickHandler());
            } else if (DiskResourceUtil.isGenomeVizTab(manifest)) {
                link = new IPlantAnchor(displayStrings.coge(), 100, new CogeViewerInfoClickHandler());
            } else if (DiskResourceUtil.isEnsemblVizTab(manifest)) {
                link = new IPlantAnchor(displayStrings.ensembl(), 100, new EnsemblViewerInfoClickHandler());
            }
        }
        if (link == null) {
            panel.add(new HTML("-"));
        } else {
            panel.add(link);
        }
        return panel;
    }

    private HorizontalPanel getSharingLabel(String label, int shareCount, PermissionValue permissions) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(label, true));
        panel.add(fl);
        if (permissions.equals(PermissionValue.own)) {
            IPlantAnchor link;
            if (shareCount == 0) {
                link = new IPlantAnchor(I18N.DISPLAY.nosharing(), 100, new SharingLabelClickHandler());
            } else {
                link = new IPlantAnchor("" + shareCount, 100, new SharingLabelClickHandler()); //$NON-NLS-1$
            }
            panel.add(link);
        } else {
            panel.add(new HTML("-"));
        }

        return panel;
    }

    private HorizontalPanel getInfoTypeLabel(String label, DiskResourceInfo info) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(label, true));
        panel.add(fl);
        String infoType = info.getInfoType();

        IPlantAnchor link;
        if (infoType != null && !infoType.isEmpty()) {
            if (info.getPermission().equals(PermissionValue.own) || info.getPermission().equals(PermissionValue.write)) {
                link = new IPlantAnchor(infoType, 60, new InfoTypeClickHandler(infoType));
                panel.add(link);
                Image rmImg = new Image(IplantResources.RESOURCES.deleteIcon());
                rmImg.addClickHandler(new RemoveInfoTypeClickHandler());
                rmImg.setTitle(I18N.DISPLAY.delete());
                rmImg.getElement().getStyle().setCursor(Cursor.POINTER);
                panel.add(rmImg);
            } else {
                FieldLabel infoLbl = new FieldLabel();
                infoLbl.setLabelSeparator("");
                infoLbl.setHTML(getDetailAsHtml(infoType, false));
                panel.add(infoLbl);
            }
        } else {
            if (info.getPermission().equals(PermissionValue.own) || info.getPermission().equals(PermissionValue.write)) {
                link = new IPlantAnchor("Select", 100, new InfoTypeClickHandler(""));
                panel.add(link);
            } else {
                FieldLabel infoLbl = new FieldLabel();
                infoLbl.setLabelSeparator("");
                infoLbl.setHTML("-");
                panel.add(infoLbl);
            }
        }

        return panel;
    }

    @Override
    public void maskDetailsPanel() {
        detailsPanel.mask(displayStrings.loadingMask());
    }

    @Override
    public void unmaskDetailsPanel() {
        detailsPanel.unmask();
    }

    private class InfoTypeClickHandler implements ClickHandler {

        private final String infoType;

        public InfoTypeClickHandler(String type) {
            this.infoType = type;
        }

        @Override
        public void onClick(ClickEvent arg0) {
            List<DiskResource> selection = grid.getSelectionModel().getSelectedItems();
            Iterator<DiskResource> it = selection.iterator();
            presenter.onInfoTypeClick(it.next(), infoType);
        }

    }

    private class SharingLabelClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            presenter.manageSelectedResourceCollaboratorSharing();

        }
    }

    private class TreeViewerInfoClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            presenter.sendSelectedResourcesToTreeViewer();
        }

    }

    private class CogeViewerInfoClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            presenter.sendSelectedResourcesToCoge();

        }

    }

    private class EnsemblViewerInfoClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            presenter.sendSelectedResourceToEnsembl();

        }
    }

    @Override
    public boolean isSelectAllChecked() {
        return sm.isSelectAllChecked();
    }

    @Override
    public int getTotalSelectionCount() {
        return sm.getSelectedItems().size();
    }

    @Override
    public HandlerRegistration addFolderSelectedEventHandler(FolderSelectionEvent.FolderSelectionEventHandler handler) {
        return asWidget().addHandler(handler, FolderSelectionEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteSavedSearchEventHandler(DeleteSavedSearchEvent.DeleteSavedSearchEventHandler handler) {
        return tree.addHandler(handler, DeleteSavedSearchEvent.TYPE);
    }

    @Override
    public void displayAndCacheDiskResourceInfo(String path, DiskResourceInfo info) {
        DiskResource dr = listStore.findModelWithKey(path);
        if (dr == null) {
            return;
        } else {
            dr.setDiskResourceInfo(info);
            updateDetails(path, info);
        }
    }

    @Override
    public void maskSendToCoGe() {
        toolbar.maskSendToCoGe();
    }

    @Override
    public void unmaskSendToCoGe() {
        toolbar.unmaskSendToCoGe();
    }

    @Override
    public void maskSendToEnsembl() {
        toolbar.maskSendToEnsembl();
    }

    @Override
    public void unmaskSendToEnsembl() {
        toolbar.unmaskSendToEnsembl();
    }

    @Override
    public void maskSendToTreeViewer() {
        toolbar.maskSendToTreeViewer();
    }

    @Override
    public void unmaskSendToTreeViewer() {
        toolbar.unmaskSendToTreeViewer();
    }

    @Override
    public void attachTag(IplantTag tag) {
        presenter.attachTag(tag);
    }

    @Override
    public void detachTag(IplantTag tag) {
        presenter.detachTag(tag);
    }

}
