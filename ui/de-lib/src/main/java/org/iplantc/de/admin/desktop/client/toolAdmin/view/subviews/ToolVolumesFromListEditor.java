package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.client.models.tool.ToolAutoBeanFactory;
import org.iplantc.de.client.models.tool.ToolVolumesFrom;
import org.iplantc.de.commons.client.validators.UrlValidator;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.uibinder.client.UiField;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import java.util.ArrayList;
import java.util.List;


public class ToolVolumesFromListEditor extends Composite
        implements IsEditor<Editor<List<ToolVolumesFrom>>> {

    private GridEditing<ToolVolumesFrom> editing;
    private Grid<ToolVolumesFrom> grid;
    private static final String TOOL_VOLUMES_FROM_MODEL_KEY = "model_key";
    private int unique_volumes_from_id;

    @UiField
    ListStore<ToolVolumesFrom> listStore;
    @UiField(provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance =
            GWT.create(ToolAdminView.ToolAdminViewAppearance.class);


    interface ToolVolumesFromProperties extends PropertyAccess<ToolVolumesFrom> {

        ValueProvider<ToolVolumesFrom, String> name();

        ValueProvider<ToolVolumesFrom, String> namePrefix();

        ValueProvider<ToolVolumesFrom, String> tag();

        ValueProvider<ToolVolumesFrom, String> url();

        ValueProvider<ToolVolumesFrom, Boolean> readOnly();
    }

    private static final ToolVolumesFromProperties ToolVolumesFromProperties =
            GWT.create(ToolVolumesFromProperties.class);

    public ToolVolumesFromListEditor() {
        listStore = new ListStore<ToolVolumesFrom>(new ModelKeyProvider<ToolVolumesFrom>() {
            @Override
            public String getKey(ToolVolumesFrom item) {
                return getVolumesFromTag(item);
            }
        });

        ColumnConfig<ToolVolumesFrom, String> name = new ColumnConfig<ToolVolumesFrom, String>(
                ToolVolumesFromProperties.name(),
                appearance.containerVolumesFromNameWidth(),
                appearance.containerVolumesFromNameLabel());
        ColumnConfig<ToolVolumesFrom, String> namePrefix = new ColumnConfig<ToolVolumesFrom, String>(
                ToolVolumesFromProperties.namePrefix(),
                appearance.containerVolumesFromNamePrefixWidth(),
                appearance.containerVolumesFromNamePrefixLabel());
        ColumnConfig<ToolVolumesFrom, String> tag = new ColumnConfig<ToolVolumesFrom, String>(
                ToolVolumesFromProperties.tag(),
                appearance.containerVolumesFromTagWidth(),
                appearance.containerVolumesFromTagLabel());
        ColumnConfig<ToolVolumesFrom, String> url = new ColumnConfig<ToolVolumesFrom, String>(
                ToolVolumesFromProperties.url(),
                appearance.containervolumesFromURLWidth(),
                appearance.containerVolumesFromURLLabel());
        ColumnConfig<ToolVolumesFrom, Boolean> readOnly = new ColumnConfig<ToolVolumesFrom, Boolean>(
                ToolVolumesFromProperties.readOnly(),
                appearance.containerVolumesFromReadOnlyWidth(),
                appearance.containerVolumesFromReadOnlyLabel());


        List<ColumnConfig<ToolVolumesFrom, ?>> columns =
                new ArrayList<ColumnConfig<ToolVolumesFrom, ?>>();
        columns.add(name);
        columns.add(namePrefix);
        columns.add(tag);
        columns.add(url);
        columns.add(readOnly);
        ColumnModel<ToolVolumesFrom> cm = new ColumnModel<ToolVolumesFrom>(columns);

        grid = new Grid<ToolVolumesFrom>(listStore, cm);

        editing = new GridInlineEditing<ToolVolumesFrom>(grid);
        enableGridEditing(name, namePrefix, tag, url, readOnly);

        initWidget(grid);
    }

    private void enableGridEditing(ColumnConfig<ToolVolumesFrom, String> name,
                                   ColumnConfig<ToolVolumesFrom, String> namePrefix,
                                   ColumnConfig<ToolVolumesFrom, String> tag,
                                   ColumnConfig<ToolVolumesFrom, String> url,
                                   ColumnConfig<ToolVolumesFrom, Boolean> readOnly) {
        final TextField nameTextField = new TextField();
        editing.addEditor(name, nameTextField);

        final TextField namePrefixTextfield = new TextField();
        editing.addEditor(namePrefix, namePrefixTextfield);

        final TextField tagTextField = new TextField();
        editing.addEditor(tag, tagTextField);

        final TextField urlTextField = new TextField();
        urlTextField.addValidator(new UrlValidator());
        editing.addEditor(url, urlTextField);

        final CheckBox readOnlyCheckBox = new CheckBox();
        editing.addEditor(readOnly, readOnlyCheckBox);

        editing.addCompleteEditHandler(new CompleteEditEvent.CompleteEditHandler<ToolVolumesFrom>() {
            @Override
            public void onCompleteEdit(CompleteEditEvent<ToolVolumesFrom> event) {
                String name = nameTextField.getCurrentValue();
                String namePrefix = namePrefixTextfield.getCurrentValue();
                String tag = tagTextField.getCurrentValue();
                String url = urlTextField.getCurrentValue();
                Boolean readOnly = readOnlyCheckBox.getValue();
                ToolVolumesFrom volumesFrom = listStore.get(0);
                volumesFrom.setName(name);
                volumesFrom.setNamePrefix(namePrefix);
                volumesFrom.setTag(tag);
                volumesFrom.setUrl(url);
                volumesFrom.setReadOnly(readOnly);
                listStore.update(volumesFrom);
            }
        });
    }

    @Override
    public Editor<List<ToolVolumesFrom>> asEditor() {
        return new ListStoreEditor<>(listStore);
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

    public List<ToolVolumesFrom> getVolumesFromList() {
        List<ToolVolumesFrom> volumesFromList = Lists.newArrayList();
        removeDisallowedId(volumesFromList);
        return volumesFromList;
    }

    private void removeDisallowedId(List<ToolVolumesFrom> volumesFromList) {
        for (ToolVolumesFrom volumeFrom : listStore.getAll()) {
            volumeFrom.setId(null);
            volumesFromList.add(volumeFrom);
        }
    }

}
