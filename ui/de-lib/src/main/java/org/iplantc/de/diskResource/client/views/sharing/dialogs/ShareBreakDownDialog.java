package org.iplantc.de.diskResource.client.views.sharing.dialogs;

import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.DataSharing;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.diskResource.client.DataSharingView;
import org.iplantc.de.diskResource.client.model.DataSharingKeyProvider;
import org.iplantc.de.diskResource.client.model.DataSharingProperties;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.List;

/**
 * FIXME Implement appearance separate from DataSharingView.Appearance
 * @author sriram, jstroot
 * 
 */
public class ShareBreakDownDialog extends Dialog {

    private final DataSharingView.Appearance appearance;
    private Grid<DataSharing> grid;
    private final DiskResourceUtil diskResourceUtil;

    public ShareBreakDownDialog(final List<DataSharing> shares) {
        this(shares,
             GWT.<DataSharingView.Appearance> create(DataSharingView.Appearance.class));
    }

    ShareBreakDownDialog(final List<DataSharing> shares,
                         final DataSharingView.Appearance appearance) {
        this.appearance = appearance;
        init();
        diskResourceUtil = DiskResourceUtil.getInstance();

        ToolBar toolbar = new ToolBar();
        toolbar.setHeight(appearance.shareBreakDownDlgToolbarHeight());
        toolbar.add(buildGroupByUserButton());
        toolbar.add(buildGroupByDataButton());
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        container.add(toolbar);
        container.add(buildGrid());
        container.setScrollMode(ScrollMode.AUTOY);
        setWidget(container);
        loadGrid(shares);
    }

    private void init() {
        setPixelSize(appearance.shareBreakDownDlgWidth(),
                     appearance.shareBreakDownDlgHeight());
        setHideOnButtonClick(true);
        setModal(true);
        setHeadingText(appearance.whoHasAccess());
        buildGrid();
    }

    private Grid<DataSharing> buildGrid() {
        ListStore<DataSharing> store = new ListStore<>(new DataSharingKeyProvider());
        ColumnModel<DataSharing> cm = buildColumnModel();
        GroupingView<DataSharing> view = new GroupingView<>();
        view.groupBy(cm.getColumn(0));
        view.setAutoExpandColumn(cm.getColumn(0));
        view.setShowGroupedColumn(false);
        grid = new Grid<>(store, cm);
        grid.setView(view);
        return grid;
    }

    private void loadGrid(List<DataSharing> shares) {
        grid.getStore().clear();
        grid.getStore().addAll(shares);
    }

    private TextButton buildGroupByUserButton() {
        TextButton button = new TextButton(appearance.groupByUser());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GroupingView<DataSharing> view = (GroupingView<DataSharing>)grid.getView();
                view.groupBy(grid.getColumnModel().getColumn(0));

            }
        });
        button.setIcon(appearance.shareIcon());
        return button;
    }

    private TextButton buildGroupByDataButton() {
        TextButton button = new TextButton(appearance.groupByData());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GroupingView<DataSharing> view = (GroupingView<DataSharing>)grid.getView();
                view.groupBy(grid.getColumnModel().getColumn(1));

            }
        });
        button.setIcon(appearance.folderIcon());
        return button;
    }

    private ColumnModel<DataSharing> buildColumnModel() {
        List<ColumnConfig<DataSharing, ?>> configs = new ArrayList<>();
        DataSharingProperties props = GWT.create(DataSharingProperties.class);
        ColumnConfig<DataSharing, String> name = new ColumnConfig<>(props.name());

        name.setHeader(appearance.nameColumnLabel());
        name.setWidth(appearance.shareBreakDownDlgNameColumnWidth());

        ColumnConfig<DataSharing, String> diskRsc = new ColumnConfig<>(new ValueProvider<DataSharing, String>() {

            @Override
            public String getValue(DataSharing object) {
                return diskResourceUtil.parseNameFromPath((object.getPath()));
            }

            @Override
            public void setValue(DataSharing object, String value) {
                // do nothing intentionally

            }

            @Override
            public String getPath() {
                return "path";
            }
        });

        diskRsc.setHeader(appearance.nameColumnLabel());
        diskRsc.setWidth(appearance.shareBreakDownDlgNameColumnWidth());
        ColumnConfig<DataSharing, PermissionValue> permission = new ColumnConfig<>(new ValueProvider<DataSharing, PermissionValue>() {

            @Override
            public PermissionValue getValue(DataSharing object) {
                return object.getDisplayPermission();
            }

            @Override
            public void setValue(DataSharing object, PermissionValue value) {
                object.setDisplayPermission(value);

            }

            @Override
            public String getPath() {
                return "displayPermission";
            }
        });

        permission.setHeader(appearance.permissionsColumnLabel());
        permission.setWidth(appearance.shareBreakDownDlgPermissionColumnWidth());
        configs.add(name);
        configs.add(diskRsc);
        configs.add(permission);
        return new ColumnModel<>(configs);
    }

}
