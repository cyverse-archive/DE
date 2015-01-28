package org.iplantc.de.diskResource.client.views;

import org.iplantc.de.client.models.diskResources.DiskResource;
import org.iplantc.de.client.models.diskResources.File;
import org.iplantc.de.client.models.diskResources.Folder;
import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.tags.IplantTag;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.commons.client.widgets.IPlantAnchor;
import org.iplantc.de.diskResource.client.DiskResourceView;
import org.iplantc.de.diskResource.client.GridView;
import org.iplantc.de.diskResource.client.NavigationView;
import org.iplantc.de.diskResource.client.events.DiskResourceSelectionChangedEvent;
import org.iplantc.de.diskResource.share.DiskResourceModule;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.IplantDisplayStrings;
import org.iplantc.de.tags.client.TagsView;
import org.iplantc.de.tags.client.gin.factory.TagListPresenterFactory;

import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
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
import com.google.inject.assistedinject.Assisted;
import com.google.web.bindery.autobean.shared.Splittable;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * FIXME Factor out appearance. This class is not testable in it's current form.
 * FIXME Factor out details panel.
 *
 * @author jstroot, sriram, psarando
 */
public class DiskResourceViewImpl extends Composite implements DiskResourceView {

/*    private final class PathFieldKeyPressHandlerImpl implements KeyPressHandler {
        @Override
        public void onKeyPress(KeyPressEvent event) {
            if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER && !Strings.isNullOrEmpty(pathField.getCurrentValue())) {
                String path = pathField.getCurrentValue();
                HasPath folderToSelect = CommonModelUtils.getInstance().createHasPathFromString(path);
                navigationPresenter.setSelectedFolder(folderToSelect);
            }
        }
    }
*/
    private final class RemoveInfoTypeClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            presenter.resetInfoType();
        }
    }

    @UiTemplate("DiskResourceView.ui.xml")
    interface DiskResourceViewUiBinder extends UiBinder<Widget, DiskResourceViewImpl> {
    }

    private static DiskResourceViewUiBinder BINDER = GWT.create(DiskResourceViewUiBinder.class);
//    private final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader;

    private final DiskResourceUtil diskResourceUtil;
    private final GridView.Presenter gridViewPresenter;
//    private final NavigationView.Presenter navigationPresenter;
    private final TagListPresenterFactory tagListPresenterFactory;
    private Presenter presenter;

    DiskResourceViewToolbar toolbar;

    private final IplantDisplayStrings displayStrings;

    @UiField BorderLayoutContainer con;
//    @UiField VerticalLayoutContainer centerPanel;
//    @UiField Grid<DiskResource> grid;
//    @UiField ColumnModel<DiskResource> cm;
//    @UiField ListStore<DiskResource> listStore;
//    @UiField LiveGridView<DiskResource> gridView;
    @UiField VerticalLayoutContainer detailsPanel;
    @UiField BorderLayoutData westData;
    @UiField BorderLayoutData centerData;
    @UiField BorderLayoutData eastData;
    @UiField BorderLayoutData northData;
    @UiField BorderLayoutData southData;

    @UiField VerticalLayoutData centerLayoutData;
//    @UiField ToolBar pagingToolBar;
//    @UiField ContentPanel centerCp;
//    @UiField TextField pathField;
    @UiField(provided = true) NavigationView navigationView;
    @UiField(provided = true) GridView centerGridView;

//    private final LiveGridCheckBoxSelectionModel sm;

//    private Status selectionStatus;

    private TagsView.Presenter tagPresenter;

    Logger LOG = Logger.getLogger("DRV");

    @Inject
    DiskResourceViewImpl(final DiskResourceViewToolbar viewToolbar,
                         final IplantDisplayStrings displayStrings,
                         final DiskResourceUtil diskResourceUtil,
                         final TagListPresenterFactory tagListPresenterFactory,
                         @Assisted final DiskResourceView.Presenter presenter,
                         @Assisted final NavigationView.Presenter navigationPresenter,
                         @Assisted final GridView.Presenter gridViewPresenter) {
//                         @Assisted final PagingLoader<FolderContentsLoadConfig, PagingLoadResult<DiskResource>> gridLoader) {
//        this.navigationPresenter = navigationPresenter;
        this.navigationView = navigationPresenter.getView();
        this.centerGridView = gridViewPresenter.getView();
        this.gridViewPresenter = gridViewPresenter;
        this.toolbar = viewToolbar;
        this.displayStrings = displayStrings;
        this.diskResourceUtil = diskResourceUtil;
        this.tagListPresenterFactory = tagListPresenterFactory;
        this.presenter = presenter;
//        this.gridLoader = gridLoader;
//        sm = new LiveGridCheckBoxSelectionModel();

        initWidget(BINDER.createAndBindUi(this));


/*        ((DiskResourceColumnModel)cm).addDiskResourceNameSelectedEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageSharingEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageMetadataEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addShareByDataLinkEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageFavoritesEventHandler(presenter);
        ((DiskResourceColumnModel)cm).addManageCommentsEventHandler(presenter);*/
        toolbar.init(presenter, this);
//        initDragAndDrop();

        detailsPanel.setScrollMode(ScrollMode.AUTO);

/*        this.gridLoader.addBeforeLoadHandler(this);
        grid.setLoader(gridLoader);
        grid.setSelectionModel(sm);
        grid.getSelectionModel().addSelectionChangedHandler(this);
        gridView.setCacheSize(500);
        gridView.setAutoExpandColumn(grid.getColumnModel().getColumn(1));
        initLiveToolbar();
        */

//        navigationPresenter.getView().addFolderSelectedEventHandler(this);

        // by default no details to show...
        resetDetailsPanel();
//        setGridEmptyText();
//        pathField.addKeyPressHandler(new PathFieldKeyPressHandlerImpl());

        con.setNorthWidget(toolbar, northData);
/*        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                final FolderContentsLoadConfig loadConfig = new FolderContentsLoadConfig();
                loadConfig.setLimit(gridView.getCacheSize());
                gridLoader.useLoadConfig(loadConfig);
            }
        });*/
    }

/*    @Override
    public HandlerRegistration addDiskResourceSelectionChangedEventHandler(DiskResourceSelectionChangedEvent.DiskResourceSelectionChangedEventHandler handler) {
        return asWidget().addHandler(handler, DiskResourceSelectionChangedEvent.TYPE);
    }
    */

    @Override
    public void onDiskResourceSelectionChanged(DiskResourceSelectionChangedEvent event) {
        if (event.getSelection().isEmpty()) {
            resetDetailsPanel();
        }
    }

/*    @Override
    public void onSelectionChanged(SelectionChangedEvent<DiskResource> event) {
//        updateSelectionCount(sm.getSelectedCount());

//        asWidget().fireEvent(new DiskResourceSelectionChangedEvent(event.getSelection()));
        if (event.getSelection().isEmpty()) {
            resetDetailsPanel();
        }
    }
    */

/*    @Override
    public void onFolderSelected(FolderSelectionEvent event) {
        final Folder selectedItem = event.getSelectedFolder();
        if (selectedItem == null
            || selectedItem.isFilter()) {
            return;
        }
        if (!asWidget().isAttached()) {
            return;
        }

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
    } */


/*    @Override
    public void onBeforeLoad(BeforeLoadEvent<FolderContentsLoadConfig> event) {
        if(navigationPresenter.getSelectedFolder() == null){
            return;
        }
        final Folder folderToBeLoaded = event.getLoadConfig().getFolder();

        // If the loaded contents are not the contents of the currently selected folder, then cancel the load.
        if(!folderToBeLoaded.getId().equals(navigationPresenter.getSelectedFolder().getId())){
            event.setCancelled(true);
        }
    }
    */

/*    private void initLiveToolbar() {
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
*/

/*    private void updateSelectionCount(int selectionCount) {
        selectionStatus.setText(selectionCount + " item(s)");
    } */

/*    @Override
    public HasSafeHtml getCenterHeader() {
        return centerCp.getHeader();
    }*/

/*    @Override
    public void loadFolder(Folder folder) {
        sm.clear();
        sm.setShowSelectAll(!(folder instanceof DiskResourceQueryTemplate));
        // grid.getView().getHeader().refresh();

        if (folder instanceof DiskResourceQueryTemplate){
            // If the given query has not been saved, we need to deselect everything
            DiskResourceQueryTemplate searchQuery = (DiskResourceQueryTemplate)folder;
            if (!searchQuery.isSaved()) {
                navigationPresenter.deSelectAll();
            }
        }

        gridLoader.getLastLoadConfig().setFolder(folder);
        gridLoader.getLastLoadConfig().setOffset(0);

        gridLoader.load();

        if (folder instanceof DiskResourceFavorite || folder instanceof DiskResourceQueryTemplate) {
            reconfigureToSearchView();
        } else {
            reconfigureToListingView();
        }
    }
    */

/*    @UiFactory
    LiveGridView<DiskResource> createLiveGridView() {
        // CORE-5723 KLUDGE for Firefox bug with LiveGridView row height calculation.
        // Always use a row height of 25 for now.

        return new LiveGridView<DiskResource>() {

            @Override
            protected void insertRows(int firstRow, int lastRow, boolean isUpdate) {
                super.insertRows(firstRow, lastRow, isUpdate);

                setRowHeight(25);
            }
        };
    }
    */

/*    @UiFactory
    ListStore<DiskResource> createListStore() {
        return new ListStore<>(new DiskResourceModelKeyProvider());
    }
    */

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

/*    @UiFactory
    ColumnModel<DiskResource> createColumnModel() {
        return new DiskResourceColumnModel(sm, displayStrings);
    }
    */

    /*private DiskResourceColumnModel getDiskResourceColumnModel() {
        return (DiskResourceColumnModel)cm;
    }*/

/*    private void initDragAndDrop() {
        DiskResourceViewDnDHandler dndHandler = new DiskResourceViewDnDHandler(this, presenter);

        DropTarget gridDropTarget = new DropTarget(grid);
        gridDropTarget.setAllowSelfAsSource(true);
        gridDropTarget.setOperation(Operation.COPY);
        gridDropTarget.addDragEnterHandler(dndHandler);
        gridDropTarget.addDragMoveHandler(dndHandler);
        gridDropTarget.addDropHandler(dndHandler);

        DragSource gridDragSource = new DragSource(grid);
        gridDragSource.addDragStartHandler(dndHandler);

    }
    */

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);
        toolbar.asWidget().ensureDebugId(baseID + DiskResourceModule.Ids.MENU_BAR);
//        grid.ensureDebugId(baseID + DiskResourceModule.Ids.GRID);
//        ((DiskResourceColumnModel)cm).ensureDebugId(baseID);
    }

/*    @Override
    public Set<DiskResource> getSelectedDiskResources() {
        return Sets.newHashSet(grid.getSelectionModel().getSelectedItems());
    } */

/*    @Override
    public ListStore<DiskResource> getListStore() {
        return listStore;
    }
    */

/*    @Override
    public void setDiskResources(Set<DiskResource> folderChildren) {
        grid.getStore().clear();
        grid.getStore().addAll(folderChildren);
    }
    */

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

/*    @Override
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
    */

    /*@Override
    public void updateStore(DiskResource item) {
        grid.getStore().update(item);
        gridView.refresh();
    }*/

/*    @Override
    public void deSelectDiskResources() {
        grid.getSelectionModel().deselectAll();
    }
    */

    @Override
    public List<DiskResource> getSelectedDiskResources() {
        return gridViewPresenter.getSelectedDiskResources();
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
//        grid.unmask();
    }

/*    private void setGridEmptyText() {
        gridView.setEmptyText(displayStrings.noItemsToDisplay());
    }
    */

/*    @Override
    public boolean isViewGrid(IsWidget widget) {
        return widget.asWidget() == grid;
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
*/

    @Override
    public void resetDetailsPanel() {
        detailsPanel.clear();
        FieldLabel fl = new FieldLabel();
        fl.setLabelWidth(detailsPanel.getOffsetWidth(true) - 10);
        fl.setLabelSeparator(""); //$NON-NLS-1$
        fl.setHTML(getDetailAsHtml(displayStrings.noDetails(), false));
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
    public void updateDetails(DiskResource info) {
        detailsPanel.clear();
        List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
        // guard race condition
        if (selection != null && selection.size() == 1) {
            Iterator<DiskResource> it = selection.iterator();
            DiskResource next = it.next();
            if (next.getId().equals(info.getId())) {
                detailsPanel.add(getDateLabel(displayStrings.lastModified(), info.getLastModified()));
                detailsPanel.add(getDateLabel(displayStrings.createdDate(), info.getDateCreated()));
                detailsPanel.add(getPermissionsLabel(displayStrings.permissions(), info.getPermission()));
                if (!diskResourceUtil.inTrash(next)) {
                    detailsPanel.add(getSharingLabel(displayStrings.share(),
                                                     info.getShareCount(),
                                                     info.getPermission()));
                }
                if (info instanceof File) {
                    addFileDetails((File)info, !diskResourceUtil.inTrash(next));

                } else {
                    addFolderDetails((Folder)info);
                }
            }

        }

        presenter.getTagsForSelectedResource();
        detailsPanel.add(createTagView("", true, true));
    }

    @Override
    public void updateTags(List<IplantTag> tags) {
        tagPresenter.buildTagCloudForSelectedResource(tags);
    }

    private Widget createTagView(String containerStyle,
                                 boolean editable,
                                 boolean removable) {
        HorizontalPanel hp = new HorizontalPanel();
        SimplePanel boundaryBox = new SimplePanel();
        if (tagPresenter == null) {
            tagPresenter = createTagListPresenter(editable,
                                                  removable,
                                                  createOnFocusCmd(boundaryBox, containerStyle),
                                                  createOnBlurCmd(boundaryBox, containerStyle));

        }
        tagPresenter.removeAll();
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

    private TagsView.Presenter createTagListPresenter(boolean editable,
                                                      boolean removeable,
                                                      Command onFocusCmd,
                                                      Command onBlurCmd) {
        TagsView.Presenter tagsPresenter = tagListPresenterFactory.createTagListPresenter(this);
        tagsPresenter.setEditable(editable);
        tagsPresenter.setRemovable(removeable);
        tagsPresenter.setOnFocusCmd(onFocusCmd);
        tagsPresenter.setOnBlurCmd(onBlurCmd);

        return tagsPresenter;
    }

    private void addFolderDetails(Folder info) {
        detailsPanel.add(getDirFileCount(displayStrings.files() + " / " + displayStrings.folders(), //$NON-NLS-1$
                info.getFileCount(), info.getDirCount()));
    }

    private void addFileDetails(File info, boolean addViewerInfo) {
        detailsPanel.add(getStringLabel(displayStrings.size(), diskResourceUtil.formatFileSize(info.getSize() + ""))); //$NON-NLS-1$
        detailsPanel.add(getStringLabel("Type", info.getContentType()));
        detailsPanel.add(getInfoTypeLabel("Info-Type", info));
        if (addViewerInfo) {
            detailsPanel.add(getViewerInfo(info));
        }
    }

    private HorizontalPanel getViewerInfo(File info) {
        HorizontalPanel panel = buildRow();
        FieldLabel fl = new FieldLabel();
        fl.setWidth(100);
        fl.setHTML(getDetailAsHtml(displayStrings.sendTo(), true));
        panel.add(fl);
        IPlantAnchor link = null;
        String infoType = info.getInfoType();
        if (infoType != null && !infoType.isEmpty()) {
            Splittable manifest = diskResourceUtil.createInfoTypeSplittable(infoType);
            if (diskResourceUtil.isTreeTab(manifest)) {
                link = new IPlantAnchor(displayStrings.treeViewer(), 100, new TreeViewerInfoClickHandler());
            } else if (diskResourceUtil.isGenomeVizTab(manifest)) {
                link = new IPlantAnchor(displayStrings.coge(), 100, new CogeViewerInfoClickHandler());
            } else if (diskResourceUtil.isEnsemblVizTab(manifest)) {
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
                link = new IPlantAnchor(displayStrings.nosharing(), 100, new SharingLabelClickHandler());
            } else {
                link = new IPlantAnchor("" + shareCount, 100, new SharingLabelClickHandler()); //$NON-NLS-1$
            }
            panel.add(link);
        } else {
            panel.add(new HTML("-"));
        }

        return panel;
    }

    private HorizontalPanel getInfoTypeLabel(String label, File info) {
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
                rmImg.setTitle(displayStrings.delete());
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
            List<DiskResource> selection = gridViewPresenter.getSelectedDiskResources();
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

/*    @Override
    public boolean isSelectAllChecked() {
        return sm.isSelectAllChecked();
    }
    */

/*    @Override
    public int getTotalSelectionCount() {
        return sm.getSelectedItems().size();
    }
*/

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

/*    private void reconfigureToSearchView() {
        // display Path
        grid.getColumnModel().getColumn(2).setHidden(false);
        grid.getView().refresh(true);
    }

    private void reconfigureToListingView() {
        // hide Path.
        grid.getColumnModel().getColumn(2).setHidden(true);
        grid.getView().refresh(true);
    } */

    @Override
    public void selectTag(IplantTag tag) {
        LOG.fine("tag selected ==>" + tag.getValue());
        presenter.doSearchTaggedWithResources(Sets.newHashSet(tag));
    }

}
