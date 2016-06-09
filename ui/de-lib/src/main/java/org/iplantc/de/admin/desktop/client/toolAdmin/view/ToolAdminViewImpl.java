package org.iplantc.de.admin.desktop.client.toolAdmin.view;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.AddToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.DeleteToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.SaveToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.events.ToolSelectedEvent;
import org.iplantc.de.admin.desktop.client.toolAdmin.model.ToolProperties;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.cells.ToolAdminNameCell;
import org.iplantc.de.admin.desktop.client.toolAdmin.view.dialogs.ToolAdminDetailsDialog;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.commons.client.ErrorHandler;
import org.iplantc.de.shared.AsyncProviderWrapper;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.Comparator;
import java.util.List;

/**
 * @author aramsey
 */
public class ToolAdminViewImpl extends Composite implements ToolAdminView {

    interface ToolAdminViewImplUiBinder extends UiBinder<Widget, ToolAdminViewImpl> {

    }

    private final class NameFilter implements Store.StoreFilter<Tool> {

        private String filterText;

        @Override
        public boolean select(Store<Tool> store, Tool parent, Tool item) {
            return !Strings.isNullOrEmpty(filterText) && item.getName()
                                                                     .toLowerCase()
                                                                     .contains(filterText.toLowerCase());
        }

        public void setQuery(String query) {
            this.filterText = query;
        }
    }

    private final class ToolNameComparator implements Comparator<Tool> {

        @Override
        public int compare(Tool o1, Tool o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    private static ToolAdminViewImplUiBinder uiBinder = GWT.create(ToolAdminViewImplUiBinder.class);

    @UiField TextButton addButton;
    @UiField TextButton deleteButton;
    @UiField Grid<Tool> grid;
    @UiField TextField filterField;
    @UiField(provided = true) ListStore<Tool> listStore;
    @UiField(provided = true) ToolAdminViewAppearance appearance;
    @Inject AsyncProviderWrapper<ToolAdminDetailsDialog> toolDetailsDialog;

    private final ToolProperties toolProps;
    private final NameFilter nameFilter;

    @Inject
    public ToolAdminViewImpl(final ToolAdminViewAppearance appearance,
                             ToolProperties toolProps,
                             @Assisted ListStore<Tool> listStore) {
        this.appearance = appearance;
        this.toolProps = toolProps;
        this.listStore = listStore;
        initWidget(uiBinder.createAndBindUi(this));
        nameFilter = new NameFilter();
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        addButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.ADD);
        deleteButton.ensureDebugId(baseID + Belphegor.ToolAdminIds.DELETE);
        filterField.setId(baseID + Belphegor.ToolAdminIds.FILTER);
        grid.ensureDebugId(baseID + Belphegor.ToolAdminIds.GRID);
    }

    @Override
    public HandlerRegistration addAddToolSelectedEventHandler(AddToolSelectedEvent.AddToolSelectedEventHandler handler) {
        return addHandler(handler, AddToolSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addDeleteToolSelectedEventHandler(DeleteToolSelectedEvent.DeleteToolSelectedEventHandler handler) {
        return addHandler(handler, DeleteToolSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addSaveToolSelectedEventHandler(SaveToolSelectedEvent.SaveToolSelectedEventHandler handler) {
        return addHandler(handler, SaveToolSelectedEvent.TYPE);
    }

    @Override
    public HandlerRegistration addToolSelectedEventHandler(ToolSelectedEvent.ToolSelectedEventHandler handler) {
        return addHandler(handler, ToolSelectedEvent.TYPE);
    }

    @UiFactory
    ColumnModel<Tool> createColumnModel() {
        List<ColumnConfig<Tool, ?>> list = Lists.newArrayList();
        ColumnConfig<Tool, Tool> nameCol = new ColumnConfig<>(new IdentityValueProvider<Tool>("name"),
                                                                appearance.nameColumnWidth(),
                                                                appearance.nameColumnLabel());
        ColumnConfig<Tool, String> descriptionCol = new ColumnConfig<>(toolProps.description(),
                                                                       appearance.descriptionColumnWidth(),
                                                                       appearance.descriptionColumnLabel());
        ColumnConfig<Tool, String> locationCol = new ColumnConfig<>(toolProps.location(),
                                                                    appearance.locationColumnInfoWidth(),
                                                                    appearance.locationColumnInfoLabel());
        ColumnConfig<Tool, String> typeCol = new ColumnConfig<>(toolProps.type(),
                                                                appearance.typeColumnInfoWidth(),
                                                                appearance.typeColumnInfoLabel());
        ColumnConfig<Tool, String> attributionCol = new ColumnConfig<>(toolProps.attribution(),
                                                                       appearance.attributionColumnWidth(),
                                                                       appearance.attributionColumnLabel());
        ColumnConfig<Tool, String> versionCol = new ColumnConfig<>(toolProps.version(),
                                                                   appearance.versionColumnInfoWidth(),
                                                                   appearance.versionColumnInfoLabel());

        nameCol.setCell(new ToolAdminNameCell(this));
        nameCol.setComparator(new ToolNameComparator());
        list.add(nameCol);
        list.add(descriptionCol);
        list.add(locationCol);
        list.add(typeCol);
        list.add(attributionCol);
        list.add(versionCol);
        return new ColumnModel<>(list);
    }

    @Override
    public void editToolDetails(final Tool tool) {
        toolDetailsDialog.get(new AsyncCallback<ToolAdminDetailsDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(final ToolAdminDetailsDialog result) {
                result.show(tool);
                result.ensureDebugId(Belphegor.ToolAdminIds.TOOL_ADMIN_DIALOG);
                result.addSaveToolSelectedEventHandler(new SaveToolSelectedEvent.SaveToolSelectedEventHandler() {
                    @Override
                    public void onSaveToolSelected(SaveToolSelectedEvent event) {
                        fireEvent(event);
                        result.hide();
                        grid.getSelectionModel().deselect(grid.getSelectionModel().getSelectedItem());
                    }
                });
            }
        });
    }

    @UiHandler("addButton")
    void addButtonClicked(SelectEvent event) {
        toolDetailsDialog.get(new AsyncCallback<ToolAdminDetailsDialog>() {
            @Override
            public void onFailure(Throwable caught) {
                ErrorHandler.post(caught);
            }

            @Override
            public void onSuccess(final ToolAdminDetailsDialog result) {
                result.show();
                result.ensureDebugId(Belphegor.ToolAdminIds.TOOL_ADMIN_DIALOG);
                result.addSaveToolSelectedEventHandler(new SaveToolSelectedEvent.SaveToolSelectedEventHandler() {
                    @Override
                    public void onSaveToolSelected(SaveToolSelectedEvent event) {
                        AddToolSelectedEvent addToolSelectedEvent = new AddToolSelectedEvent(event.getTool());
                        fireEvent(addToolSelectedEvent);
                        result.hide();
                    }
                });
            }
        });
    }

    @UiHandler("deleteButton")
    void deleteButtonClicked(SelectEvent event) {
        final Tool tool = grid.getSelectionModel().getSelectedItem();
        if (tool != null) {
            final ConfirmMessageBox deleteMsgBox =
                    new ConfirmMessageBox("Confirm", "Delete " + tool.getName() + "?");
            deleteMsgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                @Override
                public void onDialogHide(DialogHideEvent event) {
                    if (event.getHideButton().equals(Dialog.PredefinedButton.OK) || event.getHideButton()
                                                                                         .equals(Dialog.PredefinedButton.YES)) {
                        DeleteToolSelectedEvent deleteToolSelectedEvent =
                                new DeleteToolSelectedEvent(tool);
                        fireEvent(deleteToolSelectedEvent);
                        deleteMsgBox.hide();
                    }
                }
            });
            deleteMsgBox.show();
            setDeleteMsgDebugId(deleteMsgBox);

        }
    }

    private void setDeleteMsgDebugId(ConfirmMessageBox deleteMsgBox) {
        deleteMsgBox.getButton(Dialog.PredefinedButton.YES).ensureDebugId(
                Belphegor.ToolAdminIds.CONFIRM_DELETE + Belphegor.ToolAdminIds.YES);
        deleteMsgBox.getButton(Dialog.PredefinedButton.NO).ensureDebugId(Belphegor.ToolAdminIds.CONFIRM_DELETE + Belphegor.ToolAdminIds.NO);
    }

    public void toolSelected(Tool tool) {
        ToolSelectedEvent toolSelectedEvent =
                new ToolSelectedEvent(tool);
        fireEvent(toolSelectedEvent);
    }

    @UiHandler("filterField")
    void onFilterValueChanged(ValueChangeEvent<String> event) {
        listStore.removeFilters();
        final String query = Strings.nullToEmpty(event.getValue());
        if (query.isEmpty()) {
            return;
        }
        nameFilter.setQuery(query);
        listStore.addFilter(nameFilter);
    }

}
