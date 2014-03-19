/**
 *
 */
package org.iplantc.de.diskResource.client.sharing.views;

import org.iplantc.de.client.models.sharing.DataSharing;
import org.iplantc.de.client.util.DiskResourceUtil;
import org.iplantc.de.resources.client.IplantResources;
import org.iplantc.de.resources.client.messages.I18N;

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
 * @author sriram
 *
 */
public class ShareBreakDownDialog extends Dialog {

    private Grid<DataSharing> grid;
    public ShareBreakDownDialog(List<DataSharing> shares) {
        init();

        ToolBar toolbar = new ToolBar();
        toolbar.setHeight(30);
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
        setPixelSize(400, 375);
        setHideOnButtonClick(true);
        setModal(true);
        setHeadingText(I18N.DISPLAY.whoHasAccess());
        buildGrid();
    }

	private Grid<DataSharing> buildGrid() {
		ListStore<DataSharing> store = new ListStore<DataSharing>(new DataSharingKeyProvider());
		ColumnModel<DataSharing> cm = buildColumnModel();
		GroupingView<DataSharing> view = new GroupingView<DataSharing>();
		view.groupBy(cm.getColumn(0));
        view.setAutoExpandColumn(cm.getColumn(0));
        view.setShowGroupedColumn(false);
        grid = new Grid<DataSharing>(store, cm);
		grid.setView(view);
		return grid;
	}

    private void loadGrid(List<DataSharing> shares) {
        grid.getStore().clear();
        grid.getStore().addAll(shares);
    }

    private TextButton buildGroupByUserButton() {
        TextButton button = new TextButton(I18N.DISPLAY.groupByUser());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GroupingView<DataSharing> view = (GroupingView<DataSharing>)grid.getView();
                view.groupBy(grid.getColumnModel().getColumn(0));

            }
        });
        button.setIcon(IplantResources.RESOURCES.share());
        return button;
    }

	private TextButton buildGroupByDataButton() {
        TextButton button = new TextButton(I18N.DISPLAY.groupByData());
        button.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GroupingView<DataSharing> view = (GroupingView<DataSharing>)grid.getView();
                view.groupBy(grid.getColumnModel().getColumn(1));

            }
        });
        button.setIcon(IplantResources.RESOURCES.folder());
        return button;
    }

	private ColumnModel<DataSharing> buildColumnModel() {
		List<ColumnConfig<DataSharing, ?>> configs = new ArrayList<ColumnConfig<DataSharing, ?>>();
		  DataSharingProperties props = GWT.create(DataSharingProperties.class);
		ColumnConfig<DataSharing, String> name = new ColumnConfig<DataSharing, String>(
               props.name());

        name.setHeader(I18N.DISPLAY.name());
        name.setWidth(120);

        ColumnConfig<DataSharing, String> diskRsc = new ColumnConfig<DataSharing, String>(
                new ValueProvider<DataSharing, String>() {

                    @Override
                    public String getValue(DataSharing object) {
                            return DiskResourceUtil.parseNameFromPath((object.getPath()));
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

        diskRsc.setHeader(I18N.DISPLAY.name());
        diskRsc.setWidth(120);
        ColumnConfig<DataSharing, String> permission = new ColumnConfig<DataSharing, String>(
                props.displayPermission());

        permission.setHeader(I18N.DISPLAY.permissions());
        permission.setWidth(80);
        configs.add(name);
        configs.add(diskRsc);
        configs.add(permission);
        return new ColumnModel<DataSharing>(configs);

	}



}
