package org.iplantc.de.admin.desktop.client.toolAdmin.view;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.Tool;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.commons.client.views.dialogs.IPlantDialog;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.Style;
import com.sencha.gxt.core.client.dom.ScrollSupport;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

import java.util.List;

/**
 * Created by aramsey on 10/27/15.
 */


public class ToolAdminViewImpl extends Composite
        implements ToolAdminView, SelectionHandler<Tool> {

    interface ToolAdminViewImplUiBinder extends UiBinder<Widget, ToolAdminViewImpl> {

    }

    private final class NameFilter implements Store.StoreFilter<Tool> {

        private String filterText;

        @Override
        public boolean select(Store<Tool> store, Tool parent, Tool item) {
            if (Strings.nullToEmpty(filterText).isEmpty()) {
                return false;
            }
            return item.getName().toLowerCase().contains(filterText.toLowerCase());
        }

        public void setQuery(String query) {
            this.filterText = query;
        }
    }

    private static ToolAdminViewImplUiBinder uiBinder = GWT.create(ToolAdminViewImplUiBinder.class);

    @UiField
    TextButton addButton;
    @UiField
    Grid<Tool> grid;
    @UiField
    ListStore<Tool> listStore;
    @UiField(provided = true)
    ToolAdminViewAppearance appearance;

    private final ToolProperties toolProps;
    private final ToolAutoBeanFactory factory;
    private final NameFilter nameFilter;
    private ToolAdminView.Presenter presenter;

    @Inject
    public ToolAdminViewImpl(final ToolAdminViewAppearance appearance,
                             ToolProperties toolProps,
                             ToolAutoBeanFactory factory) {
        this.appearance = appearance;
        this.toolProps = toolProps;
        this.factory = factory;
        initWidget(uiBinder.createAndBindUi(this));
        nameFilter = new NameFilter();
        grid.getSelectionModel().addSelectionHandler(this);
        grid.getSelectionModel().setSelectionMode(Style.SelectionMode.SINGLE);
    }

    @UiFactory
    ListStore<Tool> createListStore() {
        final ListStore<Tool> listStore = new ListStore<>(toolProps.id());
        listStore.setEnableFilters(true);
        return listStore;
    }

    @UiFactory
    ColumnModel<Tool> createColumnModel() {
        List<ColumnConfig<Tool, ?>> list = Lists.newArrayList();
        ColumnConfig<Tool, String> nameCol = new ColumnConfig<>(toolProps.name(),
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

        list.add(nameCol);
        list.add(descriptionCol);
        list.add(locationCol);
        list.add(typeCol);
        list.add(attributionCol);
        list.add(versionCol);
        return new ColumnModel<>(list);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setToolList(List<Tool> tools) {
        listStore.replaceAll(tools);
    }

    @Override
    public void setToolDetails(final Tool tool) {
        final ToolAdminDetailsWindow detailsPanel = ToolAdminDetailsWindow.addToolDetails(factory);
        detailsPanel.edit(tool);

        final IPlantDialog dialogWindow = getIplantDialogWindow();

        final TextButton delete = getDeleteButton(tool, dialogWindow);
        dialogWindow.addButton(delete);
        dialogWindow.getOkButton().setText(appearance.dialogWindowUpdateBtnText());
        dialogWindow.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                Tool tool = detailsPanel.getTool();
                if (detailsPanel.isValid()){
                    presenter.updateTool(tool);
                    dialogWindow.hide();
                    grid.getSelectionModel().deselect(grid.getSelectionModel().getSelectedItem());
                }
                else{
                    AlertMessageBox alertMsgBox = new AlertMessageBox("Warning", appearance.completeRequiredFieldsError());
                    alertMsgBox.show();
                }

            }
        });
        addCancelButton(dialogWindow);

        addDetailsPanel(detailsPanel, dialogWindow);

        dialogWindow.show();
    }

    @UiHandler("addButton")
    void addButtonClicked(SelectEvent event) {
        final ToolAdminDetailsWindow detailsPanel = ToolAdminDetailsWindow.addToolDetails(factory);

        final IPlantDialog dialogWindow = getIplantDialogWindow();

        dialogWindow.getOkButton().setText(appearance.dialogWindowUpdateBtnText());
        dialogWindow.addOkButtonSelectHandler(new SelectEvent.SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                Tool tool = detailsPanel.getTool();
                if (detailsPanel.isValid()) {
                    presenter.addTool(tool);
                    dialogWindow.hide();
                }
                else{
                    AlertMessageBox alertMsgBox = new AlertMessageBox("Warning", appearance.completeRequiredFieldsError());
                    alertMsgBox.show();
                }
            }
        });
        addCancelButton(dialogWindow);

        addDetailsPanel(detailsPanel, dialogWindow);

        dialogWindow.show();
    }

    private void addDetailsPanel(ToolAdminDetailsWindow detailsPanel, IPlantDialog dialogWindow) {
        FlowLayoutContainer container = addScrollSupport();
        container.add(detailsPanel);
        dialogWindow.add(container);
    }

    private void addCancelButton(final IPlantDialog dialogWindow) {
        dialogWindow.addCancelButtonSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                dialogWindow.hide();
            }
        });
    }

    private FlowLayoutContainer addScrollSupport() {
        FlowLayoutContainer container = new FlowLayoutContainer();
        container.getScrollSupport().setScrollMode(ScrollSupport.ScrollMode.AUTO);
        return container;
    }

    private TextButton getDeleteButton(final Tool tool,
                                       final IPlantDialog dialogWindow) {
        final TextButton delete = new TextButton();
        delete.setText(appearance.dialogWindowDeleteBtnText());
        delete.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ConfirmMessageBox deleteMsgBox =
                        new ConfirmMessageBox("Confirm", "Delete " + tool.getName() + "?");
                deleteMsgBox.addDialogHideHandler(new DialogHideEvent.DialogHideHandler() {
                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if (event.getHideButton().equals(Dialog.PredefinedButton.OK)
                            || event.getHideButton().equals(Dialog.PredefinedButton.YES)) {
                            presenter.deleteTool(tool);
                            deleteMsgBox.hide();
                            dialogWindow.hide();
                        }
                    }
                });
                deleteMsgBox.show();
            }
        });
        return delete;
    }

    @Override
    public void deleteTool(String toolId) {
        listStore.remove(listStore.findModelWithKey(toolId));
    }


    @Override
    public void onSelection(SelectionEvent event) {
        presenter.getToolDetails(grid.getSelectionModel().getSelectedItem());
    }

    private IPlantDialog getIplantDialogWindow() {
        final IPlantDialog dialogWindow = new IPlantDialog();
        dialogWindow.setHideOnButtonClick(false);
        dialogWindow.setHeadingText(appearance.dialogWindowName());
        dialogWindow.setResizable(true);
        dialogWindow.setPixelSize(1000, 500);
        return dialogWindow;
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
