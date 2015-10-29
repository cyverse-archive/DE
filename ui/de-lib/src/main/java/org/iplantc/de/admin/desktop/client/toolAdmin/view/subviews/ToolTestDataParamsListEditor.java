package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.uibinder.client.UiField;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import java.util.ArrayList;
import java.util.List;


public class ToolTestDataParamsListEditor extends Composite
        implements IsEditor<Editor<List<String>>> {

    private GridEditing<String> editing;
    private Grid<String> grid;

    @UiField
    ListStore<String> listStore;
    @UiField(provided = true)
    ToolAdminView.ToolAdminViewAppearance appearance =
            GWT.create(ToolAdminView.ToolAdminViewAppearance.class);

    public ToolTestDataParamsListEditor() {
        listStore = new ListStore<>(getModelKeyProvider());

        List<ColumnConfig<String, ?>> columns = new ArrayList<>();

        ColumnConfig<String, String> param =
                new ColumnConfig<String, String>(getValueProvider(), appearance.toolTestDataParamsWidth(), appearance.toolTestDataParamsLabel());

        columns.add(param);
        ColumnModel<String> columnModel = new ColumnModel<>(columns);

        grid = new Grid<String>(listStore, columnModel);
        editing = new GridInlineEditing<>(grid);
        ColumnConfig<String, String> columnConfig = grid.getColumnModel().getColumn(0);
        final TextField editor = new TextField();
        editing.addEditor(columnConfig, editor);

        initWidget(grid);

    }

    private ValueProvider<String, String> getValueProvider() {
        return new ValueProvider<String, String>() {
            @Override
            public String getValue(String object) {
                return object;
            }

            @Override
            public void setValue(String object, String value) {
                listStore.remove(object);
                listStore.add(value);
            }

            @Override
            public String getPath() {
                return null;
            }
        };
    }

    private ModelKeyProvider<String> getModelKeyProvider() {
        return new ModelKeyProvider<String>() {
            @Override
            public String getKey(String item) {
                return item;
            }
        };
    }

    @Override
    public Editor<List<String>> asEditor() {
        return new ListStoreEditor<String>(listStore);
    }

    public List<String> getTestDataParamsList() {

        return listStore.getAll();
    }

    public void addToolTestDataParam() {
        listStore.commitChanges();
        String param = "";

        editing.cancelEditing();

        listStore.add(0, param);
        int row = listStore.indexOf(param);
        editing.startEditing(new Grid.GridCell(row, 0));
    }

    public void deleteToolTestDataParam() {
        listStore.commitChanges();
        String deleteParam = grid.getSelectionModel().getSelectedItem();
        if (deleteParam != null) {
            listStore.remove(listStore.findModelWithKey(deleteParam));
        }
    }

}
