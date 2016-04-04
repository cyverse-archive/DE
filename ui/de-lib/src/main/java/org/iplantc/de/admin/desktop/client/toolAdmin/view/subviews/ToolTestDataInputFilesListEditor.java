package org.iplantc.de.admin.desktop.client.toolAdmin.view.subviews;

import org.iplantc.de.admin.desktop.client.toolAdmin.ToolAdminView;
import org.iplantc.de.admin.desktop.shared.Belphegor;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.IsEditor;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.client.editor.ListStoreEditor;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

import java.util.ArrayList;
import java.util.List;


public class ToolTestDataInputFilesListEditor extends Composite
        implements IsEditor<Editor<List<String>>> {

    private GridInlineEditing<String> editing;
    private Grid<String> grid;
    private ListStoreEditor<String> listStoreEditor;

    @UiField ListStore<String> listStore;
    @UiField (provided = true) ToolAdminView.ToolAdminViewAppearance appearance;

    @Inject
    public ToolTestDataInputFilesListEditor(ToolAdminView.ToolAdminViewAppearance appearance) {
        this.appearance = appearance;
        listStore = getStringListStore();
        listStoreEditor = getListStoreEditor();
        listStore.setAutoCommit(true);

        List<ColumnConfig<String, ?>> columns = new ArrayList<>();

        ColumnConfig<String, String> inputFile = new ColumnConfig<>(getValueProvider(),
                                                                    appearance.toolTestDataInputFilesWidth(),
                                                                    appearance.toolTestDataInputFilesColumnLabel());

        columns.add(inputFile);
        ColumnModel<String> columnModel = new ColumnModel<>(columns);
        grid = getStringGrid(columnModel);
        editing = getStringGridInlineEditing();
        ColumnConfig<String, String> columnConfig = grid.getColumnModel().getColumn(0);
        final TextField editor = new TextField();
        editing.addEditor(columnConfig, editor);

        initWidget(grid);

    }

    GridInlineEditing<String> getStringGridInlineEditing() {
        return new GridInlineEditing<>(grid);
    }

    Grid<String> getStringGrid(ColumnModel<String> columnModel) {
        return new Grid<>(listStore, columnModel);
    }

    ListStoreEditor<String> getListStoreEditor() {
        return new ListStoreEditor<>(listStore);
    }

    ListStore<String> getStringListStore() {
        return new ListStore<>(getModelKeyProvider());
    }

    ModelKeyProvider<String> getModelKeyProvider() {
        return new ModelKeyProvider<String>() {
            @Override
            public String getKey(String item) {
                return item;
            }

        };
    }

    ValueProvider<String, String> getValueProvider() {
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

    @Override
    public Editor<List<String>> asEditor() {
        return listStoreEditor;
    }

    public void addToolTestDataInputFile() {
        String inputFile = "";

        editing.cancelEditing();

        listStore.add(0, inputFile);
        int row = listStore.indexOf(inputFile);
        editing.startEditing(new Grid.GridCell(row, 0));
    }

    public void deleteToolTestDataInputFile() {
        String deleteInput = grid.getSelectionModel().getSelectedItem();
        if (deleteInput != null) {
            listStore.remove(listStore.findModelWithKey(deleteInput));
        }
    }

    @Override
    protected void onEnsureDebugId(String baseID) {
        super.onEnsureDebugId(baseID);

        grid.ensureDebugId(baseID + Belphegor.ToolAdminIds.INPUT_FILES_GRID);
    }
}
