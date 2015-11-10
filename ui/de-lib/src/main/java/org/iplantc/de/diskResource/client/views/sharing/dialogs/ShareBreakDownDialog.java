package org.iplantc.de.diskResource.client.views.sharing.dialogs;

import org.iplantc.de.client.models.diskResources.PermissionValue;
import org.iplantc.de.client.models.sharing.Sharing;
import org.iplantc.de.client.sharing.SharingAppearance;
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
 * 
 * @author sriram, jstroot
 * 
 */
public class ShareBreakDownDialog extends Dialog {

    private final SharingAppearance appearance;
    private Grid<Sharing> grid;

    public ShareBreakDownDialog(final List<Sharing> shares) {
        this(shares, GWT.<SharingAppearance> create(SharingAppearance.class));
    }

    ShareBreakDownDialog(final List<Sharing> shares, final SharingAppearance appearance) {
        this.appearance = appearance;
        init();

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
        setPixelSize(appearance.shareBreakDownDlgWidth(), appearance.shareBreakDownDlgHeight());
        setHideOnButtonClick(true);
        setModal(true);
        setHeadingText(appearance.whoHasAccess());
        buildGrid();
    }

    private Grid<Sharing> buildGrid() {
        ListStore<Sharing> store = new ListStore<>(new DataSharingKeyProvider());
        ColumnModel<Sharing> cm = buildColumnModel();
        GroupingView<Sharing> view = new GroupingView<>();
        view.groupBy(cm.getColumn(0));
        view.setAutoExpandColumn(cm.getColumn(0));
        view.setShowGroupedColumn(false);
        grid = new Grid<>(store, cm);
        grid.setView(view);
        return grid;
    }

    private void loadGrid(List<Sharing> shares) {
        grid.getStore().clear();
        grid.getStore().addAll(shares);
    }

    private TextButton buildGroupByUserButton() {
        TextButton button = new TextButton(appearance.groupByUser());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GroupingView<Sharing> view = (GroupingView<Sharing>)grid.getView();
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
                GroupingView<Sharing> view = (GroupingView<Sharing>)grid.getView();
                view.groupBy(grid.getColumnModel().getColumn(1));

            }
        });
        button.setIcon(appearance.folderIcon());
        return button;
    }

    private ColumnModel<Sharing> buildColumnModel() {
        List<ColumnConfig<Sharing, ?>> configs = new ArrayList<>();
        DataSharingProperties props = GWT.create(DataSharingProperties.class);
        ColumnConfig<Sharing, String> name = new ColumnConfig<>(props.name());

        name.setHeader(appearance.nameColumnLabel());
        name.setWidth(appearance.shareBreakDownDlgNameColumnWidth());

        ColumnConfig<Sharing, String> diskRsc = new ColumnConfig<>(new ValueProvider<Sharing, String>() {

            @Override
            public String getValue(Sharing object) {
                return object.getName();
            }

            @Override
            public void setValue(Sharing object, String value) {
                // do nothing intentionally

            }

            @Override
            public String getPath() {
                return "";
            }
        });

        diskRsc.setHeader(appearance.nameColumnLabel());
        diskRsc.setWidth(appearance.shareBreakDownDlgNameColumnWidth());
        ColumnConfig<Sharing, PermissionValue> permission = new ColumnConfig<>(new ValueProvider<Sharing, PermissionValue>() {

            @Override
            public PermissionValue getValue(Sharing object) {
                return object.getDisplayPermission();
            }

            @Override
            public void setValue(Sharing object, PermissionValue value) {
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
