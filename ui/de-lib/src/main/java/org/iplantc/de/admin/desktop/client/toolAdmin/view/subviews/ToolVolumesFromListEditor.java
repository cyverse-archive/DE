package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.client.toolAdmin.model.ToolVolumesFromProperties;
import org.iplantc.de.admin.desktop.shared.Belphegor;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolVolumesFrom;
import org.iplantc.de.commons.client.validators.UrlValidator;
import org.iplantc.de.commons.client.widgets.EmptyStringValueChangeHandler;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.AbstractGridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.ClicksToEdit;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;

import java.util.ArrayList;
import java.util.List;


public class ToolVolumesFromListEditor extends Composite
        implements IsEditor<Editor<List<ToolVolumesFrom>>> {

    private GridEditing<ToolVolumesFrom> editing;
    private Grid<ToolVolumesFrom> grid;
    private static final String TOOL_VOLUMES_FROM_MODEL_KEY = "model_key";
    private int unique_volumes_from_id;
    private ListStoreEditor<ToolVolumesFrom> listStoreEditor;

    @UiField
    ListStore<ToolVolumesFrom> listStore;
    @UiField(provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance;


    @Inject
    public ToolVolumesFromListEditor(ToolAdminView.ToolAdminViewAppearance appearance,
                                     ToolVolumesFromProperties toolVolumesFromProperties) {
        this.appearance = appearance;
        listStore = new ListStore<>(new ModelKeyProvider<ToolVolumesFrom>() {
            @Override
            public String getKey(ToolVolumesFrom item) {
                return getVolumesFromTag(item);
            }
        });
        listStoreEditor = new ListStoreEditor<>(listStore);
        listStore.setAutoCommit(true);

        ColumnConfig<ToolVolumesFrom, String> name = new ColumnConfig<>(toolVolumesFromProperties.name(),
                                                                        appearance.containerVolumesFromNameWidth(),
                                                                        appearance.containerVolumesFromNameLabel());
        ColumnConfig<ToolVolumesFrom, String> namePrefix = new ColumnConfig<>(toolVolumesFromProperties.namePrefix(),
                                                                              appearance.containerVolumesFromNamePrefixWidth(),
                                                                              appearance.containerVolumesFromNamePrefixLabel());
        ColumnConfig<ToolVolumesFrom, String> tag = new ColumnConfig<>(toolVolumesFromProperties.tag(),
                                                                       appearance.containerVolumesFromTagWidth(),
                                                                       appearance.containerVolumesFromTagLabel());
        ColumnConfig<ToolVolumesFrom, String> url = new ColumnConfig<>(toolVolumesFromProperties.url(),
                                                                       appearance.containerVolumesFromURLWidth(),
                                                                       appearance.containerVolumesFromURLLabel());
        ColumnConfig<ToolVolumesFrom, Boolean> readOnly = new ColumnConfig<>(toolVolumesFromProperties.readOnly(),
                                                                             appearance.containerVolumesFromReadOnlyWidth(),
                                                                             appearance.containerVolumesFromReadOnlyLabel());


        List<ColumnConfig<ToolVolumesFrom, ?>> columns = new ArrayList<>();
        columns.add(name);
        columns.add(namePrefix);
        columns.add(tag);
        columns.add(url);
        columns.add(readOnly);
        ColumnModel<ToolVolumesFrom> cm = new ColumnModel<>(columns);

        grid = new Grid<>(listStore, cm);
        grid.setHeight(100);

        editing = new GridRowEditing<>(grid);
        enableGridEditing(name, namePrefix, tag, url, readOnly);
        editing.addCancelEditHandler(getCancelHandler());
        ((AbstractGridEditing<ToolVolumesFrom>)editing).setClicksToEdit(ClicksToEdit.TWO);

        initWidget(grid);
    }

    private CancelEditEvent.CancelEditHandler<ToolVolumesFrom> getCancelHandler() {
        return new CancelEditEvent.CancelEditHandler<ToolVolumesFrom>() {
            @Override
            public void onCancelEdit(CancelEditEvent<ToolVolumesFrom> event) {
                int cancelRow = event.getEditCell().getRow();
                if (listStore.get(cancelRow).getName() == null &&
                    listStore.get(cancelRow).getNamePrefix() == null) {
                    listStore.remove(cancelRow);
                }
            }
        };
    }

    private void enableGridEditing(ColumnConfig<ToolVolumesFrom, String> name,
                                   ColumnConfig<ToolVolumesFrom, String> namePrefix,
                                   ColumnConfig<ToolVolumesFrom, String> tag,
                                   ColumnConfig<ToolVolumesFrom, String> url,
                                   ColumnConfig<ToolVolumesFrom, Boolean> readOnly) {
        final TextField nameTextField = new TextField();
        nameTextField.setAllowBlank(false);
        editing.addEditor(name, nameTextField);

        final TextField namePrefixTextfield = new TextField();
        namePrefixTextfield.setAllowBlank(false);
        editing.addEditor(namePrefix, namePrefixTextfield);

        final TextField tagTextField = new TextField();
        tagTextField.addValueChangeHandler(new EmptyStringValueChangeHandler(tagTextField));
        editing.addEditor(tag, tagTextField);

        final TextField urlTextField = new TextField();
        urlTextField.addValidator(new UrlValidator());
        urlTextField.addValueChangeHandler(new EmptyStringValueChangeHandler(urlTextField));
        editing.addEditor(url, urlTextField);

        final CheckBox readOnlyCheckBox = new CheckBox();
        editing.addEditor(readOnly, readOnlyCheckBox);
    }

    @Override
    public Editor<List<ToolVolumesFrom>> asEditor() {
        return listStoreEditor;
    }

    public void addVolumesFrom() {
        ToolAutoBeanFactory factory = GWT.create(ToolAutoBeanFactory.class);
        ToolVolumesFrom volumesFrom = factory.getVolumesFrom().as();
        getVolumesFromTag(volumesFrom);

        editing.cancelEditing();
        listStore.add(0, volumesFrom);
        int row = listStore.indexOf(volumesFrom);
        editing.startEditing(new Grid.GridCell(row, 0));
    }

    private String getVolumesFromTag(ToolVolumesFrom volumesFrom){
        if (volumesFrom != null){
            final AutoBean<ToolVolumesFrom> volumesFromAutoBean = AutoBeanUtils.getAutoBean(volumesFrom);
            String currentTag = volumesFromAutoBean.getTag(TOOL_VOLUMES_FROM_MODEL_KEY);
            if (currentTag == null){
                volumesFromAutoBean.setTag(TOOL_VOLUMES_FROM_MODEL_KEY,
                                      String.valueOf(unique_volumes_from_id++));
            }
            return volumesFromAutoBean.getTag(TOOL_VOLUMES_FROM_MODEL_KEY);
        }
        return "";
    }

    public void deleteVolumesFrom() {
        ToolVolumesFrom deleteVolumesFrom = grid.getSelectionModel().getSelectedItem();
        if (deleteVolumesFrom != null) {
            listStore.remove(listStore.findModelWithKey(getVolumesFromTag(deleteVolumesFrom)));
        }
    }

    public boolean isValid(){
        for (ToolVolumesFrom volumesFrom : listStore.getAll()){
            if (Strings.isNullOrEmpty(volumesFrom.getName()) || Strings.isNullOrEmpty(volumesFrom.getNamePrefix())){
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        grid.ensureDebugId(baseID + Belphegor.ToolAdminIds.CONTAINER_VOLUMES_FROM_GRID);
    }
}
